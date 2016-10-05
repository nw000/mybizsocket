package client;

import com.dx168.bizsocket.core.AbstractBizSocket;
import com.dx168.bizsocket.core.Configuration;
import com.dx168.bizsocket.core.ResponseHandler;
import com.dx168.bizsocket.core.signal.SerialSignal;
import com.dx168.bizsocket.tcp.Packet;
import com.dx168.bizsocket.tcp.PacketFactory;
import common.WPBPacket;
import okio.BufferedSource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by tong on 16/10/3.
 */
public class WPBClientForMockServer extends AbstractBizSocket implements PacketFactory {
    public WPBClientForMockServer(Configuration configuration) {
        super(configuration);

        getRequestQueue().addSerialSignal(new SerialSignal(OrderListSerialContext.class, WPBPacket.CMD_QUERY_ORDER_LIST,
                new int[]{WPBPacket.CMD_QUERY_ORDER_LIST, WPBPacket.CMD_QUERY_ORDER_TYPE}));
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return this;
    }

    @Override
    public Packet buildRequestPacket(int command, String body) {
        return new common.WPBPacket(command,body);
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
        WPBClientForMockServer client = new WPBClientForMockServer(new Configuration.Builder()
                .host("127.0.0.1")
                .port(9103)
                .build());
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.subscribe(client, WPBPacket.CMD_PRICE, new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, String params, Map<String, String> attach, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,params: " + params + " attach: " + attach + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        String json = "{\"productId\" : \"1\",\"isJuan\" : \"0\",\"type\" : \"2\",\"sl\" : \"1\"}";
        client.request(client, WPBPacket.CMD_CREATE_ORDER, json, null, new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, String params, Map<String, String> attach, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,params: " + params + " attach: " + attach + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        json = "{\"pageSize\" : \"10000\"}";
        client.request(client, WPBPacket.CMD_QUERY_ORDER_LIST, json, null, new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, String params, Map<String, String> attach, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,params: " + params + " attach: " + attach + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
