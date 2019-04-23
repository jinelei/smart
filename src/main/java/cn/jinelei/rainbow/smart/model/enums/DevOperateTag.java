package cn.jinelei.rainbow.smart.model.enums;

public enum DevOperateTag {
    RESERVED(0x00),
    DEV_OPERATE_QUERY_ONLINE(0x01),
    DEV_OPERATE_QUERY_SUDDEN_DEATH(0x02),
    DEV_OPERATE_QUERY_DEAD(0x03);
    byte value;

    DevOperateTag(int value) {
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

    public static DevFeature valueOf(Object obj) {
        int val = Integer.parseInt(obj.toString());
        return valueOf(val);
    }

    public static DevFeature valueOf(int val) {
        for (DevFeature DevFeature : DevFeature.class.getEnumConstants())
            if (DevFeature.value == val)
                return DevFeature;
        return null;
    }
}