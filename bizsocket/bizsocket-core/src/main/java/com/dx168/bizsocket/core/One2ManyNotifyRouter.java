package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Packet;

/**
 * Created by tong on 16/10/4.
 */
public interface One2ManyNotifyRouter {
    int FLAG_DEFAULT = 0;
    int FLAG_ONCE_CALL = 1 << 0;

    void subscribe(Object tag,int cmd,int flags,ResponseHandler responseHandler);

    void unsubscribe(Object tagOrResponseHandler);

    void route(int command, Packet packet);
}
