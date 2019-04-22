package cn.jinelei.rainbow.smart.model.enums;

public enum DevOperateTag {
    RESERVED(0x00), DEV_OPERATE_QUERY_ONLINE(0x01), DEV_OPERATE_QUERY_SUDDEN_DEATH(0x01), DEV_OPERATE_QUERY_DEAD(0x01);
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
}