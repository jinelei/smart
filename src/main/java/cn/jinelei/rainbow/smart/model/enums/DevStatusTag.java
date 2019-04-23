package cn.jinelei.rainbow.smart.model.enums;

public enum DevStatusTag {
    RESERVED(0x00),
    DEV_LOGIN(0x01),
    DEV_LOGOUT(0x02),
    DEV_HEARTBEAT(0x03);
    byte value;

    DevStatusTag(int value) {
        this.value = (byte) value;
    }

    /**
     * @return the value
     */
    public byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static DevStatusTag valueOf(Object obj) {
        int val = Integer.parseInt(obj.toString());
        return valueOf(val);
    }

    public static DevStatusTag valueOf(int val) {
        for (DevStatusTag DevStatusTag : DevStatusTag.class.getEnumConstants())
            if (DevStatusTag.value == val)
                return DevStatusTag;
        return null;
    }
}