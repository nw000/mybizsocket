package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tong on 16/9/30.
 */
public class TestServer1 {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);

            Socket socket = serverSocket.accept();
            System.out.println("accept: " + socket);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write("bye!\n");
            bw.flush();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            socket.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
