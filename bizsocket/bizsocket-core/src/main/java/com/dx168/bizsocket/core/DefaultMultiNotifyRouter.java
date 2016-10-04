package com.dx168.bizsocket.core;

import com.dx168.bizsocket.tcp.Packet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tong on 16/10/4.
 */
public class DefaultMultiNotifyRouter implements MultiNotifyRouter {
    private final Collection<NotifyContext> notifyContexts = new CopyOnWriteArrayList<NotifyContext>();

    @Override
    public void subscribe(Object tag, int cmd, int flags, ResponseHandler responseHandler) {
        if (tag == null || responseHandler == null) {
            return;
        }
        notifyContexts.add(new NotifyContext(tag,cmd,flags,responseHandler));
    }

    @Override
    public void unsubscribe(Object tagOrResponseHandler) {
        if (tagOrResponseHandler == null) {
            return;
        }
        List<NotifyContext> preDelList = new ArrayList<NotifyContext>();
        for (NotifyContext notifyContext : notifyContexts) {
            if (notifyContext.tag == tagOrResponseHandler || notifyContext.responseHandler == tagOrResponseHandler) {
                preDelList.add(notifyContext);
            }
        }

        notifyContexts.removeAll(preDelList);
    }

    @Override
    public void route(int command, Packet packet) {
        List<NotifyContext> preDelList = null;
        for (NotifyContext notifyContext : notifyContexts) {
            if (notifyContext.cmd == command) {
                notifyContext.responseHandler.sendSuccessMessage(command, null, null, packet);

                if ((notifyContext.flags & MultiNotifyRouter.FLAG_ONCE_CALL) != 0) {
                    if (preDelList == null) {
                        preDelList = new ArrayList<NotifyContext>();
                    }
                    preDelList.add(notifyContext);
                }
            }
        }

        if (preDelList != null && !preDelList.isEmpty()) {
            notifyContexts.removeAll(preDelList);
        }
    }

    public static class NotifyContext {
        int flags;
        int cmd;
        Object tag;
        ResponseHandler responseHandler;

        public NotifyContext(Object tag,int cmd,int flags,ResponseHandler responseHandler) {
            this.flags = flags;
            this.cmd = cmd;
            this.tag = tag;
            this.responseHandler = responseHandler;
        }
    }
}
