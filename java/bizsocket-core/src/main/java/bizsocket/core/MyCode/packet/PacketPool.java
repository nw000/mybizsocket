package bizsocket.core.MyCode.packet;

/**
 * Created by dxjf on 16/11/8.
 */
public  interface PacketPool {
    public abstract void push(Packet packet);

    public abstract Packet pool();
}
