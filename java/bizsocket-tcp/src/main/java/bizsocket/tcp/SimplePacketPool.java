package bizsocket.tcp;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by tong on 16/10/19.
 */
public class SimplePacketPool implements PacketPool {
    protected final Queue<Packet> queue = new ArrayBlockingQueue<Packet>(50,true);

    @Override
    public Packet pull() {
        Packet packet = queue.poll();
        if (packet != null) {
            packet.onPrepareReuse();
        }
        return packet;
    }

    @Override
    public void push(Packet packet) {
        if (queue.size() == 100) {
            return;
        }

        queue.add(packet);
    }
}
