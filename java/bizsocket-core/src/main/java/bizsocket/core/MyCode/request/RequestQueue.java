package bizsocket.core.MyCode.request;

import bizsocket.core.MyCode.SerialSignal.AbstractSerialContext;
import bizsocket.core.MyCode.SerialSignal.SerialSignal;
import bizsocket.core.MyCode.interceptor.InterceptorChain;
import bizsocket.core.MyCode.socket.AbstractBizSocket;
import bizsocket.core.ResponseHandler;
import bizsocket.logger.Logger;
import bizsocket.logger.LoggerFactory;
import bizsocket.tcp.ConnectionListener;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketListener;
import bizsocket.tcp.SocketConnection;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dxjf on 16/11/8.
 */
public class RequestQueue implements PacketListener, ConnectionListener {
    protected final Logger logger = LoggerFactory.getLogger(RequestQueue.class.getSimpleName());
    private final List<RequestContext> requestContextList = new RequestContextQueue();
    private final Set<SerialSignal> serialSignalSet = Collections.synchronizedSet(new HashSet<SerialSignal>());
    private final List<AbstractSerialContext> mSerialContexts = new CopyOnWriteArrayList<>();
    private InterceptorChain interceptorChain;
    private ResponseHandler globalNotifyHandler;
    private AbstractBizSocket abstractBizSocket;


    public RequestQueue(AbstractBizSocket bizSocket) {
        this.abstractBizSocket = bizSocket;
        interceptorChain = new InterceptorChain();

        bizSocket.getSocketConnection().addPacketListener(this);
        bizSocket.getSocketConnection().addConnectionListener(this);

    }

    public void addRequestContext(final RequestContext context) {
        if (context == null) {
            return;
        }
        //添加请求之前监听准备
        prepareContext(context);
        //判断请求队列中是否已经有了请求上下文
        boolean isExisted = requestContextList.contains(context);
        if (isExisted) {
            //是否是清空队列的请求
            if ((context.getFlags() & RequestContext.FLAG_CLEAR_QUEUE) != 0) {
                removeRequestContexts(requestContextList);
            }
            //是否是同意请求不能出现在队列中
            if ((context.getFlags() & RequestContext.FLAG_NOT_SUPPORT_REPEAT) != 0) {
                Collection<RequestContext> contexts = getRequestContext(new Filter() {
                    @Override
                    public boolean filter(RequestContext requestContext) {
                        return requestContext.getRequestCommand() == context.getRequestCommand();
                    }
                });

                if (!contexts.isEmpty()) {
                    //如果发现重复需要选择一种策略，
                    return;
                }
            }
            if ((context.getFlags() & RequestContext.FLAG_JUMP_QUOTE) != 0) {
                requestContextList.add(0, context);
            } else {
                requestContextList.add(context);
            }
            boolean result = getInterceptorChain().invokePostRequestHandle(context);
            if (result) {
                RequestInterceptedException exception = new RequestInterceptedException("请求被拦截");
                context.sendFailureMessage(context.getRequestCommand(), exception);
                removeRequestContext(context);
            } else {
                dealSerialSignal(context);
                sendRequest(context);
            }

        }


    }

    private void dealSerialSignal(final RequestContext context) {
        SerialSignal serialSignal = getSerialSignal(new SerialFilter() {
            @Override
            public boolean filter(Integer integer) {
                return integer == context.getRequestCommand();
            }
        });
        if (serialSignal != null) {
            AbstractSerialContext serialContext = getSerialContext(context);
            if (serialContext == null) {
                AbstractSerialContext abstractSerialContext = buildSerialContext(serialSignal, context);
                logger.debug("build serial request : " + serialContext);
                mSerialContexts.add(abstractSerialContext);
            } else {
                logger.debug("repeat request : " + serialContext);
            }
            return;
        }
        removeExpiredSerialContexts();
    }

    private void prepareContext(final RequestContext requestContext) {
        requestContext.setOnRequestTimeoutListener(new RequestContext.OnRequestTimeoutListener() {
            @Override
            public void onRequestTimeout(RequestContext context) {
                //请求超时
                RequestTimeoutException exception = new RequestTimeoutException("请求超时，请检查网络连接");
                context.sendFailureMessage(context.getRequestCommand(), exception);
                logger.debug("request timeout :" + requestContext);
                removeRequestContext(context);
            }
        });

    }

    private void recyclePacket(Packet packet) {
        if (packet == null) {
            return;
        }
        packet.recycle();
    }

    private void removeRequestContext(final RequestContext context) {
        removeRequestContexts(new ArrayList<RequestContext>() {{
            add(context);
        }});
    }

    public void removeRequestContexts(Collection<RequestContext> requestContexts) {
        if (requestContexts == null) {
            return;
        }
        requestContextList.removeAll(requestContexts);
        for (RequestContext context : requestContexts) {
            recyclePacket(context.getRequestPacket());
        }

    }

    public Collection<RequestContext> getRequestContext(Filter filter) {
        if (filter == null) {
            throw new RuntimeException("fileter can not be null");
        }
        CopyOnWriteArrayList<RequestContext> contexts = new CopyOnWriteArrayList<>();
        for (RequestContext requestContext : requestContextList) {
            if (filter.filter(requestContext)) {
                contexts.add(requestContext);
            }
        }
        return contexts;
    }

    private void sendRequest(RequestContext context) {
        if ((context.getFlags() | RequestContext.FLAG_CHECK_CONNECT_STATUS) == 0 ||
                (context.getFlags() | RequestContext.FLAG_CHECK_CONNECT_STATUS) != 0 &&
                        abstractBizSocket.getSocketConnection().isSocketClosed()) {
            if (sendPacket(context.getRequestPacket())) {
                context.setFlags(context.getFlags() | RequestContext.FLAG_REQUEST_ALERADY_SEND);
                onPacketSend(context);
            }
            if (context.getResponseHandler() == null) {
                removeRequestContext(context);
            }

        } else {
            //等待连接成功后
            logger.debug("connect closed , wait ...");
        }

    }

    private boolean sendPacket(Packet requestPacket) {
        if (abstractBizSocket.getSocketConnection() != null) {
            abstractBizSocket.getSocketConnection().sendPacket(requestPacket);
            return true;
        }
        return false;
    }

    private void onPacketSend(RequestContext context) {

    }

    //获取串行的context
    private AbstractSerialContext getSerialContext(RequestContext context) {
        for (AbstractSerialContext abstractBizSocket : mSerialContexts) {
            if (abstractBizSocket.getSerialSignal().getEntranceCommand() == context.getRequestCommand() &&
                    abstractBizSocket.getRequestPacketId() != null &&
                    abstractBizSocket.getRequestPacketId().equals(context)) {
                return abstractBizSocket;
            }
        }
        return null;
    }

    // 创建串行的context
    private AbstractSerialContext buildSerialContext(SerialSignal serialSignal, RequestContext context) {
        try {
            Constructor<? extends AbstractSerialContext> constructor = serialSignal.getSerialContextType().getConstructor(SerialSignal.class, RequestContext.class);
            AbstractSerialContext abstractSerialContext = constructor.newInstance(serialSignal, context);
            return abstractSerialContext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //移除过期的上下文
    private void removeExpiredSerialContexts() {
        List<AbstractSerialContext> preDelList = new ArrayList<AbstractSerialContext>();
        for (AbstractSerialContext serialContext : mSerialContexts) {
            if (serialContext.isExpired()) {
                preDelList.add(serialContext);
            }
        }
        mSerialContexts.removeAll(preDelList);
    }

    //获取串行信号
    private SerialSignal getSerialSignal(SerialFilter serialFilter) {
        if (serialFilter == null) {
            throw new RuntimeException("serialFilter can not be null");
        }
        for (SerialSignal serialSignal : serialSignalSet) {
            if (serialFilter.filter(serialSignal.getEntranceCommand())) {
                return serialSignal;
            }
        }
        return null;
    }

    public void addSerialSignal(SerialSignal serialSignal) {
        serialSignalSet.add(serialSignal);
    }

    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    @Override
    public void onSendSuccessful(Packet packet) {
        packet.onSendSuccessful();
    }

    @Override
    public void processPacket(Packet packet) {
        if (packet == null) {
            return;
        }
        packet.onReceiveFromServer();






    }

    @Override
    public void connected(SocketConnection connection) {
        excuteAllRequestContext();
    }

    private void excuteAllRequestContext() {
        Collection<RequestContext> prepareExecuteList = getRequestContext(new Filter() {
            @Override
            public boolean filter(RequestContext requestContext) {
                return (requestContext.getFlags() & RequestContext.FLAG_REQUEST_ALERADY_SEND) == 0;
            }
        });
        for (RequestContext requestContext : prepareExecuteList) {
            sendPacket(requestContext.getRequestPacket());
        }

    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    public void setGlobalNotifyHandler(ResponseHandler responseHandler) {
        this.globalNotifyHandler = responseHandler;
    }


    public interface SerialFilter {
        boolean filter(Integer integer);
    }

    public interface Filter {
        boolean filter(RequestContext requestContext);
    }
}
