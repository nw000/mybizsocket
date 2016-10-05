package client;

import com.dx168.bizsocket.tcp.Packet;
import com.dx168.bizsocket.tcp.PacketFactory;
import common.WPBPacket;
import okio.BufferedSource;
import java.io.IOException;

/**
 * Created by tong on 16/10/5.
 */
public class WPBPacketFactory implements PacketFactory {
    @Override
    public Packet buildRequestPacket(int command, String body) {
        return new common.WPBPacket(command,body);
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
