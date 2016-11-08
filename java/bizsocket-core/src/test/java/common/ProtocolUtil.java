package common;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;

/**
 * Created by tong on 16/9/14.
 */
public class ProtocolUtil {
    public static<T> T getProtocolFieldValue(Object target,Class<T> fieldClass,String fieldName,Object defaultValue) throws JSONException, NoSuchFieldException, IllegalAccessException {
        if (fieldClass != int.class
                && fieldClass != String.class) {
            throw new RuntimeException("fieldClass == int.class || fieldClass == String.class");
        }

        if (target instanceof String) {
            target = new JSONObject((String) target);
        }

        if (target instanceof JSONObject) {
            if (fieldClass == int.class || fieldClass == Integer.class) {
                if (defaultValue == null) {
                    defaultValue = Integer.valueOf(-1);
                }
                return (T)Integer.valueOf(((JSONObject) target).optInt(fieldName,(Integer)defaultValue));
            }
            else {
                if (defaultValue == null) {
                    defaultValue = "";
                }
                return (T)String.valueOf(((JSONObject) target).optString(fieldName,(String)defaultValue));
            }
        }
        else {
            Class clazz = target.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            Object result = field.get(target);
            if (result == null) {
                result = defaultValue;
            }
            return (T)result;
        }
    }

    public static String getStringProtocolFieldValue(Object target,String fieldName,Object defaultValue) throws JSONException, NoSuchFieldException, IllegalAccessException {
        return getProtocolFieldValue(target,String.class,fieldName,defaultValue);
    }

    public static int getIntProtocolFieldValue(Object target,String fieldName,Object defaultValue) throws JSONException, NoSuchFieldException, IllegalAccessException {
        return getProtocolFieldValue(target,int.class,fieldName,defaultValue);
    }
}
