package cn.jinelei.rainbow.smart.models;

public enum Direction {
    REQUEST("request"), RESPONSE("response");

    public final String value;

    private Direction(String val) {
        this.value = val;
    }
}