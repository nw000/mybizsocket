package bizsocket.tcp;

import java.io.IOException;
import java.util.Map;

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
     * @param attach
     * @return
     */
    Packet buildRequestPacket(int command, ByteString requestBody, Map<String, String> attach);

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
