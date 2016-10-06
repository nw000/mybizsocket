package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Packet;
import common.WPBCmd;
import common.WPBPacket;
import junit.framework.TestCase;
import okio.ByteString;
import org.junit.Test;

/**
 * Created by tong on 16/10/6.
 */
public class DefaultOne2ManyNotifyRouterTest extends TestCase {
    private One2ManyNotifyRouter router;
    WPBPacket receivePacket = null;

    public DefaultOne2ManyNotifyRouterTest() {
        router = new DefaultOne2ManyNotifyRouter();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        receivePacket = null;
    }

    @Test
    public void testRouteFLAG_DEFAULT() throws Exception{
        router.subscribe(this, WPBCmd.NOTIFY_PRICE.getValue(), One2ManyNotifyRouter.FLAG_DEFAULT, new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (WPBPacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        });

        WPBPacket packet = new WPBPacket(WPBCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));

        for (int i = 0; i < 5; i++) {
            receivePacket = null;
            router.route(WPBCmd.NOTIFY_PRICE.getValue(),packet);
            assertEquals(packet,receivePacket);
        }
    }

    @Test
    public void testRouteFLAG_ONCE_CALL() throws Exception{
        router.subscribe(this, WPBCmd.NOTIFY_PRICE.getValue(), One2ManyNotifyRouter.FLAG_ONCE_CALL, new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (WPBPacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        });

        WPBPacket packet = new WPBPacket(WPBCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));

        for (int i = 0; i < 5; i++) {
            receivePacket = null;
            router.route(WPBCmd.NOTIFY_PRICE.getValue(),packet);

            if (i == 0) {
                assertEquals(packet,receivePacket);
            }
            else {
                assertNull(receivePacket);
            }
        }
    }

    @Test
    public void testUnSubscribe() throws Exception{
        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (WPBPacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        };
        router.subscribe(this, WPBCmd.NOTIFY_PRICE.getValue(), One2ManyNotifyRouter.FLAG_DEFAULT, responseHandler);

        WPBPacket packet = new WPBPacket(WPBCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                receivePacket = null;
                router.route(WPBCmd.NOTIFY_PRICE.getValue(),packet);
                assertEquals(packet,receivePacket);
                router.unsubscribe(responseHandler);

                receivePacket = null;
            }
            else {
                router.route(WPBCmd.NOTIFY_PRICE.getValue(),packet);
                assertNull(receivePacket);
            }
        }
    }

    @Test
    public void testUnSubscribe2() throws Exception{
        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (WPBPacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        };
        router.subscribe(this, WPBCmd.NOTIFY_PRICE.getValue(), One2ManyNotifyRouter.FLAG_DEFAULT, responseHandler);

        WPBPacket packet = new WPBPacket(WPBCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                receivePacket = null;
                router.route(WPBCmd.NOTIFY_PRICE.getValue(),packet);
                assertEquals(packet,receivePacket);
                router.unsubscribe(new Object());
                assertEquals(packet,receivePacket);
                router.unsubscribe(this);

                receivePacket = null;
            }
            else {
                router.route(WPBCmd.NOTIFY_PRICE.getValue(),packet);
                assertNull(receivePacket);
            }
        }
    }
}
