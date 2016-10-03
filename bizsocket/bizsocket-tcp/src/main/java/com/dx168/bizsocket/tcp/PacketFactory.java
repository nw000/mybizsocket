package com.dx168.bizsocket.tcp;

import java.io.IOException;
import okio.BufferedSource;

/**
 * Created by tong on 16/10/3.
 */
public interface PacketFactory {
    /**
     * create request packet with command and body
     * @param command
     * @param body
     * @return
     */
    Packet buildRequestPacket(int command, String body);

    /**
     * create packet from the stream of server
     * @param source
     * @return
     * @throws IOException
     */
    Packet buildPacket(BufferedSource source) throws IOException;

    boolean supportHeartBeat();

    Packet buildHeartBeatPacket();
}
