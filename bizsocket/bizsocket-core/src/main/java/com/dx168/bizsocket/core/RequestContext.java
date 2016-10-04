package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Packet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tong on 16/3/7.
 */
public class RequestContext implements ResponseHandler {
    /**
     * 加入请求队列
     */
    public static final int FLAG_REQUEST = 1 << 0;

    /**
     * 请求已发送
     */
    public static final int FLAG_REQUEST_ALREADY_SEND = 1 << 2;

    /**
     * 发送时是否检查当前连接状态
     */
    public static final int FLAG_CHECK_CONNECT_STATUS = 1 << 3;

    /**
     * 紧急的包需要优先插队执行
     */
    public static final int FLAG_JUMP_QUOTE = 1 << 4;

    /**
     * 写出这个包之前清空队列
     */
    public static final int FLAG_CLEAR_QUOTE = 1 << 5;

    /**
     * 同一个请求不允许重复出现在队列中
     */
    public static final int FLAG_NOT_SUPPORT_REPEAT = 1 << 6;

    /**
     * 状态机
     */
    private int flags = FLAG_REQUEST | FLAG_CHECK_CONNECT_STATUS;
    /**
     * 请求的tag
     */
    private Object tag;

    /**
     * 请求命令号
     */
    private int requestCommand;
    /**
     * 请求包
     */
    private Packet requestPacket;
    /**
     * callback
     */
    private ResponseHandler responseHandler;

    private Map<String, String> attachInfo;

    private OnRequestTimeoutListener onRequestTimeoutListener;

    private Timer timer;
    private String requestBody;
    private int readTimeout = Configuration.DEFAULT_READ_TIMEOUT;

    public void addFlag(int flag) {
        this.flags |= flag;
    }

    public void removeFlag(int flag) {
        this.flags &= ~flag;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Packet getRequestPacket() {
        return requestPacket;
    }

    public void setRequestPacket(Packet requestPacket) {
        this.requestPacket = requestPacket;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public int getRequestCommand() {
        return requestCommand;
    }

    public void setRequestCommand(int requestCommand) {
        this.requestCommand = requestCommand;
    }

    public void setOnRequestTimeoutListener(OnRequestTimeoutListener listener) {
        this.onRequestTimeoutListener = listener;
    }

    @Override
    public void sendSuccessMessage(int command, String params, Map<String, String> attach, Packet packet) {
        if (responseHandler != null) {
            responseHandler.sendSuccessMessage(command, params, attach, packet);
        }
    }

    @Override
    public void sendFailureMessage(int command, Throwable error) {
        if (responseHandler != null) {
            responseHandler.sendFailureMessage(command, error);
        }
    }

    public void setAttachInfo(Map<String, String> attachInfo) {
        this.attachInfo = attachInfo;
    }

    public Map<String, String> getAttachInfo() {
        return attachInfo;
    }

    public void onRemoveFromQuoue() {

    }

    public void startTimeoutTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RequestContext.this.onRequestTimeoutListener.onRequestTimeout(RequestContext.this);
            }
        },readTimeout * 1000);
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public interface OnRequestTimeoutListener {
        void onRequestTimeout(RequestContext context);
    }
}
