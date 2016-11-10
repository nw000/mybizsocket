package bizsocket.core.MyCode.socket;

import bizsocket.core.MyCode.Configuration;
import bizsocket.core.MyCode.SerialSignal.SerialSignal;
import bizsocket.core.MyCode.interceptor.InterceptorChain;
import bizsocket.core.MyCode.notify.DefaultOne2ManyNotifyRouter;
import bizsocket.core.MyCode.request.RequestContext;
import bizsocket.core.MyCode.request.RequestQueue;
import bizsocket.core.One2ManyNotifyRouter;
import bizsocket.core.ResponseHandler;
import bizsocket.core.cache.CacheManager;
import bizsocket.tcp.*;
import okio.ByteString;

/**
 * Created by dxjf on 16/11/3.
 */
public abstract class AbstractBizSocket implements Connection, BizSocket {

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
        this.configuration = configuration; //配置信息
        this.socketConnection = createSocketConnection(createPacketFactory());//socket链接
        this.one2ManyNotifyRouter = createMultiNotifyRouter();//粘性通知，订阅路由
        this.requestQueue = createRequestQueue(this);// 请求队列
        requestQueue.setGlobalNotifyHandler(new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                one2ManyNotifyRouter.route(command, responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        });
        cacheManager = createCacheManager(); //创建缓存管理
    }


    @Override
    public void connect() throws Exception {
        socketConnection.setHostAddress(configuration.getHost(), configuration.getPort());
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
    public Object request(Request request, ResponseHandler responseHandler) {
        if (request == null) {
            throw new IllegalArgumentException("request can not be null !!!");
        }

        if (request.tag() == null) {
            request = request.newBuilder().tag(new Object()).build();
        }
        RequestContext requestContext = buildRequestContext(request, responseHandler);
        requestQueue.addRequestContext(requestContext);
        return request.tag();
    }

    @Override
    public void cancel(final Object tagOrResponseHandler) {
        requestQueue.removeRequestContexts(requestQueue.getRequestContext(new RequestQueue.Filter() {
            @Override
            public boolean filter(bizsocket.core.MyCode.request.RequestContext requestContext) {
                return requestContext.getTag() == tagOrResponseHandler | requestContext.getResponseHandler() == tagOrResponseHandler;
            }
        }));
    }

    @Override
    public void subscribe(Object tag, int cmd, ResponseHandler responseHandler) {
        one2ManyNotifyRouter.subscribe(tag, cmd, responseHandler);
    }

    @Override
    public void unsubscribe(Object tagOrResponseHandler) {
        one2ManyNotifyRouter.unsubscribe(tagOrResponseHandler);
    }

    private SocketConnection createSocketConnection(final PacketFactory packetFactory) {
        return new SocketConnection() {
            @Override
            protected PacketFactory createPacketFactory() {
                return packetFactory;
            }

            @Override
            public void doReconnect(SocketConnection connection) {
                super.doReconnect(connection);
                AbstractBizSocket.this.doReconnect();
            }
        };
    }

    private One2ManyNotifyRouter createMultiNotifyRouter() {
        return new DefaultOne2ManyNotifyRouter();
    }

    private RequestQueue createRequestQueue(AbstractBizSocket abstractBizSocket) {
        return new RequestQueue(abstractBizSocket);
    }

    private CacheManager createCacheManager() {
        return null;
    }

    public Configuration getConfiguration() {
        return configuration;
    }


    public PacketFactory getPacketFactory() {
        return getSocketConnection().getPacketFactory();
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

    private void doReconnect() {
        getSocketConnection().reconnect();
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public One2ManyNotifyRouter getOne2ManyNotifyRouter() {
        return one2ManyNotifyRouter;
    }

    private RequestContext buildRequestContext(Request request, ResponseHandler responseHandler) {
        Packet packet = getPacketFactory().getRequestPacket(request);
        String description = request.description();
        if (description != null && description.length() > 0) {
            packet.setDescription(description);
        }

        RequestContext requestContext = new RequestContext();
        requestContext.setFlags(requestContext.getFlags() | RequestContext.FLAG_CHECK_CONNECT_STATUS);
        requestContext.setReadTimeout(Configuration.DEFULT_READ_TIMEOUT);
        return requestContext;
    }
}
