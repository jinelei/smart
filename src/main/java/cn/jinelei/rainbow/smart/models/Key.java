package cn.jinelei.rainbow.smart.models;

public enum Key {
    LOGIN("login"), HEARTBEAT("heartbeat"), LOGOUT("logout");

    public final String value;

    private Key(String val) {
        this.value = val;
    }
}