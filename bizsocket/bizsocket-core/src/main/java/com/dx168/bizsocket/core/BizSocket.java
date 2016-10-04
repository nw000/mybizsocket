package com.dx168.bizsocket.core;

import java.util.Map;

/**
 * Created by tong on 16/10/4.
 */
public interface BizSocket {
    /**
     * execute new request
     * @param tag
     * @param command
     * @param params
     * @param attach
     * @param responseHandler
     */
    void request(Object tag, int command, String params,Map<String,String> attach, ResponseHandler responseHandler);

    /**
     * cancel a request
     * @param tagOrResponseHandler
     */
    void cancel(Object tagOrResponseHandler);

    /**
     * subscribe notify
     * @param tag
     * @param cmd
     * @param responseHandler
     */
    void subscribe(Object tag,int cmd,ResponseHandler responseHandler);

    /**
     * unsubscribe notify
     * @param tagOrResponseHandler
     */
    void unsubscribe(Object tagOrResponseHandler);
}
