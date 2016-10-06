package bizsocket.core;

import okio.ByteString;

/**
 * Created by tong on 16/10/4.
 */
public interface BizSocket {
    /**
     * execute new request
     * @param tag
     * @param command
     * @param requestBody
     * @param responseHandler
     */
    void request(Object tag, int command, ByteString requestBody, ResponseHandler responseHandler);

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
