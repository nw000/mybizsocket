package socket;

/**
 * Created by tong on 16/10/3.
 */
public abstract class Packet {
    /**
     * 获取报文
     * @return
     */
    public abstract byte[] toBytes();

    public abstract int getCommand();

    public abstract String getPacketID();
}
