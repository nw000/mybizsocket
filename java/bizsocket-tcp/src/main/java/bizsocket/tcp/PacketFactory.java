package bizsocket.tcp;

import java.io.IOException;
import okio.BufferedSource;

/**
 * Created by tong on 16/10/3.
 */
public abstract class PacketFactory {
    /**
     * create request packet with command and body
     * @param request
     * @return
     */
    public abstract Packet getRequestPacket(Request request);

    /**
     * create heartbeat packet
     * @return
     */
    public abstract Packet getHeartBeatPacket();

    /**
     * create packet from the stream of server
     * @param source
     * @return
     * @throws IOException
     */
    public abstract Packet getRemotePacket(BufferedSource source) throws IOException;
}
