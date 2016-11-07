package bizsocket.core.MyCode;

import bizsocket.tcp.Packet;

/**
 * Created by dxjf on 16/11/7.
 */
public interface PacketValidator {
    boolean verity(Packet packet);
}
