package cn.jinelei.rainbow.smart.models;

public enum Type {
    STATE("state"), OPERATE("operate");

    public final String value;

    private Type(String val) {
        this.value = val;
    }
}
