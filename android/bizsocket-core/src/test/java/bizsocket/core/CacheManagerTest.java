package bizsocket.core;

import client.WPBPacketFactory;
import bizsocket.tcp.Packet;
import common.WPBBizPacketValidator;
import junit.framework.TestCase;
import okio.ByteString;
import org.json.JSONObject;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

/**
 * Created by tong on 16/10/5.
 */
public class CacheManagerTest extends TestCase {
    CacheManager cacheManager;

    private PacketValidator validator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        cacheManager = new CacheManager();
        validator = new WPBBizPacketValidator();
        System.out.println("setUp");
    }

    @Test
    public void testPersistenceCacheEntry() throws Exception {
        System.out.println("testPersistenceCacheEntry");

        int command = 1;
        assertNull(cacheManager.get(command));
        cacheManager.put(CacheEntry.createPersistence(command, validator));
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(22, ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);
        assertNull(cacheManager.get(command));

        packet = new WPBPacketFactory().buildRequestPacket(command, ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        for (int i = 0; i < 15; i++) {
            assertNotNull(cacheManager.get(command));
        }
    }

    @Test
    public void testRelativeMillisCacheEntryTYPE_EXPIRED_NOT_USE() throws Exception {
        System.out.println("testRelativeMillisCacheEntryTYPE_EXPIRED_NOT_USE");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createRelativeMillis(command, CacheEntry.TYPE_EXPIRED_NOT_USE, TimeUnit.SECONDS, 3,validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(22,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);
        assertNull(cacheManager.get(command));

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNotNull(cacheManager.get(command));

        Thread.sleep(3500);

        assertNull(cacheManager.get(command));

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);
        assertNotNull(cacheManager.get(command));
    }

    @Test
    public void testRelativeMillisCacheEntryTYPE_EXPIRED_USE_AND_REFRESH() throws Exception {
        System.out.println("testRelativeMillisCacheEntryTYPE_EXPIRED_USE_AND_REFRESH");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createRelativeMillis(command, CacheEntry.TYPE_EXPIRED_USE_AND_REFRESH, TimeUnit.SECONDS, 3,validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(22,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);
        assertNull(cacheManager.get(command));

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(), false);

        Thread.sleep(3500);

        CacheEntry res = cacheManager.get(command);

        assertNotNull(res);
        assertEquals(res.isExpired(),true);

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);
        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(), false);
    }

    @Test
    public void testCounterCacheEntryTYPE_EXPIRED_NOT_USE() throws Exception {
        System.out.println("testCounterCacheEntryTYPE_EXPIRED_NOT_USE");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createCounter(command, CacheEntry.TYPE_EXPIRED_NOT_USE, 10,validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(22,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);
        assertNull(cacheManager.get(command));

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        for (int i = 0; i < 9; i++) {
            CacheEntry entry = cacheManager.get(command);
            assertNotNull(entry);
            assertNotNull(entry.getEntry());
            assertEquals(entry.isExpired(),false);
        }
        assertNull(cacheManager.get(command));

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        for (int i = 0; i < 9; i++) {
            CacheEntry entry = cacheManager.get(command);
            assertNotNull(entry);
            assertNotNull(entry.getEntry());
            assertEquals(entry.isExpired(),false);
        }
        assertNull(cacheManager.get(command));
    }

    @Test
    public void testCounterCacheEntryTYPE_EXPIRED_USE_AND_REFRESH() throws Exception {
        System.out.println("testCounterCacheEntryTYPE_EXPIRED_USE_AND_REFRESH");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createCounter(command, CacheEntry.TYPE_EXPIRED_USE_AND_REFRESH, 10,validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(22,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);
        assertNull(cacheManager.get(command));

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        for (int i = 0; i < 9; i++) {
            CacheEntry entry = cacheManager.get(command);
            assertNotNull(entry);
            assertNotNull(entry.getEntry());
            assertEquals(entry.isExpired(),false);
        }
        CacheEntry cacheEntry1 = cacheManager.get(command);
        assertNotNull(cacheEntry1);
        assertEquals(cacheEntry1.isExpired(), true);

        cacheEntry1 = cacheManager.get(command);
        assertNotNull(cacheEntry1);
        assertEquals(cacheEntry1.isExpired(),true);

        packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        for (int i = 0; i < 9; i++) {
            CacheEntry entry = cacheManager.get(command);
            assertNotNull(entry);
            assertNotNull(entry.getEntry());
            assertEquals(entry.isExpired(),false);
        }
        cacheEntry1 = cacheManager.get(command);
        assertNotNull(cacheEntry1);
        assertEquals(cacheEntry1.isExpired(), true);
    }

    @Test
    public void testUseUtilSendCmdCacheEntryTYPE_EXPIRED_NOT_USE() throws Exception {
        System.out.println("testUseUtilSendCmdCacheEntryTYPE_EXPIRED_NOT_USE");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createUseUtilSendCmd(command, CacheEntry.TYPE_EXPIRED_NOT_USE, new int[]{2, 3, 4},validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(), false);

        packet = new WPBPacketFactory().buildRequestPacket(2,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onSendPacket(packet);

        assertNull(cacheManager.get(command));
    }

    @Test
    public void testUseUtilSendCmdCacheEntryTYPE_EXPIRED_USE_AND_REFRESH() throws Exception {
        System.out.println("testUseUtilSendCmdCacheEntryTYPE_EXPIRED_USE_AND_REFRESH");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createUseUtilSendCmd(command, CacheEntry.TYPE_EXPIRED_USE_AND_REFRESH, new int[]{2, 3, 4},validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(), false);

        packet = new WPBPacketFactory().buildRequestPacket(2,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onSendPacket(packet);

        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(),true);
    }

    @Test
    public void testUseUtilReceiveCmdCacheEntryTYPE_EXPIRED_NOT_USE() throws Exception {
        System.out.println("testUseUtilReceiveCmdCacheEntryTYPE_EXPIRED_NOT_USE");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createUseUtilReceiveCmd(command, CacheEntry.TYPE_EXPIRED_NOT_USE, new int[]{2, 3, 4},validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(), false);

        packet = new WPBPacketFactory().buildRequestPacket(2,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNull(cacheManager.get(command));
    }

    @Test
    public void testUseUtilReceiveCmdCacheEntryTYPE_EXPIRED_USE_AND_REFRESH() throws Exception {
        System.out.println("testUseUtilReceiveCmdCacheEntryTYPE_EXPIRED_USE_AND_REFRESH");

        int command = 1;
        assertNull(cacheManager.get(command));

        CacheEntry cacheEntry =  CacheEntry.createUseUtilReceiveCmd(command, CacheEntry.TYPE_EXPIRED_USE_AND_REFRESH, new int[]{2, 3, 4},validator);
        cacheManager.put(cacheEntry);
        assertNull(cacheManager.get(command));

        Packet packet = new WPBPacketFactory().buildRequestPacket(command,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(), false);

        packet = new WPBPacketFactory().buildRequestPacket(2,ByteString.encodeUtf8(new JSONObject().toString()), attach);
        cacheManager.onReceivePacket(packet);

        assertNotNull(cacheManager.get(command));
        assertEquals(cacheManager.get(command).isExpired(),true);
    }
}
