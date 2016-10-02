package server;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by tong on 16/10/2.
 */
public class Packet {
    //查询白银报价
    public static final int CMD_PRICE = 1;
    public static final int CMD_CREATE_ORDER = 2;
    public static final int CMD_WEBSOCKET = 8888;

    public int contentLen;
    public int cmd;
    public int seq;

    public String content;

    public Packet() {
    }

    public Packet(int cmd, int seq, String content) {
        this.cmd = cmd;
        this.seq = seq;
        this.content = content;
        this.contentLen = content.length();
    }

    public void setResponse(Map<String,String> map) {
        this.content = JSONUtil.map2json(map);
        this.contentLen = content.length();
    }

    public byte[] toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(bos));

        try {
            bufferedSink.writeInt(contentLen);
            bufferedSink.writeInt(cmd);
            bufferedSink.writeInt(seq);
            bufferedSink.write(ByteString.encodeUtf8(content));
            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }


    @Override
    public String toString() {
        return "Packet{" +
                "cmd=" + cmd +
                ", seq=" + seq +
                ", content='" + content + '\'' +
                '}';
    }

    public static Packet build(BufferedSource reader) throws IOException {
        Packet packet = new Packet();
        packet.contentLen = reader.readInt();
        packet.cmd = reader.readInt();
        packet.seq = reader.readInt();
        packet.content = reader.readString(packet.contentLen, Charset.forName("utf-8"));
        return packet;
    }
}
