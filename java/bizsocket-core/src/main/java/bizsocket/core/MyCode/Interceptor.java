package bizsocket.core.MyCode;

import bizsocket.core.RequestContext;
import bizsocket.tcp.Packet;

/**
 * Created by dxjf on 16/11/7.
 */
public interface Interceptor {
    /**
     * 决定是否拦截请求
     * @return true 拦截请求
     * @throws Exception
     */
    boolean postRequestHandle(RequestContext context) throws Exception;

    /**
     * 决定是否拦截响应
     * @return true 拦截响应
     * @throws Exception
     */
    boolean postResponseHandle(int command, Packet responsePacket) throws Exception;
}
