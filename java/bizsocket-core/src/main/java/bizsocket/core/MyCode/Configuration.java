package bizsocket.core.MyCode;

import java.util.concurrent.TimeUnit;

/**
 * Created by dxjf on 16/11/3.
 */
public class Configuration {
    public static final int DEFULT_READ_TIMEOUT = 30;

    public static final int HEART_BEAT_INTERVAL = 20;

    private long readTimeout = DEFULT_READ_TIMEOUT;

    private String host;// socket server host;

    private int port;// 端口

    private int heartbeat;//心跳间隔

    private boolean logEnable;

    private String logTag = "SocketClient";

    private Configuration actual;

    Configuration() {

    }

    protected Configuration(int readTimeout, String host, int port, int heartbeat) {
        this.readTimeout = readTimeout;
        this.host = host;
        this.port = port;
        heartbeat = heartbeat;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setLogEnable(boolean logEnable) {
        this.logEnable = logEnable;
    }

    public boolean isLogEnable() {
        return logEnable;
    }

    public void setLogTag(String logTag) {
        this.logTag = logTag;
    }

    public String getLogTag() {
        return logTag;
    }

    public void apply(Configuration configuration) {
        if (configuration == null) {
            return;
        }
        this.readTimeout = configuration.getReadTimeout();
        this.port = configuration.getPort();
        this.host = configuration.getHost();
        this.heartbeat = configuration.heartbeat;
        this.actual = configuration;
    }

    public static class Builder {
        private Configuration configuration;

        public Builder() {
            configuration = new Configuration();
        }

        public Builder readTimeout(TimeUnit unit, long duration) {
            configuration.setReadTimeout(unit.toSeconds(duration));
            return this;
        }

        public Builder host(String host) {
            configuration.setHost(host);
            return this;
        }

        public Builder port(int port) {
            configuration.setPort(port);
            return this;
        }


        public Builder heartbeat(int heartbeat) {
            configuration.setHeartbeat(heartbeat);
            return this;
        }

        public Configuration build() {
            if (configuration.readTimeout < 5) {
                configuration.setReadTimeout(DEFULT_READ_TIMEOUT);
            }

            if (configuration.getHost() == null || "".equals(configuration.getHost().trim())) {
                throw new IllegalArgumentException("Invalid port !");
            }

            if (configuration.getPort() <= 0 || configuration.getPort() > 65535) {
                throw new IllegalArgumentException("Invalid port !!!");
            }

            if (configuration.getHeartbeat() <= HEART_BEAT_INTERVAL) {
                configuration.setHeartbeat(HEART_BEAT_INTERVAL);
            }

            return configuration;
        }
    }
}
