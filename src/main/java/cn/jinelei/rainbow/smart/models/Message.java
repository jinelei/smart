package cn.jinelei.rainbow.smart.models;

import java.util.Map;
import java.util.Objects;

public class Message {
    private Type type;
    private Key key;
    private int seq;
    private long timestamp;
    private Direction direction;
    private Map<String, Object> params;

    public Message() {
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Key getKey() {
        return this.key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Message type(Type type) {
        this.type = type;
        return this;
    }

    public Message key(Key key) {
        this.key = key;
        return this;
    }

    public Message seq(int seq) {
        this.seq = seq;
        return this;
    }

    public Message timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Message direction(Direction direction) {
        this.direction = direction;
        return this;
    }

    public Message params(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(type, message.type) && Objects.equals(key, message.key) && seq == message.seq
                && timestamp == message.timestamp && Objects.equals(direction, message.direction)
                && Objects.equals(params, message.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, key, seq, timestamp, direction, params);
    }

    @Override
    public String toString() {
        return "{" + " type='" + getType() + "'" + ", key='" + getKey() + "'" + ", seq='" + getSeq() + "'"
                + ", timestamp='" + getTimestamp() + "'" + ", direction='" + getDirection() + "'" + ", params='"
                + getParams() + "'" + "}";
    }

}