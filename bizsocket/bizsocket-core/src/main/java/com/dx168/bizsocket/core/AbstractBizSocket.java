package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Connection;
import com.dx168.bizsocket.tcp.Packet;
import com.dx168.bizsocket.tcp.PacketFactory;
import com.dx168.bizsocket.tcp.SocketConnection;
import java.util.Map;

/**
 * Created by tong on 16/10/4.
 */
public abstract class AbstractBizSocket implements Connection,BizSocket {
    protected final Configuration configuration;
    protected final SocketConnection socketConnection;
    protected final RequestQueue requestQueue;
    protected final MultiNotifyRouter multiNotifyRouter;
    protected final CacheManager cacheManager;

    protected abstract PacketFactory createPacketFactory();

    public AbstractBizSocket(Configuration configuration) {
        this.configuration = configuration;
        socketConnection = createSocketConnection(createPacketFactory());
        multiNotifyRouter = createMultiNotifyRouter();
        requestQueue = createRequestQueue(this);
        requestQueue.setGlobalNotifyHandler(new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, String params, Map<String, String> attach, Packet packet) {
                multiNotifyRouter.route(command, packet);
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
    public void request(Object tag, int command, String body, Map<String, String> attach, ResponseHandler responseHandler) {
        RequestContext requestContext = buildRequestContext(tag,command,body,attach,responseHandler);
        requestQueue.addRequestContext(requestContext);
        requestContext.startTimeoutTimer();
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
        multiNotifyRouter.subscribe(tag,cmd,MultiNotifyRouter.FLAG_DEFAULT,responseHandler);
    }

    @Override
    public void unsubscribe(Object tagOrResponseHandler) {
        multiNotifyRouter.unsubscribe(tagOrResponseHandler);
    }

    public SocketConnection createSocketConnection(final PacketFactory factory) {
        return new SocketConnection() {
            @Override
            protected PacketFactory createPacketFactory() {
                return factory;
            }
        };
    }

    public MultiNotifyRouter createMultiNotifyRouter() {
        return new DefaultMultiNotifyRouter();
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

    protected RequestContext buildRequestContext(Object tag, int command, String body, Map<String, String> attach, ResponseHandler responseHandler) {
        Packet packet = getPacketFactory().buildRequestPacket(command,body);
        RequestContext requestContext = new RequestContext();
        requestContext.addFlag(RequestContext.FLAG_REQUEST | RequestContext.FLAG_CHECK_CONNECT_STATUS);
        requestContext.setRequestCommand(command);
        requestContext.setRequestBody(body);
        requestContext.setTag(tag);
        requestContext.setResponseHandler(responseHandler);
        requestContext.setAttachInfo(attach);
        requestContext.setRequestPacket(packet);
        requestContext.setReadTimeout(configuration.getReadTimeout());
        return requestContext;
    }
}

