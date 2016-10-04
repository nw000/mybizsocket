package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Packet;
import java.util.Map;

/**
 * Created by tong on 16/2/17.
 */
public interface ResponseHandler {
    /**
     * Notifies callback, that request was handled successfully
     *
     * @param command   command code
     *
     */
    void sendSuccessMessage(int command, String params,Map<String,String> attach, Packet responsePacket);

    /**
     * Returns if request was completed with error code or failure of implementation
     *
     * @param command      command code
     * @param error        cause of request failure
     */
    void sendFailureMessage(int command, Throwable error);
}
