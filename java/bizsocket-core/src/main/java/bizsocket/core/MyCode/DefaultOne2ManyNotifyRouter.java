package bizsocket.core.MyCode;

import bizsocket.core.PacketValidator;
import bizsocket.core.ResponseHandler;
import bizsocket.logger.Logger;
import bizsocket.logger.LoggerFactory;
import bizsocket.tcp.Packet;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dxjf on 16/11/7.
 */
public class DefaultOne2ManyNotifyRouter implements bizsocket.core.One2ManyNotifyRouter {
    private final Logger logger = LoggerFactory.getLogger(DefaultOne2ManyNotifyRouter.class.getSimpleName());

    private final Collection<StickyContext> stickyContexts = new CopyOnWriteArrayList<StickyContext>();
    private final ConcurrentHashMap<Integer, Packet> packetMap = new ConcurrentHashMap<Integer, Packet>();

    private final Collection<NotifyContext> notifyContexts = new CopyOnWriteArrayList<>();


    @Override
    public void addStickyCmd(int cmd, PacketValidator triggerPacketValidator) {
        stickyContexts.add(new StickyContext(cmd, triggerPacketValidator));
    }

    @Override
    public void removeStickyCmd(int cmd) {
        StickyContext context = null;
        for (StickyContext stickyContext : stickyContexts) {
            if (stickyContext.cmd == cmd) {
                context = stickyContext;
            }
        }

        if (context != null) {
            stickyContexts.remove(context);
            packetMap.remove(cmd);
        }

    }

    @Override
    public void subscribe(Object tag, int cmd, ResponseHandler responseHandler) {
        if (tag == null || responseHandler == null) {
            throw new IllegalArgumentException("tag or responseHandler can not be null");
        }

        notifyContexts.add(new NotifyContext(tag, cmd, responseHandler));
        Packet packet = null;
        if (stickyContexts.contains(cmd) && (packet = packetMap.get(cmd)) != null) {
            logger.debug("cmd" + cmd + "packet" + packet);
            responseHandler.sendSuccessMessage(cmd, null, packet);
        }


    }

    private void sendSuccessMessage(NotifyContext notifyContext, int cmd, Packet packet) {
        notifyContext.sendSuccessMessage(cmd, packet);
    }

    @Override
    public void unsubscribe(Object tagOrResponseHandler) {
        if (tagOrResponseHandler == null) {
            return;
        }

        CopyOnWriteArrayList<NotifyContext> list = new CopyOnWriteArrayList<>();
        for (NotifyContext notifyContext : notifyContexts) {
            if (notifyContext.tag == tagOrResponseHandler || notifyContext.responseHandler == tagOrResponseHandler) {
                list.add(notifyContext);
            }
        }
        notifyContexts.removeAll(list);

    }

    @Override
    public void route(int command, Packet packet) {
        if (command != packet.getCommand() || packet == null) {
            logger.error("can not route command:" + command + "packet" + packet);
            return;
        }
        for (NotifyContext notifyContext : notifyContexts) {
            if (command == notifyContext.cmd) {
                sendSuccessMessage(notifyContext, command, packet);
            }
        }
        StickyContext stickyContext = null;
        for (StickyContext context : stickyContexts) {
            if (context.cmd == command) {
                stickyContext = context;
                return;
            }
        }

        if (stickyContext != null && stickyContext.triggerPacketValidator != null
                && stickyContext.triggerPacketValidator.verify(packet)) {
            packet.setFlags(packet.getFlags() | Packet.FLAG_RECYCLED);
            packetMap.put(command, packet);
        }


    }

    private static class StickyContext {
        int cmd;

        PacketValidator triggerPacketValidator;

        public StickyContext(int cmd, PacketValidator triggerPacketbValidator) {
            this.cmd = cmd;
            this.triggerPacketValidator = triggerPacketbValidator;

            if (triggerPacketbValidator == null) {
                throw new IllegalArgumentException("PacketValidator can not be null !!!");
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            StickyContext stickyContext = (StickyContext) obj;
            return stickyContext.cmd == this.cmd;
        }

        @Override
        public int hashCode() {
            return cmd;
        }
    }

    private static class NotifyContext {

        private Object tag;
        private int cmd;
        private ResponseHandler responseHandler;

        public NotifyContext(Object tag, int cmd, ResponseHandler responseHandler) {
            this.tag = tag;
            this.cmd = cmd;
            this.responseHandler = responseHandler;
        }

        public void sendSuccessMessage(int cmd, Packet packet) {
            responseHandler.sendSuccessMessage(cmd, null, packet);
        }
    }
}
