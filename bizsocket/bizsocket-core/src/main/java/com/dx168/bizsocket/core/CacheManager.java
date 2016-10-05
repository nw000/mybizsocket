package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Packet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tong on 16/10/5.
 */
public class CacheManager {
    private final Map<Integer,CacheEntry> cacheEntryMap = new ConcurrentHashMap<Integer, CacheEntry>();

    public CacheEntry get(Packet request) {
        if (request == null) {
            return null;
        }
        return get(request.getCommand());
    }

    public CacheEntry get(int cmd) {
        CacheEntry cacheEntry = cacheEntryMap.get(cmd);
        if (cacheEntry != null && cacheEntry.getEntry() == null) {
            return null;
        }

        if (cacheEntry != null && cacheEntry.getEntry() != null && cacheEntry instanceof CounterCacheEntry) {
            ((CounterCacheEntry) cacheEntry).addCount();
        }

        if (cacheEntry != null && cacheEntry.isExpired()) {
            if (cacheEntry.getType() == CacheEntry.TYPE_EXPIRED_NOT_USE) {
                return null;
            }
//            if (cacheEntry.getType() == CacheEntry.TYPE_EXPIRED_USE_AND_REMOVE) {
//                remove(cacheEntry);
//                return cacheEntry;
//            }
        }
        return cacheEntry;
    }

    public void put(CacheEntry entry) {
        if (entry != null) {
            cacheEntryMap.put(entry.getCommand(),entry);
        }
    }

    public void remove(CacheEntry cacheEntry) {
        if (cacheEntry != null) {
            remove(cacheEntry.getCommand());
        }
    }

    public void remove(int cmd) {
        cacheEntryMap.remove(cmd);
    }

    public void removeAll() {
        cacheEntryMap.clear();
    }

    void onSendPacket(Packet request) {
        for (CacheEntry entry : cacheEntryMap.values()) {
            if (entry instanceof UseUtilSendCmdCacheEntry) {
                ((UseUtilSendCmdCacheEntry) entry).onSendCmd(request.getCommand());
            }
        }
    }

    void onReceivePacket(Packet network) {
        for (CacheEntry entry : cacheEntryMap.values()) {
            if (entry.getCommand() == network.getCommand()) {
                entry.updateEntry(network);
            }
            if (entry instanceof UseUtilReceiveCmdCacheEntry) {
                ((UseUtilReceiveCmdCacheEntry) entry).onReceiveCmd(network.getCommand());
            }
        }
    }
}
