package bizsocket.rx;

import bizsocket.core.ResponseHandler;
import bizsocket.tcp.Packet;
import com.google.gson.internal.$Gson$Types;
import okio.ByteString;
import rx.Observable;
import rx.Subscriber;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tong on 16/10/6.
 */
public class BizSocketCall {
    public Observable<?> call(final RxBizSocket rxBizSocket, final Method method,Object... args) throws Exception {
        Request request = null;
        if ((request = method.getAnnotation(Request.class)) == null) {
            throw new IllegalArgumentException("Can not found annotation(" + Request.class.getPackage() + "." + Request.class.getSimpleName() + ")");
        }
        final int command = request.cmd();
        final Object tag = getTag(method,args);
        if (tag == null) {
            throw new IllegalArgumentException("request tag can not be null(@Tag tag)");
        }
        final ByteString requestBody = getRequestBody(rxBizSocket,method,args);
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                rxBizSocket.getBizSocket().request(tag, command, requestBody, new ResponseHandler() {
                    @Override
                    public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                        Object response = rxBizSocket.getResponseConverter().convert(getResponseType(method),responsePacket);
                        subscriber.onNext(response);
                    }

                    @Override
                    public void sendFailureMessage(int command, Throwable error) {
                        subscriber.onError(error);
                    }
                });
            }
        });
    }

    public Type getResponseType(Method method) {
        if (method.getReturnType() != Observable.class) {
            throw new IllegalStateException("return type must be rx.Observable");
        }
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            type = $Gson$Types.canonicalize(((ParameterizedType)type).getActualTypeArguments()[0]);
        }

        if ("rx.Observable".equals(type.getTypeName())) {
            throw new IllegalStateException("the generic value of the return value is unspecified; support " + "rx.Observable<String> | rx.Observable<JSONObject> | rx.Observable<JavaBean>");
        }
        return type;
    }

    public Object getTag(Method method,Object... args) {
        Parameter tagParameter = null;
        int tagParameterPosition = -1;
        int index = 0;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(Tag.class) != null) {
                if (tagParameter != null) {
                    throw new IllegalArgumentException("tag conflict " + tagParameter.getName() + " " + parameter.getName() + " ,at " + method.toString());
                }
                tagParameter = parameter;
                tagParameterPosition = index;
            }
            index++;
        }

        if (tagParameterPosition != -1) {
            return args[tagParameterPosition];
        }
        return null;
    }

    public ByteString getRequestBody(RxBizSocket rxBizSocket, Method method, Object[] args) throws Exception {
        return rxBizSocket.getRequestConverter().converter(method, args);
    }
}
