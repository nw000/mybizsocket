package com.dx168.bizsocket.tcp;

/**
 * Base class for tcp packets. Every packet has a unique ID (which is automatically
 * generated, but can be overridden).
 */
public abstract class Packet {
    protected long longPacketId;

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
        if (longPacketId == Long.MAX_VALUE) {
            longPacketId = Long.MAX_VALUE;
        }
        return String.valueOf(Long.valueOf(longPacketId));
    }
}