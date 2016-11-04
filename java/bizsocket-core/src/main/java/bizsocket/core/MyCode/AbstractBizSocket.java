package bizsocket.core.MyCode;

import bizsocket.core.One2ManyNotifyRouter;
import bizsocket.core.RequestQueue;
import bizsocket.core.ResponseHandler;
import bizsocket.core.cache.CacheManager;
import bizsocket.tcp.*;
import bizsocket.tcp.Request;

/**
 * Created by dxjf on 16/11/3.
 */
public class AbstractBizSocket implements Connection ,BizSocket{

    protected Configuration configuration;
    protected final SocketConnection socketConnection = null;
    protected final RequestQueue requestQueue = null;
    protected final One2ManyNotifyRouter one2ManyNotifyRouter = null;
    protected final CacheManager cacheManager = null;


    @Override
    public void connect() throws Exception {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Object request(Request request, ResponseHandler responseHandler) {
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
}
