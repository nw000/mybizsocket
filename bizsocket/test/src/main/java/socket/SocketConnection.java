package socket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by tong on 16/10/3.
 */
public class SocketConnection {
    private Socket socket;

    public void connect(String host, int port,int timeout) throws IOException {
        Socket socket = createSocket(host,port);
        socket.setSoTimeout(timeout);
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    public void disconnectAfterReadingAndWriting() {

    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    protected Socket createSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

//    public void handleReadWriteError(Exception e) {
//
//    }
//
//    public void notifyConnectionError(Exception e) {
//
//    }

//    public interface Listener {
//        void onSocketConnected(String host,int port);
//
//        void onSocketClosed();
//
//        void processPacket(Packet packet, SocketConnection socketConnection);
//    }
}
