package com.dx168.bizsocket.tcp;

import okio.BufferedSource;

/**
 * Listens for data from the tcp server and parses it into packet objects.
 * The packet reader also invokes all packet listeners.
 */
class PacketReader {
    private Thread readerThread;
    private final SocketConnection connection;
    private BufferedSource reader;
    volatile boolean done = false;

    public PacketReader(SocketConnection connection) {
        this.connection = connection;
        this.init();
    }

    /**
     * Initializes the reader in order to be used. The reader is initialized during the
     * first connection and when reconnecting due to an abruptly disconnection.
     */
    protected void init() {
        this.reader = connection.getReader();
        done = false;

        readerThread = new Thread() {
            public void run() {
                parsePackets(this);
            }
        };
        readerThread.setName("Packet Reader");
        readerThread.setDaemon(true);
    }

    /**
     * Starts the packet reader thread and returns once a connection to the server
     * has been established or if the server's features could not be parsed within
     * the connection's PacketReplyTimeout.
     *
     */
    public synchronized void startup() {
        done = false;

        readerThread.start();
    }

    /**
     * Shuts the packet reader down. This method simply sets the 'done' flag to true.
     */
    public void shutdown() {
        done = true;

        readerThread.interrupt();
    }

    void setReader(BufferedSource reader) {
        this.reader = reader;
    }

    /**
     * Parse top-level packets in order to process them further.
     *
     * @param thisThread
     */
    private void parsePackets(Thread thisThread) {
        while (!this.done && this.readerThread == thisThread) {
            Packet packet = null;
            try {
                packet = connection.getPacketFactory().buildPacket(reader);
                if (packet != null && !done) {
                    connection.handlerReceivedPacket(packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (!done) {
                    connection.handleReadWriteError(e);
                }
            }

            try {
                if (!done) {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {

            }
        }
    }
}
