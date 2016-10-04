import com.dx168.bizsocket.tcp.Packet;
import com.dx168.bizsocket.tcp.PacketFactory;
import com.dx168.bizsocket.tcp.PacketListener;
import com.dx168.bizsocket.tcp.SocketConnection;
import okio.BufferedSource;
import java.io.IOException;
import java.util.Random;

/**
 * Created by tong on 16/10/3.
 */
public class WPBClientForMockServer extends SocketConnection implements PacketFactory {
    public WPBClientForMockServer(String host, int port) {
        super(host, port);
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return this;
    }

    @Override
    public Packet buildRequestPacket(int command, String body) {
        return new WPBPacket(command,body);
    }

    @Override
    public Packet buildPacket(BufferedSource source) throws IOException {
        return WPBPacket.build(source);
    }

    @Override
    public boolean supportHeartBeat() {
        return false;
    }

    @Override
    public Packet buildHeartBeatPacket() {
        return null;
    }

    public static void main(String[] args) {
        WPBClientForMockServer client = new WPBClientForMockServer("127.0.0.1",9103);
        client.addPacketListener(new PacketListener() {
            @Override
            public void onSendSuccessful(Packet packet) {
                System.out.println("send: " + packet);
            }

            @Override
            public void processPacket(Packet packet) {
                System.out.println("rec: " + packet);
            }
        });
        client.connectAndStartWatch();

        while (true) {
            try {
                String json = "{\"productId\" : \"1\",\"isJuan\" : \"0\",\"type\" : \"2\",\"sl\" : \"1\"}";
                client.sendPacket(client.buildRequestPacket(WPBPacket.CMD_CREATE_ORDER,json));
                Thread.sleep(new Random().nextInt(4000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
