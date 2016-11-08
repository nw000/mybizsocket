package bizsocket.core.MyCode;

import bizsocket.core.RequestContext;
import bizsocket.tcp.Packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dxjf on 16/11/8.
 */
public class InterceptorChain {
    private List<Interceptor> interceptors = Collections.synchronizedList(new ArrayList<Interceptor>());

    public void addInterceptor(Interceptor interceptor) {
        if (interceptors.contains(interceptor)) {
            return;
        }
        interceptors.add(interceptor);
    }

    public void removeInterceptor(Interceptor interceptor) {
        interceptors.remove(interceptor);
    }


    public boolean invokePostRequestHandle(RequestContext context) {
        for (Interceptor interceptor : interceptors) {
            try {
                if (interceptor.postRequestHandle(context)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean invokePesponseHandle(int cmd, Packet responsePacket) {
        for (Interceptor interceptor : interceptors) {
            try {
                if (interceptor.postResponseHandle(cmd, responsePacket)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
