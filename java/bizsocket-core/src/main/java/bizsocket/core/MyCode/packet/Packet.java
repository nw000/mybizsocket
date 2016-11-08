package bizsocket.core.MyCode.packet;

import bizsocket.logger.Logger;
import bizsocket.logger.LoggerFactory;

/**
 * Created by dxjf on 16/11/8.
 */
public abstract class Packet {
    public static final Logger logger = LoggerFactory.getLogger(Packet.class.getSimpleName());
    //状态
    /**
     * 可回收状态
     */
    public static final int FLAG_RECYCABLE = 1;

    /**
     * 刚发送完的可回收的状态
     */
    public static final int FLAG_AUTO_RECYCLE_ON_SEND_SUCCESS = 1 << 1;

    /**
     * 已经回收的状态
     */
    public static final int FLAG_RECYCLED = 1 << 2;

    private int command;
    private String describle;
    private PacketPool packetPool;
    private int flags = FLAG_RECYCABLE;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public PacketPool getPacketPool() {
        return packetPool;
    }

    public void setPacketPool(PacketPool packetPool) {
        this.packetPool = packetPool;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public abstract byte[] toBytes();

    public abstract String getContent();

    public abstract void setPacketId(String packetId);

    public abstract String getPacketId();

    public void onSendSuccessful() {
        logger.debug("----------------send packet :" + getCommand() + ", desc :" + getDescrible() + ", id + " + getPacketId());
        logger.debug("----------------send content :" + getContent());
    }

    public void onReceiveFromServer() {

    }

    public void onDispatch() {
        logger.debug("----------------send packet :" + getCommand() + ", desc :" + getDescrible() + ", id + " + getPacketId());
        logger.debug("----------------send content :" + getContent());
    }

    /**
     * 准备复用时调用
     */
    public void onPrepareReuse() {

    }


    /**
     * 正在回收时调用
     */

    private void onRecycle() {

    }
    public void recycle() {
        if ((getFlags() & FLAG_RECYCABLE)== 0) {
            return;
        }
        if ((getFlags() & FLAG_RECYCLED) != 0) {
            return;
        }

        if (packetPool != null) {
            setFlags(getFlags() & FLAG_RECYCLED);
            packetPool.push(this);
        }
        onRecycle();

    }


}
