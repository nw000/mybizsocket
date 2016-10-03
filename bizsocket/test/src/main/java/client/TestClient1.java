package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by tong on 16/9/30.
 */
public class TestClient1 {
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("localhost",8888);
            InputStream is = socket.getInputStream();
            int len = -1;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer,0,len);
            }
            System.out.println(new String(bos.toByteArray()));
            socket.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
