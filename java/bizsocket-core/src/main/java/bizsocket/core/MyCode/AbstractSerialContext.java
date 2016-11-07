package bizsocket.core.MyCode;

import bizsocket.core.RequestContext;
import bizsocket.core.RequestQueue;
import bizsocket.tcp.Packet;

/**
 * Created by dxjf on 16/11/7.
 */
public abstract class AbstractSerialContext {
    //上下文60秒过期
    private static final long EXPIRED_MILLIS = 60 * 1000;

    private long createfMillis;

    private final SerialSignal serialSignal;

    private final RequestContext requestContext;

    public AbstractSerialContext(SerialSignal serialSignal, RequestContext requestContext) {
        createfMillis = System.currentTimeMillis();
        this.serialSignal = serialSignal;
        this.requestContext = requestContext;
    }

    public SerialSignal getSerialSignal() {
        return serialSignal;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    //是否过期
    public boolean isExpired() {
        return System.currentTimeMillis() - createfMillis >= EXPIRED_MILLIS;
    }

    public String getRequestPacketId() {
        if (requestContext != null && requestContext.getRequestPacket() != null) {
            return requestContext.getRequestPacket().getPacketID();
        }
        return null;
    }

    public abstract Packet processPacket(RequestQueue requestQueue, Packet packet);

    public boolean shouldProcess(RequestQueue requestQueue, Packet packet) {
        int command = packet.getCommand();
        String requestPacketId = getRequestPacketId();
        String packetID = packet.getPacketID();

        return (requestPacketId != null && requestPacketId.equals(packetID) || serialSignal.isStrongReference(command) || serialSignal.isWeekReference(command));
    }

}
