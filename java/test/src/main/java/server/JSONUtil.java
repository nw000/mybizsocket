package server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tong on 16/10/2.
 */
public class JSONUtil {
    public static String map2json(Map<String,String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<String,String> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"");
            sb.append(entry.getKey());
            sb.append("\"");
            sb.append(":");
            sb.append("\"");
            sb.append(entry.getValue());
            sb.append("\"");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    public static Map<String,String> json2map(String json) {
        Map<String,String> map = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder(json);
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);

        String[] keyVale = sb.toString().split(",");
        //"${key}" : "${value}"
        for (String str : keyVale) {
            str = str.replaceAll("\"","");
            map.put(str.split(":")[0],str.split(":")[1]);
        }
        return map;
    }
}
