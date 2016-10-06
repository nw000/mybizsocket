package com.dx168.bizsocket.tcp;

/**
 * Handles the automatic reconnection process. Every time a connection is dropped without
 * the application explicitly closing it, the manager automatically tries to reconnect to
 * the server.
 */
public class ReconnectionManager {
    private int RANDOM_BASE = 5;

    private SocketConnection connection;
    private ReconnectionManager.ReconnectionThread reconnectionThread;
    private boolean done = false;
    private boolean needRecnect = false;
    private PreReConnect mPreReConnect;

    public void bind(SocketConnection connection) {
        this.connection = connection;
        this.connection.removeConnectionListener(connectionListener);
        this.connection = connection;
        this.connection.addConnectionListener(connectionListener);
    }

    public boolean isNeedRecnect() {
        return needRecnect;
    }

    public boolean isReconnectionAllowed() {
        return !this.done;
    }

    public void setDone(boolean done) {
        this.done = done;

        if (done) {
            if (connection != null) {
                connection.removeConnectionListener(connectionListener);
                connection = null;
            }
            if (reconnectionThread != null) {
                reconnectionThread.interrupt();
            }
        }
    }

    public PreReConnect getPreReConnect() {
        return mPreReConnect;
    }

    public void setPreReConnect(PreReConnect mPreReConnect) {
        this.mPreReConnect = mPreReConnect;
    }

    public synchronized void reconnect() {
        if(this.isReconnectionAllowed()) {
            if(this.reconnectionThread != null && this.reconnectionThread.isAlive()) {
                return;
            }

            this.reconnectionThread = new ReconnectionManager.ReconnectionThread();
            this.reconnectionThread.setName("Reconnection Manager");
            this.reconnectionThread.setDaemon(true);
            this.reconnectionThread.start();
        }
    }

    class ReconnectionThread extends Thread {
        private int attempts = 0;

        ReconnectionThread() {
        }

        public void resetAttempts() {
            this.attempts = 0;
        }

        private int timeDelay() {
            ++this.attempts;
            return this.attempts > 9 ? ReconnectionManager.this.RANDOM_BASE * 3
                    : ReconnectionManager.this.RANDOM_BASE;
        }

        public void run() {
            while(ReconnectionManager.this.isReconnectionAllowed() && !isInterrupted()) {
                int timeDelay = this.timeDelay();

                while(ReconnectionManager.this.isReconnectionAllowed() && timeDelay > 0 && !isInterrupted()) {
                    try {
                        Thread.sleep(1000L);
                        --timeDelay;
                        for (ConnectionListener listener : connection.connectionListeners) {
                            listener.reconnectingIn(timeDelay);
                        }
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }

                try {
                    if (isReconnectionAllowed() && !isInterrupted()) {
                        if (mPreReConnect != null) {
                            mPreReConnect.doPreReConnect(connection);
                        }
                        else {
                            connection.reconnect();
                        }
                    }
                } catch (Exception exception) {
                    if (isReconnectionAllowed() && !isInterrupted()) {
                        connection.notifyConnectionError(exception);
                    }
                }
            }
        }
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connected(SocketConnection connection) {
            done = true;
            needRecnect = false;
            if (null != reconnectionThread) {
                reconnectionThread.resetAttempts();
            }
        }

        @Override
        public void connectionClosed() {
            done = true;
            needRecnect = false;
            if (null != reconnectionThread) {
                reconnectionThread.resetAttempts();
            }
        }

        @Override
        public void connectionClosedOnError(Exception exception) {
            onConnectionClosedOnError(exception);
        }

        @Override
        public void reconnectingIn(int time) {

        }
    };

    public void onConnectionClosedOnError(Exception exception) {
        done = false;
        if (!isReconnectionAllowed()) {
            return;
        }
        needRecnect = true;
        reconnect();
    }

    public interface PreReConnect {
        void doPreReConnect(SocketConnection connection);
    }
}
