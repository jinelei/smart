package cn.jinelei.rainbow.smart.model.enums;

public enum Category {
    RESERVED(0x00), DEV_STATUS(0x01), DEV_OPERATE(0x02);
    byte value;

    Category(int value) {
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