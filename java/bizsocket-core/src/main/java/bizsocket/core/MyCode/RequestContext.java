package bizsocket.core.MyCode;

import bizsocket.logger.Logger;
import bizsocket.logger.LoggerFactory;
import bizsocket.tcp.Packet;
import okio.ByteString;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dxjf on 16/11/8.
 */
public class RequestContext implements ResponseHandler {
    private final Logger logger = LoggerFactory.getLogger(RequestContext.class.getSimpleName());

    /**
     * 请求已发送
     */
    public static final int FLAG_REQUEST_ALERADY_SEND = 1 << 1;

    /**
     * 发送时检查当前的链接状态
     */
    public static final int FLAG_CHECK_CONNECT_STATUS = 1 << 2;

    /**
     * 紧急发送的包需要优先插队执行
     */
    public static final int FLAG_JUMP_QUOTE = 1 << 3;

    /**
     * 写出这个包之前清空队列
     */
    public static final int FLAG_NOT_SUPPORT_QUOTE = 1 << 4;

    /**
     * 同一个请求不允许重复出现在队列中
     */

    public static final int FLAG_NOT_SUPPORT_REPEAT = 1 << 5;

    private Request request;
    /**
     * 请求包
     */
    private Packet requestPacket;

    /**
     * callback
     */
    private ResponseHandler responseHandler;


    /**
     * 状态机
     */
    private int flags = FLAG_CHECK_CONNECT_STATUS;
    private OnRequestTimeoutListener onRequestTimeoutListener;
    private Timer timer;
    private long readTimeout = Configuration.DEFULT_READ_TIMEOUT;


    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Object getTag() {
        return request.getTag();
    }

    public Packet getRequestPacket() {
        return requestPacket;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public int getRequestCommand() {
        return request.getCommand();
    }

    public void setOnRequestTimeoutListener(OnRequestTimeoutListener onRequestTimeoutListener) {
        this.onRequestTimeoutListener = onRequestTimeoutListener;
    }

    @Override
    public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
        if (responseHandler != null) {
            responseHandler.sendSuccessMessage(command, requestBody, responsePacket);
        }
    }

    @Override
    public void sendFailureMessage(int command, Throwable error) {
        if (responseHandler != null) {
            responseHandler.sendFailureMessage(command, error);
        }
    }

    public void onAddQueue() {
        startTimeOutTimer();
    }

    private void startTimeOutTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (onRequestTimeoutListener != null) {
                    onRequestTimeoutListener.onRequestTimeout(RequestContext.this);
                }
            }
        }, readTimeout * 10000);
    }

    public void onRemoveFromQueue() {
        logger.debug("remove from queue: " + toString());
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public ByteString getRequestBody() {
        return request.getBody();
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Map getAttach() {
        return request.getAttach();
    }

    public Request getRequest() {
        return request;
    }

    public void setRequestPacket(Packet requestPacket) {
        this.requestPacket = requestPacket;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "request=" + request +
                ", responseHandler=" + responseHandler +
                '}';
    }

    public static interface OnRequestTimeoutListener {
        void onRequestTimeout(RequestContext context);
    }
}
