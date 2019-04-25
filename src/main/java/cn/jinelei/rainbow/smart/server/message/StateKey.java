package cn.jinelei.rainbow.smart.server.message;

public enum StateKey {
    RESERVED(0),
    LOGIN(1),
    LOGOUT(2),
    HEARTBEAT(3),
    QUERY_PRE_ONLINE_DEVS(4),
    QUERY_ONLINE_DEVS(5),
    QUERY_PRE_OFFLINE_DEVS(6),
    QUERY_OFFLINE_DEVS(7),
    ;

    private final int value;

    public static StateKey forNumber(int value) {
        for (StateKey type : StateKey.class.getEnumConstants()) {
            if (type.value == value)
                return type;
        }
        return RESERVED;
    }

    StateKey(int value) {
        this.value = value;
    }
}
