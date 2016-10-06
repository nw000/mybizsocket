package bizsocket.tcp;

import java.io.IOException;
import okio.BufferedSource;
import okio.ByteString;

/**
 * Created by tong on 16/10/3.
 */
public interface PacketFactory {
    /**
     * create request packet with command and body
     * @param command
     * @param requestBody
     * @return
     */
    Packet buildRequestPacket(int command, ByteString requestBody);

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
