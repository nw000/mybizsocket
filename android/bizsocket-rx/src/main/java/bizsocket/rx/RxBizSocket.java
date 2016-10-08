package bizsocket.rx;

import bizsocket.core.BizSocket;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by tong on 16/10/6.
 */
public class RxBizSocket {
    private final BizSocket bizSocket;
    private final JSONRequestConverter requestConverter;
    private final JSONResponseConverter responseConverter;

    RxBizSocket(BizSocket bizSocket, JSONRequestConverter requestConverter,JSONResponseConverter responseConverter) {
        this.responseConverter = responseConverter;
        this.bizSocket = bizSocket;
        this.requestConverter = requestConverter;
    }

    public BizSocket getBizSocket() {
        return bizSocket;
    }

    public JSONRequestConverter getRequestConverter() {
        return requestConverter;
    }

    public JSONResponseConverter getResponseConverter() {
        return responseConverter;
    }

    public <T> T create(final Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object... args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }

                        return new BizSocketCall().call(RxBizSocket.this,method,args);
                    }
                });
    }

    public static class Builder {
        private BizSocket bizSocket;
        private JSONRequestConverter requestConverter;
        private JSONResponseConverter responseConverter;

        public Builder bizSocket(BizSocket bizSocket) {
            this.bizSocket = bizSocket;
            return this;
        }

        public Builder requestConverter(JSONRequestConverter converter) {
            this.requestConverter = converter;
            return this;
        }

        public Builder responseConverter(JSONResponseConverter converter) {
            this.responseConverter = converter;
            return this;
        }

        public RxBizSocket build() {
            return new RxBizSocket(bizSocket,requestConverter,responseConverter);
        }
    }
}
