package client;

import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import common.WPBPacket;
import okio.BufferedSource;

import java.io.IOException;

/**
 * Created by tong on 16/10/5.
 */
public class WPBPacketFactory extends PacketFactory {
    @Override
    public Packet getRequestPacket(Request request) {
        return new common.WPBPacket(request.command(),request.body());
    }

    @Override
    public Packet getHeartBeatPacket() {
        return null;
    }

    @Override
    public Packet getRemotePacket(BufferedSource source) throws IOException {
        return WPBPacket.build(source);
    }
}
