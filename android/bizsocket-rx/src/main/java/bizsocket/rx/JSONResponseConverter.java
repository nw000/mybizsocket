package bizsocket.rx;

import bizsocket.tcp.Packet;
import com.google.gson.Gson;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * Created by tong on 16/10/6.
 */
public class JSONResponseConverter {
    public Object convert(Type type,Packet packet) {
        if (type == String.class) {
            return packet.getContent();
        }
        else if ("org.json.JSONObject".equals(type.getTypeName())) {
            try {
                Class clazz =  Class.forName("org.json.JSONObject");
                Constructor constructor =  clazz.getConstructor(String.class);
                return constructor.newInstance(packet.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            return new Gson().fromJson(packet.getContent(),type);
        }
        return null;
    }
}
