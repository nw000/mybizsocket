package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tong on 16/9/30.
 */
public class TestServer3 {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        boolean flag = true;
        while (flag) {
            Socket socket = serverSocket.accept();
            System.out.println("accept: " + socket);

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str = br.readLine();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(str + "\n");
            bw.flush();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            socket.close();
        }
    }

    private class ClientSocket implements Runnable {
        private Socket socket;

        public ClientSocket(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

        }
    }
}
