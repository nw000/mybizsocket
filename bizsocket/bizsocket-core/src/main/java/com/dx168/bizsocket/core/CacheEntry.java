package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Packet;
import java.util.concurrent.TimeUnit;

/**
 * Created by tong on 16/10/5.
 */
public abstract class CacheEntry {
    /**
     * 过期不使用
     */
    public static final int TYPE_EXPIRED_NOT_USE = 0;

//    /**
//     * 过期使用一次并且删除
//     */
//    public static final int TYPE_EXPIRED_USE_AND_REMOVE = 1 << 0;

    /**
     * 过期使用并且刷新缓存
     */
    public static final int TYPE_EXPIRED_USE_AND_REFRESH = 1;

    private int command;
    private CacheStrategy strategy;
    private Packet packet;
    private int type = TYPE_EXPIRED_NOT_USE;

    CacheEntry(CacheStrategy strategy,int command, int type) {
        this.strategy = strategy;
        this.command = command;
        this.type = type;

        if (type != TYPE_EXPIRED_NOT_USE
                && type != TYPE_EXPIRED_USE_AND_REFRESH) {

        }
    }

    public abstract boolean isExpired();
    public abstract void onUpdateEntry(Packet networkPacket);

    public void updateEntry(Packet networkPacket) {
        if (networkPacket == null) {
            return;
        }
        if (networkPacket.getCommand() != packet.getCommand()) {
            throw new IllegalArgumentException("can not update packet, expect cmd: " + packet.getCommand() + " but param cmd is " + networkPacket.getCommand());
        }
        this.packet = networkPacket;

        onUpdateEntry(networkPacket);
    }

    public int getCommand() {
        return command;
    }

    public CacheStrategy getStrategy() {
        return strategy;
    }

    public Packet getEntry() {
        return packet;
    }

    public int getType() {
        return type;
    }

    void setPacket(Packet packet) {
        this.packet = packet;
    }

    public static CacheEntry createPersistence(int command) {
        return new PersistenceCacheEntry(command);
    }

    public static CacheEntry createRelativeMillis(int command, int type,TimeUnit unit,long duration) {
        return new RelativeMillisCacheEntry(command,type,unit,duration);
    }

    public static CacheEntry createCounter(int command, int type,int expiresCount) {
        return new CounterCacheEntry(command,type,expiresCount);
    }

    public static CacheEntry createUseUtilConflict(int command, int type,int ...conflictCommands) {
        return new UseUtilConflictCacheEntry(command, type, conflictCommands);
    }
}

//永不过期
class PersistenceCacheEntry extends CacheEntry {
    public PersistenceCacheEntry(int command) {
        super(CacheStrategy.persistence, command, TYPE_EXPIRED_NOT_USE);
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void onUpdateEntry(Packet networkPacket) {

    }
}

//按缓存的时间过期
class RelativeMillisCacheEntry extends CacheEntry {
    private long dMillis;
    private long expiredMillis;

    public RelativeMillisCacheEntry(int command, int type,TimeUnit unit,long duration) {
        super(CacheStrategy.relative_millis, command, type);
        dMillis = unit.toMillis(duration);
        expiredMillis = System.currentTimeMillis() + dMillis;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > expiredMillis;
    }

    @Override
    public void onUpdateEntry(Packet networkPacket) {
        expiredMillis = System.currentTimeMillis() + dMillis;
    }
}

//按使用的次数过期
class CounterCacheEntry extends CacheEntry {
    private int expiresCount;
    private int count;

    public CounterCacheEntry(int command, int type,int expiresCount) {
        super(CacheStrategy.counter, command, type);
        if (expiresCount <= 0) {
            throw new IllegalArgumentException("expiresCount >= 1,but: " + expiresCount);
        }
        this.expiresCount = expiresCount;
    }

    @Override
    public Packet getEntry() {
        count ++;
        return super.getEntry();
    }

    @Override
    public boolean isExpired() {
        return count >= expiresCount;
    }

    @Override
    public void onUpdateEntry(Packet networkPacket) {
        count = 0;
    }
}

//发现执行的命令后过期
class UseUtilConflictCacheEntry extends CacheEntry {
    private boolean expired = false;
    private int[] conflictCommands;

    public UseUtilConflictCacheEntry(int command, int type,int ...conflictCommands) {
        super(CacheStrategy.use_util_conflict, command, type);
        this.conflictCommands = conflictCommands;

        if (conflictCommands == null || conflictCommands.length == 0) {
            throw new IllegalArgumentException("conflict commands can not be null or empty");
        }
    }

    public void onReceiveCmd(int command) {
        for (int cmd : conflictCommands) {
            if (cmd == command) {
                expired = true;
                break;
            }
        }
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void onUpdateEntry(Packet networkPacket) {
        expired = false;
    }
}