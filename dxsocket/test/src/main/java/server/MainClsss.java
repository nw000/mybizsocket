package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by tong on 16/10/2.
 */
public class MainClsss {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String json = "{\"productId\" : \"1\",\"isJuan\" : \"0\",\"type\" : \"2\",\"sl\" : \"1\"}";
        bos.write(json.length());
        bos.write(2);
        bos.write(100);
        bos.write(json.getBytes());
        System.out.println(new String(bos.toByteArray()));
    }
}
