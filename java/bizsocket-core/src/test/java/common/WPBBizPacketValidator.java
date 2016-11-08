package common;

import bizsocket.core.PacketValidator;
import bizsocket.tcp.Packet;

/**
 * Created by tong on 16/10/6.
 */
public class WPBBizPacketValidator implements PacketValidator {
    @Override
    public boolean verify(Packet packet) {
        return WPBProtocolUtil.isSuccessResponsePacket((WPBPacket) packet);
    }



}
