package bizsocket.core.MyCode.packet;

import java.util.*;

/**
 * Created by dxjf on 16/11/8.
 */
public class SimplePacketPool implements PacketPool {

    private final static int QUEUE_SIZE = 60;

    private static final int COOLING_PERIOD = 10 * 1000;

    public final Collection<Entity> packets = Collections.synchronizedSet(new HashSet<Entity>());

    @Override
    public void push(Packet packet) {
        if (packet == null || packets.size() == QUEUE_SIZE) {
            return;
        }
        packets.add(new Entity(packet));
    }

    @Override
    public Packet pool() {
        Entity entity = null;
        for (Entity e : packets) {
            if (e.isEnable()) {
                entity = e;
                break;
            }
        }

        if (entity != null) {
            packets.remove(entity);
            Packet packet = entity.packet;
            packet.setFlags(packet.getFlags() & -Packet.FLAG_RECYCLED);
            packet.onPrepareReuse();
            return packet;
        }
        return null;
    }

    static class Entity {

        private final Packet packet;
        private final long saveTimeMillis;

        public Entity(Packet packet) {
            this.packet = packet;
            saveTimeMillis = System.currentTimeMillis();
        }

        public boolean isEnable() {
            long current = System.currentTimeMillis();
            return current >= saveTimeMillis + COOLING_PERIOD && (packet.getFlags() & Packet.FLAG_RECYCLED) != 0;
        }
    }

}
