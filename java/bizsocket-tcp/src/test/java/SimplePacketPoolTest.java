import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import junit.framework.TestCase;
import okio.BufferedSource;
import okio.ByteString;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by tong on 16/10/19.
 */
//public class SimplePacketPoolTest extends TestCase {
//    public static class TestPacketFactory extends PacketFactory {
//        @Override
//        public Packet getRequestPacket(Packet reusable, Request request) {
//            return null;
//        }
//
//        @Override
//        public Packet getHeartBeatPacket(Packet reusable) {
//            int cmd = WPBPacket.CMD_HEARTBEAT;
//            if (reusable != null && reusable instanceof WPBPacket) {
//                reusable.setCommand(cmd);
//                ((WPBPacket) reusable).setContent("{}");
//                return reusable;
//            }
//            return new WPBPacket(WPBPacket.CMD_HEARTBEAT,ByteString.encodeUtf8("{}"));
//        }
//
//        @Override
//        public Packet getRemotePacket(Packet reusable, BufferedSource source) throws IOException {
//            return null;
//        }
//    }
//    @Test
//    public void test1() throws Exception {
//
//    }
//}
