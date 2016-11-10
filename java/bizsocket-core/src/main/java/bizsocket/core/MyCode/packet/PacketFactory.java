package bizsocket.core.MyCode.packet;

import bizsocket.core.MyCode.request.Request;
import okio.BufferedSource;

import java.io.IOException;

/**
 * Created by dxjf on 16/11/9.
 */
public abstract class PacketFactory {
    private PacketPool packetPool;

    public PacketFactory() {
        this(new SimplePacketPool());
    }


    public PacketFactory(SimplePacketPool simplePacketPool) {
        this.packetPool = simplePacketPool;
    }

    public Packet getRequestPacket(Request request) {
        Packet packet = getRequestPacket(getPacketPool().pool(), request);
        if (packet != null && request.isRecycleOnSend()) {
            packet.setFlags(packet.getFlags() | Packet.FLAG_AUTO_RECYCLE_ON_SEND_SUCCESS);
        }
        return packet;
    }

    public Packet getHeartBeatPacket() {
        Packet heartBeatPacket = getHeartBeatPacket(getPacketPool().pool());
        if (heartBeatPacket != null) {
            heartBeatPacket.setFlags(heartBeatPacket.getFlags() | Packet.FLAG_AUTO_RECYCLE_ON_SEND_SUCCESS);
        }
        return heartBeatPacket;
    }

    public Packet getRemotePacket(BufferedSource source) throws IOException {
        return getRemotePacket(getPacketPool().pool(), source);
    }

    protected abstract WPBPacket getRequestPacket(Packet pool, Request request);

    protected abstract Packet getHeartBeatPacket(Packet pool);

    protected abstract Packet getRemotePacket(Packet pool, BufferedSource source);

    public PacketPool getPacketPool() {
        return packetPool;
    }
}
