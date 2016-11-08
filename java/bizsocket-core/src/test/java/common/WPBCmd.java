package common;

/**
 * 微盘宝命令枚举
 */
public enum WPBCmd {
    UNKNOWN(-1, ""),
    WEBSOCKET2(8888, "代替http请求的命令"),
    NOTIFY_PRICE(1, "白银报价"),
    CREATE_ORDER(2, "创建订单"),
    QUERY_ORDER_LIST(10006, "查询订单列表"),
    QUERY_ORDER_TYPE(51009, "查询当前持仓单类型"),
    HEARTBEAT(9999, "心跳");

    private int value;
    private String desc;

    WPBCmd(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return desc;
    }

    public static WPBCmd fromValue(int value) {
        for (WPBCmd WPBCmd : values()) {
            if (WPBCmd.getValue() == value) {
                return WPBCmd;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return "WPBCmd{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}


