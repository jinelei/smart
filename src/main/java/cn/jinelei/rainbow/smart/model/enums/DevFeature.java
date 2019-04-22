package cn.jinelei.rainbow.smart.model.enums;

public enum DevFeature {
    RESERVED(0x00), LIGHT_SENSOR(0x01), HUMAN_SENSOR(0x02), MICROPHONE(0x04), SPEAKER(0x08), ELETRIC_RELAY(0x10),
    WS2812(0x20), TEMPERATURE_SENSOR(0x40), HUMIDITY_SENSOR(0x80), RAINFALL_SENSOR(0x100), CAMERA(0x200), LIGHT(0x400);
    byte value;

    DevFeature(int value) {
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