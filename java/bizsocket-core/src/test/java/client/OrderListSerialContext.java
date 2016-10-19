package client;

import bizsocket.core.RequestContext;
import bizsocket.core.RequestQueue;
import bizsocket.core.AbstractSerialContext;
import bizsocket.core.SerialSignal;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import common.*;
import common.WPBPacket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tong on 16/9/27.
 */
public class OrderListSerialContext extends AbstractSerialContext {
    private static final String TAG = OrderListSerialContext.class.getSimpleName();

    private WPBPacket orderListPacket;
    private int[] orderIdArr;
    private int[] orderQuerySeqArr;

    private Map<Integer,Packet> orderTypeMap = new ConcurrentHashMap();

    public OrderListSerialContext(SerialSignal serialSignal, RequestContext requestContext) {
        super(serialSignal, requestContext);
    }

    @Override
    public String getRequestPacketId() {
        String packetId = getRequestContext().getRequestPacket().getPacketID();
        return packetId;
    }

    @Override
    public boolean shouldProcess(RequestQueue requestQueue, Packet packet) {
        boolean result = super.shouldProcess(requestQueue,packet);
        if (!result) {
            return false;
        }

        WPBPacket responsePacket = (WPBPacket) packet;
        JSONObject obj = null;
        try {
            obj = new JSONObject(responsePacket.getContent());
        } catch (JSONException e) {
            return false;
        }
        if (packet.getCommand() == WPBCmd.QUERY_ORDER_LIST.getValue()) {
            if (!WPBProtocolUtil.isSuccessResponsePacket(responsePacket)) {
                return false;
            }
            //订单列表信息
            try {
                JSONArray resultArr = obj.optJSONArray("result");

                PacketFactory packetFactory = requestQueue.getBizSocket().getPacketFactory();
                if (resultArr == null || resultArr.length() == 0 || packetFactory == null) {
                    throw new Exception("");
                }

                orderListPacket = (WPBPacket) packet;
                orderIdArr = new int[resultArr.length()];
                orderQuerySeqArr = new int[resultArr.length()];
                for (int i = 0;i < resultArr.length();i++) {
                    JSONObject order = resultArr.optJSONObject(i);
                    if (order == null) {
                        continue;
                    }

                    int orderId = order.optInt("orderId",-1);
                    if (orderId == -1) {
                        continue;
                    }

                    orderIdArr[i] = orderId;
                    //发起查询订单类型的查询
                    WPBPacket wpbPacket = buildQueryOrderTypePacket(requestQueue,orderId);
                    
                    orderQuerySeqArr[i] = Integer.valueOf(wpbPacket.getPacketID());
                    if (wpbPacket != null) {
                        System.out.println("同步请求订单类型: " + wpbPacket.getContent());
                        requestQueue.getBizSocket().getSocketConnection().sendPacket(wpbPacket);
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        else if (packet.getCommand() == WPBCmd.QUERY_ORDER_TYPE.getValue()) {
            if (orderIdArr == null) {
                return false;
            }
            WPBPacket wpbPacket = (WPBPacket) packet;

            boolean contain = false;
            int orderid = obj.optInt("orderId");
            for (int i = 0; i < orderIdArr.length; i++) {
                if (orderid == orderIdArr[i] && Integer.valueOf(wpbPacket.getPacketID()) == orderQuerySeqArr[i]) {
                    contain = true;
                    System.out.println("收到订单类型: " + wpbPacket.getContent());
                    orderTypeMap.put(orderid,packet);
                    break;
                }
            }

            if (!contain) {
                return false;
            }
        }
        return true;
    }

    private WPBPacket buildQueryOrderTypePacket(RequestQueue requestQueue, int orderId) {
        JSONObject params = new JSONObject();
        try {
            params.put("orderId",String.valueOf(orderId));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            return (WPBPacket) requestQueue.getBizSocket().getPacketFactory().getRequestPacket(new Request.Builder().command(WPBCmd.QUERY_ORDER_TYPE.getValue()).utf8body(params.toString()).build());
        } catch (Throwable e) {

        }

        return null;
    }

    @Override
    public Packet processPacket(RequestQueue requestQueue, Packet packet) {
        if (orderListPacket != null && orderIdArr != null && orderTypeMap.size() == orderIdArr.length) {
            try {
                JSONObject obj = new JSONObject(orderListPacket.getContent());
                JSONArray resultArr = obj.optJSONArray("result");
                for (int i = 0;i < resultArr.length();i++) {
                    JSONObject order = resultArr.optJSONObject(i);
                    int orderId = order.optInt("orderId");

                    JSONObject orderType = new JSONObject(((WPBPacket)orderTypeMap.get(orderId)).getContent());
                    order.put("orderType",orderType.optInt("ordertype",0));
                    order.put("orderTypeRes",orderType);
                }

                WPBPacket wpbPacket = (WPBPacket) packet;
                wpbPacket.setCommand(orderListPacket.getCommand());
                wpbPacket.setContent(orderListPacket.getContent());
                wpbPacket.setPacketID(orderListPacket.getPacketID());

                System.out.println("合并订单列表和类型: " + orderListPacket.getContent());
                return wpbPacket;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
