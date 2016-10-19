package bizsocket.core;

import bizsocket.logger.Logger;
import bizsocket.logger.LoggerFactory;
import bizsocket.tcp.Packet;
import java.util.concurrent.TimeUnit;

/**
 * Created by tong on 16/10/5.
 */
public abstract class CacheEntry {
    private final Logger logger = LoggerFactory.getLogger(CacheEntry.class.getSimpleName());

    /**
     * 过期不使用
     */
    public static final int TYPE_EXPIRED_NOT_USE = 0;

    /**
     * 过期使用并且刷新缓存
     */
    public static final int TYPE_EXPIRED_USE_AND_REFRESH = 1;

    private int command;
    private CacheStrategy strategy;
    private Packet packet;
    private int type = TYPE_EXPIRED_NOT_USE;
    private PacketValidator validator;

    CacheEntry(CacheStrategy strategy,int command, int type,PacketValidator validator) {
        if (type != TYPE_EXPIRED_NOT_USE
                && type != TYPE_EXPIRED_USE_AND_REFRESH) {
            throw new IllegalArgumentException("type == TYPE_EXPIRED_NOT_USE(" + TYPE_EXPIRED_NOT_USE + ") || type == TYPE_EXPIRED_USE_AND_REFRESH(" + TYPE_EXPIRED_USE_AND_REFRESH + ") ,param type: " + type);
        }

        this.strategy = strategy;
        this.command = command;
        this.type = type;
        this.validator = validator;

//        if (this.validator == null) {
//            throw new IllegalArgumentException("validator can not be null");
//        }
    }

    public abstract boolean isExpired();
    abstract void onUpdateEntry(Packet networkPacket);

    public void updateEntry(Packet networkPacket) {
        if (networkPacket == null) {
            return;
        }
        if (packet != null && packet.getCommand() != networkPacket.getCommand()) {
            throw new IllegalArgumentException("can not update packet, expect cmd: " + packet.getCommand() + " but param cmd is " + networkPacket.getCommand());
        }
        if (validator == null || validator.verify(networkPacket)) {
            this.packet = networkPacket;
            logger.debug("save or update cache packet: " + packet);
            onUpdateEntry(networkPacket);
        }
        else {
            if (validator != null) {
                logger.debug("ignore cache; check fail packet: " + networkPacket);
            }
        }
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

    public static CacheEntry createPersistence(int command,PacketValidator validator) {
        return new PersistenceCacheEntry(command,validator);
    }

    public static CacheEntry createRelativeMillis(int command, int type,TimeUnit unit,long duration,PacketValidator validator) {
        return new RelativeMillisCacheEntry(command,type,unit,duration,validator);
    }

    public static CacheEntry createCounter(int command, int type,int expiresCount,PacketValidator validator) {
        return new CounterCacheEntry(command,type,expiresCount,validator);
    }

    public static CacheEntry createUseUtilSendCmd(int command, int type,int[] conflictCommands,PacketValidator validator) {
        return new UseUtilSendCmdCacheEntry(command, type, conflictCommands,validator);
    }

    public static CacheEntry createUseUtilReceiveCmd(int command, int type,int[] conflictCommands,PacketValidator validator) {
        return new UseUtilReceiveCmdCacheEntry(command, type, conflictCommands,validator);
    }
}

//永不过期
class PersistenceCacheEntry extends CacheEntry {
    public PersistenceCacheEntry(int command,PacketValidator validator) {
        super(CacheStrategy.persistence, command, TYPE_EXPIRED_NOT_USE,validator);
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    void onUpdateEntry(Packet networkPacket) {

    }
}

//按缓存的时间过期
class RelativeMillisCacheEntry extends CacheEntry {
    private long dMillis;
    private long expiredMillis;

    public RelativeMillisCacheEntry(int command, int type,TimeUnit unit,long duration,PacketValidator validator) {
        super(CacheStrategy.relative_millis, command, type,validator);
        dMillis = unit.toMillis(duration);
        expiredMillis = System.currentTimeMillis() + dMillis;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > expiredMillis;
    }

    @Override
    void onUpdateEntry(Packet networkPacket) {
        expiredMillis = System.currentTimeMillis() + dMillis;
    }
}

//按使用的次数过期
class CounterCacheEntry extends CacheEntry {
    private int expiresCount;
    private int count;

    public CounterCacheEntry(int command, int type,int expiresCount,PacketValidator validator) {
        super(CacheStrategy.counter, command, type,validator);
        if (expiresCount <= 0) {
            throw new IllegalArgumentException("expiresCount >= 1,but: " + expiresCount);
        }
        this.expiresCount = expiresCount;
    }

    @Override
    public Packet getEntry() {
        Packet packet = super.getEntry();
        return packet;
    }

    @Override
    public boolean isExpired() {
        return count >= expiresCount;
    }

    @Override
    void onUpdateEntry(Packet networkPacket) {
        count = 0;
    }

    public void addCount() {
        count++;
    }
}

//接收指定的的命令后过期
class UseUtilSendCmdCacheEntry extends CacheEntry {
    private boolean expired = false;
    private int[] conflictCommands;

    public UseUtilSendCmdCacheEntry(int command, int type, int[] conflictCommands,PacketValidator validator) {
        super(CacheStrategy.use_util_conflict, command, type,validator);
        this.conflictCommands = conflictCommands;

        if (conflictCommands == null || conflictCommands.length == 0) {
            throw new IllegalArgumentException("conflict commands can not be null or empty");
        }
    }

    public void onSendCmd(int command) {
        if (getEntry() == null) {
            return;
        }
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
    void onUpdateEntry(Packet networkPacket) {
        expired = false;
    }
}

//接收指定的的命令后过期
class UseUtilReceiveCmdCacheEntry extends CacheEntry {
    private boolean expired = false;
    private int[] conflictCommands;

    public UseUtilReceiveCmdCacheEntry(int command, int type, int[] conflictCommands,PacketValidator validator) {
        super(CacheStrategy.use_util_conflict, command, type,validator);
        this.conflictCommands = conflictCommands;

        if (conflictCommands == null || conflictCommands.length == 0) {
            throw new IllegalArgumentException("conflict commands can not be null or empty");
        }
    }

    public void onReceiveCmd(int command) {
        if (getEntry() == null) {
            return;
        }
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
    void onUpdateEntry(Packet networkPacket) {
        expired = false;
    }
}