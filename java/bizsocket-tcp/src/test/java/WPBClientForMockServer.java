import bizsocket.tcp.*;
import okio.BufferedSource;
import java.io.IOException;
import java.util.Random;

/**
 * Created by tong on 16/10/3.
 */
public class WPBClientForMockServer extends SocketConnection {
    public WPBClientForMockServer(String host, int port) {
        super(host, port);
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return new WPBPacketFactory();
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
                client.sendPacket(client.getPacketFactory().getRequestPacket(new Request.Builder().command(WPBPacket.CMD_CREATE_ORDER).utf8body(json).build()));
                Thread.sleep(new Random().nextInt(4000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class WPBPacketFactory extends PacketFactory {
        @Override
        public Packet getRequestPacket(Packet reusable,Request request) {
            return new WPBPacket(request.command(),request.body());
        }

        @Override
        public Packet getHeartBeatPacket(Packet recyclable) {
            return null;
        }

        @Override
        public Packet getRemotePacket(Packet reusable,BufferedSource source) throws IOException {
            return WPBPacket.build(source);
        }
    }
}
