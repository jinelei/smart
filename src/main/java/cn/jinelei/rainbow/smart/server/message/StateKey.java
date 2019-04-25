package cn.jinelei.rainbow.smart.server.message;

public enum StateKey {
    RESERVED(0),
    LOGIN(1),
    LOGOUT(2),
    HEARTBEAT(3),
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
