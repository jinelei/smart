package cn.jinelei.rainbow.smart.server.message;

public enum Type {
    RESERVED(0),
    STATE(1),
    ;

    private final int value;

    public static Type forNumber(int value) {
        for (Type type : Type.class.getEnumConstants()) {
            if (type.value == value)
                return type;
        }
        return RESERVED;
    }

    public boolean equals(int value) {
        return this.value == value;
    }

    Type(int value) {
        this.value = value;
    }

}
