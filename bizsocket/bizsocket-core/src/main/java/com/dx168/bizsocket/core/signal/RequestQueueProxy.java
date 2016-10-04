package com.dx168.bizsocket.core.signal;

import com.dx168.bizsocket.core.AbstractBizSocket;
import com.dx168.bizsocket.core.RequestContext;
import com.dx168.bizsocket.core.RequestQueue;
import com.dx168.bizsocket.core.ResponseHandler;
import com.dx168.bizsocket.tcp.Packet;
import com.dx168.bizsocket.tcp.SocketConnection;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Created by tong on 16/10/4.
 */
public class RequestQueueProxy extends RequestQueue {
    private final RequestQueue base;

    public RequestQueueProxy(RequestQueue base) {
        super(base.getBizSocket());

        this.base = base;
    }

    @Override
    public void addRequestContext(RequestContext context) {
        base.addRequestContext(context);
    }

    @Override
    public void sendRequest(RequestContext context) {
        base.sendRequest(context);
    }

    @Override
    public boolean sendPacket(Packet requestPacket) {
        return base.sendPacket(requestPacket);
    }

    @Override
    public void onPacketSend(RequestContext context) {
        base.onPacketSend(context);
    }

    @Override
    public AbstractSerialContext buildSerialContext(SerialSignal serialSignal, RequestContext context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return base.buildSerialContext(serialSignal, context);
    }

    @Override
    public void removeRequestContext(RequestContext context) {
        base.removeRequestContext(context);
    }

    @Override
    public void removeRequestContexts(Collection<RequestContext> requestContexts) {
        base.removeRequestContexts(requestContexts);
    }

    @Override
    public Collection<RequestContext> getRequestContext(Filter filter) {
        return base.getRequestContext(filter);
    }

    @Override
    public void dispatchPacket(Packet responsePacket) {
        base.dispatchPacket(responsePacket);
    }

    @Override
    public void executeAllRequestContext() {
        base.executeAllRequestContext();
    }

    @Override
    public void connected(SocketConnection connection) {
        base.connected(connection);
    }

    @Override
    public void connectionClosed() {
        base.connectionClosed();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        base.connectionClosedOnError(e);
    }

    @Override
    public void reconnectingIn(int seconds) {
        base.reconnectingIn(seconds);
    }

    @Override
    public void onSendSuccessful(Packet packet) {
        base.onSendSuccessful(packet);
    }

    @Override
    public boolean prepareDispatchPacket(Packet packet) {
        return base.prepareDispatchPacket(packet);
    }

    @Override
    public void processPacket(Packet packet) {
        base.processPacket(packet);
    }

    @Override
    public AbstractBizSocket getBizSocket() {
        return base.getBizSocket();
    }

    @Override
    public void setGlobalNotifyHandler(ResponseHandler globalNotifyHandler) {
        base.setGlobalNotifyHandler(globalNotifyHandler);
    }
}
