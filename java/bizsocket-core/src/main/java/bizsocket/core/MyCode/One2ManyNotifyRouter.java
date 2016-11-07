package bizsocket.core.MyCode;

import bizsocket.core.PacketValidator;
import bizsocket.core.ResponseHandler;
import bizsocket.tcp.Packet;

/**
 * Created by dxjf on 16/11/7.
 */
public interface One2ManyNotifyRouter {
    /**
     *添加对粘性通知的支持
     */
    void addStickyCmd(int cmd, PacketValidator packetValidator);


    /**
     * 移除粘性广播通知
     */
    void removeStickyCmd(int cmd);


    /**
     * 订阅事件
     */
    void subscribe(Object tag, int cmd , ResponseHandler responseHandler);

    /**
     * 取消订阅
     */
    void unsubscribe(Object tagOrResponseHandler);

    /**
     * 路由
     */
    void route(int command, Packet packet);

}
