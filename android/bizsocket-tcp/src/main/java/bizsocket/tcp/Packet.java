package bizsocket.tcp;

import bizsocket.logger.Logger;
import bizsocket.logger.LoggerFactory;

/**
 * Base class for tcp packets. Every packet has a unique ID (which is automatically
 * generated, but can be overridden).
 */
public abstract class Packet {
    private static final Logger LOGGER = LoggerFactory.getLogger(Packet.class.getSimpleName());

    private long longPacketId;

    /**
     * Returns the packet as bytes.
     */
    public abstract byte[] toBytes();

    public abstract String getContent();

    /**
     * Returns the unique ID of the packet.
     */
    public abstract String getPacketID();

    public abstract void setPacketID(String packetID);

    public abstract int getCommand();

    public abstract void setCommand(int command);

    public String nextPacketID() {
        if (longPacketId == Long.MAX_VALUE) {
            longPacketId = Long.MAX_VALUE;
        }
        return String.valueOf(Long.valueOf(longPacketId));
    }

    /**
     * 获取包的描述
     * @return
     */
    public abstract String getDescription();


    public void onSendSuccessful() {
        LOGGER.debug("-------------------send packet: " + getCommand() + " ,desc: " + getDescription() + ", id: " + getPacketID());
        LOGGER.debug("-------------------send content: " + getContent());
    }

    public void onReceiveFromServer() {

    }

    public void onDispatch() {
        LOGGER.debug("-------------------receive: cmd: " + getCommand() + ", id: " + getPacketID() + " ,desc: " + getDescription());
        LOGGER.debug("-------------------receive: content: " + getContent());
    }
}