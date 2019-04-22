package cn.jinelei.rainbow.smart.model.enums;

public enum Strings {
    RESERVED("reserved"), SERVER_ADDR("00:00:00:00:00:00");
    String value;

    Strings(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}