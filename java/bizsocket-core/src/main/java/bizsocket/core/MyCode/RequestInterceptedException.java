package bizsocket.core.MyCode;

/**
 * Created by dxjf on 16/11/8.
 */
public class RequestInterceptedException extends RuntimeException {
    public RequestInterceptedException() {

    }

    public RequestInterceptedException(String msg) {
        super(msg);
    }
}
