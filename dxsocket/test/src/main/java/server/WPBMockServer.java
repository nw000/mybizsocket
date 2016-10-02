package server;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tong on 16/9/30.
 */
public class WPBMockServer {
    private static final List<ConnectThread> connectThreads = new CopyOnWriteArrayList<ConnectThread>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9103);

        new QuoteThread().start();
        boolean flag = true;
        while (flag) {
            Socket socket = serverSocket.accept();
            ConnectThread connectThread = new ConnectThread(socket);
            connectThreads.add(connectThread);
            connectThread.start();
        }
    }

    private static class QuoteThread extends Thread {
        @Override
        public void run() {
            List<ConnectThread> connections = connectThreads;

            boolean flag = true;
            while (flag) {
                DecimalFormat decimalFormat = new DecimalFormat("0.000");
                Map<String,String> map = new HashMap<String, String>();
                map.put("code","200");
                map.put("result",decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));
                map.put("lastPrice",decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));

                for (ConnectThread connectThread : connections) {
                    try {
                        connectThread.writePacket(new Packet(Packet.CMD_PRICE,0,JSONUtil.map2json(map)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(new Random().nextInt(5000) + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ConnectThread extends Thread {
        Socket socket;
        boolean isRunning = true;
        BufferedSource reader;
        BufferedSink writer;

        public ConnectThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("accept: " + socket);

                reader = Okio.buffer(Okio.source(socket.getInputStream()));
                writer = Okio.buffer(Okio.sink(socket.getOutputStream()));
                while (isRunning) {
                    Packet packet = Packet.build(reader);
                    handleRequest(packet);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    socket = null;
                }

                connectThreads.remove(this);
            }
        }

        private void handleRequest(Packet packet) throws IOException {
            System.out.println("handleRequest: " + packet);
            int cmd = packet.cmd;
            switch (cmd) {
                case Packet.CMD_PRICE: {
                    DecimalFormat decimalFormat = new DecimalFormat("0.000");
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("code","200");
                    map.put("result",decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));
                    map.put("lastPrice",decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));
                    packet.setResponse(map);
                    writePacket(packet);
                }
                break;
                case Packet.CMD_CREATE_ORDER: {
                    Map<String,String> params = JSONUtil.json2map(packet.content);
                    int productId = -1;
                    try {
                        productId = Integer.valueOf(params.get("productId"));
                    } catch (NumberFormatException e) {
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("code","-1");
                        map.put("msg","产品类型不正确");
                        packet.setResponse(map);
                        writePacket(packet);
                        return;
                    }

                    Map<String,String> map = new HashMap<String, String>();
                    map.put("code","200");
                    map.put("msg","创建成功");
                    map.putAll(params);
                    packet.setResponse(map);
                    writePacket(packet);
                }
                break;
            }
        }

        public void writePacket(Packet packet) throws IOException {
            writer.write(packet.toBytes());
            writer.flush();
        }
    }
}
