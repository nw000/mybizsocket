package bizsocket.core.MyCode;

import bizsocket.tcp.Packet;
import okio.ByteString;

/**
 * Created by dxjf on 16/11/7.
 */
public interface ResponseHandler {
    /**
     * Notifies callback, that request was handled successfully
     *
     * @param command   command code
     *
     */
    void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket);

    /**
     * Returns if request was completed with error code or failure of implementation
     *
     * @param command      command code
     * @param error        cause of request failure
     */
    void sendFailureMessage(int command, Throwable error);
}
