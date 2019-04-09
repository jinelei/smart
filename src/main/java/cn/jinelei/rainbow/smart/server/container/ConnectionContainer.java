package cn.jinelei.rainbow.smart.server.container;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import protobuf.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionContainer {
    public static final String KEY_CHANNEL = "key_channel";
    public static final String KEY_MAC = "key_mac";
    public static final String KEY_FETURES = "key_fetures";
    public static final String KEY_TIMEOUT = "key_timeout";
    private final Map<ChannelId, Map<String, Object>> map = new HashMap<>();

    private static class ConnectionContainerInstance {
        private static final ConnectionContainer instance = new ConnectionContainer();
    }

    public static ConnectionContainer getInstance() {
        return ConnectionContainerInstance.instance;
    }

    public Map<ChannelId, Map<String, Object>> getMap() {
        return map;
    }

    public void addMacAddr(ChannelId channelId, String mac) {
        Map<String, Object> tmp = map.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_MAC))
            tmp.remove(KEY_MAC);
        tmp.put(KEY_MAC, mac);
        this.map.put(channelId, tmp);
    }

    public void addChannel(ChannelId channelId, Channel channel) {
        Map<String, Object> tmp = map.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_CHANNEL))
            tmp.remove(KEY_CHANNEL);
        tmp.put(KEY_CHANNEL, channel);
        this.map.put(channelId, tmp);
    }

    public void removeChannelId(ChannelId channelId) {
        if (map.containsKey(channelId)) {
            map.remove(channelId);
        }
    }

    public void addFetures(ChannelId channelId, List<Common.DevFeature> fetures) {
        Map<String, Object> tmp = map.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_FETURES))
            tmp.remove(KEY_FETURES);
        tmp.put(KEY_FETURES, fetures);
        this.map.put(channelId, tmp);
    }

    public void addTimeout(ChannelId channelId, int timeout) {
        Map<String, Object> tmp = map.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_TIMEOUT))
            tmp.remove(KEY_TIMEOUT);
        tmp.put(KEY_TIMEOUT, timeout);
        this.map.put(channelId, tmp);
    }

    public int getTimeout(ChannelId channelId) {
        Map<String, Object> tmp = map.get(channelId);
        if (tmp == null || !tmp.containsKey(KEY_TIMEOUT))
            return -1;
        else
            return (int) tmp.get(KEY_TIMEOUT);
    }

}
