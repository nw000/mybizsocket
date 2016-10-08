package bizsocket.rx;

import okio.ByteString;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * Created by tong on 16/10/6.
 */
public class JSONRequestConverter {
    public ByteString converter(Method method, Object... args) throws Exception{
        JSONObject obj = new JSONObject();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            Query query = null;
            if ((query = parameter.getAnnotation(Query.class)) != null) {
                try {
                    obj.put(query.value(), args[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (parameter.getAnnotation(QueryMap.class) != null) {
                Object keyValueStore = args[i];
                if (keyValueStore instanceof Map) {
                    Map map = (Map) keyValueStore;
                    for (Object key : map.keySet()) {
                        try {
                            obj.put(String.valueOf(key), map.get(key));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (keyValueStore instanceof JSONObject) {
                    JSONObject jobj = (JSONObject) keyValueStore;
                    JSONArray names = jobj.names();
                    for (int j = 0;i < names.length();i++) {
                        obj.put(names.optString(j),jobj.opt(names.optString(j)));
                    }
                }
            }
        }
        return ByteString.encodeUtf8(obj.toString());
    }
}
