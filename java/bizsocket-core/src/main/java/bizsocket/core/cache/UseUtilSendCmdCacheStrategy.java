package bizsocket.core.cache;

import bizsocket.core.PacketValidator;
import bizsocket.tcp.Packet;

/**
 * 发送指定的的命令后移除缓存
 * Created by tong on 16/10/21.
 */
public class UseUtilSendCmdCacheStrategy extends UseUtilReceiveCmdCacheStrategy {
    public UseUtilSendCmdCacheStrategy(int command, int[] conflictCommands) {
        super(command, conflictCommands);
    }

    public UseUtilSendCmdCacheStrategy(int command, int[] conflictCommands, PacketValidator validator) {
        super(command, conflictCommands, validator);
    }

    @Override
    public void onSendSuccessful(Packet packet) {
        processTriggerPacket(packet);
    }

    @Override
    public void processPacket(Packet packet) {

    }
}
