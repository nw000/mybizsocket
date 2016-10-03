package com.dx168.bizsocket.tcp;

/**
 * Base class for XMPP packets. Every packet has a unique ID (which is automatically
 * generated, but can be overridden). Optionally, the "to" and "from" fields can be set.
 *
 */
public abstract class Packet {
    /**
     * Returns the packet as bytes.
     */
    public abstract byte[] toBytes();

    public abstract int getCommand();

    /**
     * Returns the unique ID of the packet.
     */
    public abstract String getPacketID();

    public String nextPacketID() {
        return "";
    }
}