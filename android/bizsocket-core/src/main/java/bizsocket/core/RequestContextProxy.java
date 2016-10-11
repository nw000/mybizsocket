package bizsocket.core;

import bizsocket.tcp.Packet;
import okio.ByteString;

/**
 * Created by tong on 16/10/10.
 */

public class RequestContextProxy extends RequestContext {
    private RequestContext base;

    public RequestContextProxy(RequestContext base) {
        this.base = base;
    }

    @Override
    public int getFlags() {
        return base.getFlags();
    }

    @Override
    public void setFlags(int flags) {
        base.setFlags(flags);
    }

    @Override
    public Object getTag() {
        return base.getTag();
    }

    @Override
    public void setTag(Object tag) {
        base.setTag(tag);
    }

    @Override
    public Packet getRequestPacket() {
        return base.getRequestPacket();
    }

    @Override
    public void setRequestPacket(Packet requestPacket) {
        base.setRequestPacket(requestPacket);
    }

    @Override
    public ResponseHandler getResponseHandler() {
        return base.getResponseHandler();
    }

    @Override
    public void setResponseHandler(ResponseHandler responseHandler) {
        base.setResponseHandler(responseHandler);
    }

    @Override
    public int getRequestCommand() {
        return base.getRequestCommand();
    }

    @Override
    public void setRequestCommand(int requestCommand) {
        base.setRequestCommand(requestCommand);
    }

    @Override
    public void setOnRequestTimeoutListener(OnRequestTimeoutListener listener) {
        base.setOnRequestTimeoutListener(listener);
    }

    @Override
    public void sendSuccessMessage(int command, ByteString requestBody, Packet packet) {
        base.sendSuccessMessage(command, requestBody, packet);
    }

    @Override
    public void sendFailureMessage(int command, Throwable error) {
        base.sendFailureMessage(command, error);
    }

    @Override
    public void startTimeoutTimer() {
        base.startTimeoutTimer();
    }

    @Override
    public void setRequestBody(ByteString requestBody) {
        base.setRequestBody(requestBody);
    }

    @Override
    public ByteString getRequestBody() {
        return base.getRequestBody();
    }

    @Override
    public void setReadTimeout(long readTimeout) {
        base.setReadTimeout(readTimeout);
    }

    @Override
    public void onAddToQuote() {
        base.onAddToQuote();
    }

    @Override
    public void onRemoveFromQuoue() {
        base.onRemoveFromQuoue();
    }
}
