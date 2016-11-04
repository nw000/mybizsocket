package bizsocket.core.MyCode;

import okio.ByteString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dxjf on 16/11/3.
 */
public class Request {
    private Object tag;
    private int command;
    private ByteString body;
    private Map attach;
    private String description;
    private boolean recycleOnSend; //自动回收请求包

    public Object getTag() {
        return tag;
    }

    public int getCommand() {
        return command;
    }

    public ByteString getBody() {
        return body;
    }

    public Map getAttach() {
        return attach;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRecycleOnSend() {
        return recycleOnSend;
    }

    public void putToAttach(Object key, Object value) {
        if (attach == null) {
            attach = new HashMap();
        }
        attach.put(key, value);
    }

    public Object getFromAttach(Object key) {
        if (attach != null) {
            return attach.get(key);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Request{" +
                "command=" + command +
                ", body=" + body +
                ", description='" + description + '\'' +
                '}';
    }

    public static class Build {
        private final Request request;

        public Build() {
            this.request = new Request();
        }

        public Build(Request request) {
            this.request = new Request();
            this.request.tag = request.tag;
            this.request.command = request.command;
            this.request.body = request.body;
            this.request.attach = request.attach;
        }

        public Build tag(Object tag) {
            this.request.tag = tag;
            return this;
        }

        public Build command(int command) {
            this.request.command = command;
            return this;
        }

        public Build body(ByteString body) {
            this.request.body = body;
            return this;
        }

        public Build utf8Body(String utf8Body) {
            this.request.body = ByteString.encodeUtf8(utf8Body);
            return this;
        }

        public Build attach(Map attach) {
            request.attach = attach;
            return this;
        }

        public Build description(String description) {
            request.description = description;
            return this;
        }

        public Build recycleOnSend(boolean recycleOnSend) {
            request.recycleOnSend = recycleOnSend;
            return this;
        }

        public Request build() {
            return request;
        }

    }

}
