package bizsocket.core.MyCode.socket;

import bizsocket.core.MyCode.Configuration;
import bizsocket.core.One2ManyNotifyRouter;
import bizsocket.core.RequestContext;
import bizsocket.core.RequestQueue;
import bizsocket.core.ResponseHandler;
import bizsocket.core.cache.CacheManager;
import bizsocket.tcp.*;
import bizsocket.tcp.Request;
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

    private RequestContext buildRequestContext(Request request, ResponseHandler responseHandler) {
        return null;
    }

    @Override
    public void cancel(Object tagOrResponseHandler) {

    }

    @Override
    public void subscribe(Object tag, int cmd, ResponseHandler responseHandler) {

    }

    @Override
    public void unsubscribe(Object tagOrResponseHandler) {

    }

    private CacheManager createCacheManager() {
        return null;
    }

    private RequestQueue createRequestQueue(AbstractBizSocket abstractBizSocket) {
        return null;
    }

    private One2ManyNotifyRouter createMultiNotifyRouter() {
        return null;
    }

    private SocketConnection createSocketConnection(PacketFactory packetFactory) {
        return null;
    }
}
