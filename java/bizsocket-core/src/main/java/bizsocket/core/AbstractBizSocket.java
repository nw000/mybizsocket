package bizsocket.core;

import java.util.Map;
import bizsocket.tcp.Connection;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.SocketConnection;
import okio.ByteString;

/**
 * Created by tong on 16/10/4.
 */
public abstract class AbstractBizSocket implements Connection,BizSocket {
    protected Configuration configuration;
    protected final SocketConnection socketConnection;
    protected final RequestQueue requestQueue;
    protected final One2ManyNotifyRouter one2ManyNotifyRouter;
    protected final CacheManager cacheManager;

    protected abstract PacketFactory createPacketFactory();

    public AbstractBizSocket() {
        this(null);
    }

    public AbstractBizSocket(Configuration configuration) {
        this.configuration = configuration;
        socketConnection = createSocketConnection(createPacketFactory());
        one2ManyNotifyRouter = createMultiNotifyRouter();
        requestQueue = createRequestQueue(this);
        requestQueue.setGlobalNotifyHandler(new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet packet) {
                one2ManyNotifyRouter.route(command, packet);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        });
        cacheManager = createCacheManager();
        getInterceptorChain().addInterceptor(new CacheInterceptor(cacheManager));
    }

    @Override
    public void connect() throws Exception {
        socketConnection.setHostAddress(configuration.getHost(),configuration.getPort());
        socketConnection.connect();
    }

    @Override
    public void disconnect() {
        socketConnection.disconnect();
    }

    @Override
    public boolean isConnected() {
        return socketConnection.isConnected();
    }

    @Override
    public void request(Object tag, int command, ByteString requestBody, Map attach, ResponseHandler responseHandler) {
        RequestContext requestContext = buildRequestContext(tag,command, requestBody,attach,responseHandler);
        requestQueue.addRequestContext(requestContext);
        //requestContext.startTimeoutTimer();
    }

    @Override
    public void cancel(final Object tagOrResponseHandler) {
        requestQueue.removeRequestContexts(requestQueue.getRequestContext(new RequestQueue.Filter() {
            @Override
            public boolean filter(RequestContext context) {
                return context.getTag() == tagOrResponseHandler || context.getResponseHandler() == tagOrResponseHandler;
            }
        }));
    }

    @Override
    public void subscribe(Object tag, int cmd, ResponseHandler responseHandler) {
        one2ManyNotifyRouter.subscribe(tag,cmd, One2ManyNotifyRouter.FLAG_DEFAULT,responseHandler);
    }

    @Override
    public void unsubscribe(Object tagOrResponseHandler) {
        one2ManyNotifyRouter.unsubscribe(tagOrResponseHandler);
    }

    public SocketConnection createSocketConnection(final PacketFactory factory) {
        return new SocketConnection() {
            @Override
            protected PacketFactory createPacketFactory() {
                return factory;
            }

            @Override
            public void doReconnect(SocketConnection connection) {
                AbstractBizSocket.this.doReconnect();
            }
        };
    }

    public One2ManyNotifyRouter createMultiNotifyRouter() {
        return new DefaultOne2ManyNotifyRouter();
    }

    public RequestQueue createRequestQueue(AbstractBizSocket bizSocket) {
        return new RequestQueue(bizSocket);
    }

    public CacheManager createCacheManager() {
        return new CacheManager();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public PacketFactory getPacketFactory() {
        return socketConnection.getPacketFactory();
    }

    public SocketConnection getSocketConnection() {
        return socketConnection;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void addSerialSignal(SerialSignal serialSignal) {
        getRequestQueue().addSerialSignal(serialSignal);
    }

    public InterceptorChain getInterceptorChain() {
        return getRequestQueue().getInterceptorChain();
    }

    public void doReconnect() {
        getSocketConnection().reconnect();
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected RequestContext buildRequestContext(Object tag, int command, ByteString requestBody, Map attach, ResponseHandler responseHandler) {
        Packet packet = getPacketFactory().buildRequestPacket(command,requestBody,attach);
        if (attach != null) {
            String desc = (String) attach.get(Constants.KEY_DESCRIPTION);
            if (desc != null && desc.trim().length() > 0) {
                packet.setDescription(desc);
            }
        }

        RequestContext requestContext = new RequestContext();
        requestContext.setFlags(RequestContext.FLAG_REQUEST | RequestContext.FLAG_CHECK_CONNECT_STATUS);
        requestContext.setRequestCommand(command);
        requestContext.setRequestBody(requestBody);
        requestContext.setTag(tag);
        requestContext.setResponseHandler(responseHandler);
        requestContext.setRequestPacket(packet);
        requestContext.setReadTimeout(configuration.getReadTimeout());
        return requestContext;
    }
}

