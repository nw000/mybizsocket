package bizsocket.core.MyCode;

import java.util.Arrays;

/**
 * Created by dxjf on 16/11/7.
 */
public class SerialSignal {
    /**
     * 必须要等待的
     */
    public static final int FLAG_OREDRED = 0x1;

    private int entranceCommand; // 串行命令的入口

    private int[] strongReferences; // 必须等待的命令

    private int[] weekReferences; // 不是必须等待的命令

    private int flag;

    private Class<? extends AbstractSerialContext> serialContextType;

    public SerialSignal(Class<? extends AbstractSerialContext> serialContextType, int entranceCommand, int[] strongReference) {
        this(serialContextType, entranceCommand, strongReference, null);
    }

    public SerialSignal(Class<? extends AbstractSerialContext> serialContextType, int entranceCommand, int[] strongReference, int[] weekReferences) {
        this.serialContextType = serialContextType;
        this.entranceCommand = entranceCommand;
        this.strongReferences = strongReference;
        this.weekReferences = weekReferences;
    }

    public int[] getStrongReference() {
        return strongReferences;
    }

    public int[] getWeekReferences() {
        return weekReferences;
    }

    public Class<? extends AbstractSerialContext> getSerialContextType() {
        return serialContextType;
    }

    public int getEntranceCommand() {
        return entranceCommand;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public boolean isStrongReference(int cmd) {
        if (strongReferences != null) {
            for (int command : strongReferences) {
                if (cmd == command) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWeekReference(int cmd) {
        if (weekReferences != null) {
            for (int command : weekReferences) {
                if (cmd == command) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "SerialSignal{" +
                "entranceCommand=" + entranceCommand +
                ", strongReferences=" + Arrays.toString(strongReferences) +
                ", weekReferences=" + Arrays.toString(weekReferences) +
                '}';
    }
}
