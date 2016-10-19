package client;

import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import common.WPBCmd;
import common.WPBPacket;
import okio.BufferedSource;
import java.io.IOException;

/**
 * Created by tong on 16/10/5.
 */
public class WPBPacketFactory extends PacketFactory {
    @Override
    public Packet getRequestPacket(Packet reusable,Request request) {
        if (reusable != null && reusable instanceof WPBPacket) {
            WPBPacket packet = (WPBPacket) reusable;
            packet.setCommand(request.command());
            packet.setContent(request.body().utf8());
            return packet;
        }
        return new WPBPacket(request.command(),request.body());
    }

    @Override
    public Packet getHeartBeatPacket(Packet reusable) {
        return getRequestPacket(reusable,new Request.Builder().command(WPBCmd.HEARTBEAT.getValue()).utf8body("{}").build());
    }

    @Override
    public Packet getRemotePacket(Packet reusable,BufferedSource source) throws IOException {
        return WPBPacket.build(reusable,source);
    }
}
