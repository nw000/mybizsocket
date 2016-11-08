package bizsocket.core.MyCode.request;

import java.net.SocketTimeoutException;

/**
 * Created by dxjf on 16/11/8.
 */
public class RequestTimeoutException extends SocketTimeoutException {
    public RequestTimeoutException() {

    }


    public RequestTimeoutException(String msg) {
        super(msg);
    }

}
