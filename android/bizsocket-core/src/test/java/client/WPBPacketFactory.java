package client;

import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import common.WPBPacket;
import okio.BufferedSource;
import okio.ByteString;
import java.io.IOException;

/**
 * Created by tong on 16/10/5.
 */
public class WPBPacketFactory implements PacketFactory {
    @Override
    public Packet buildRequestPacket(int command, ByteString requestBody) {
        return new common.WPBPacket(command,requestBody);
    }

    @Override
    public Packet buildPacket(BufferedSource source) throws IOException {
        return WPBPacket.build(source);
    }

    @Override
    public boolean supportHeartBeat() {
        return false;
    }

    @Override
    public Packet buildHeartBeatPacket() {
        return null;
    }
}
