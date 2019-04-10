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
    public static final String KEY_FEATURES = "key_features";
    public static final String KEY_TIMEOUT = "key_timeout";
    public static final String KEY_WAIT = "key_wait";
    private final Map<ChannelId, Map<String, Object>> onlineMap = new HashMap<>();
    private final Map<ChannelId, Map<String, Object>> suddenDeathMap = new HashMap<>();
    private final Map<ChannelId, Map<String, Object>> deadMap = new HashMap<>();

    private static class ConnectionContainerInstance {
        private static final ConnectionContainer instance = new ConnectionContainer();
    }

    public static ConnectionContainer getInstance() {
        return ConnectionContainerInstance.instance;
    }

    public Map<ChannelId, Map<String, Object>> getOnlineMap() {
        return onlineMap;
    }

    public Map<ChannelId, Map<String, Object>> getSuddenDeathMap() {
        return suddenDeathMap;
    }

    public Map<ChannelId, Map<String, Object>> getDeadMap() {
        return deadMap;
    }

    public void addMacAddr(ChannelId channelId, String mac) {
        Map<String, Object> tmp = onlineMap.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_MAC))
            tmp.remove(KEY_MAC);
        tmp.put(KEY_MAC, mac);
        this.onlineMap.put(channelId, tmp);
    }

    public void addChannel(ChannelId channelId, Channel channel) {
        Map<String, Object> tmp = onlineMap.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_CHANNEL))
            tmp.remove(KEY_CHANNEL);
        tmp.put(KEY_CHANNEL, channel);
        this.onlineMap.put(channelId, tmp);
    }

    public void onlineToSuddenDeath(ChannelId channelId) {
        if (onlineMap.containsKey(channelId)) {
            Map<String, Object> map = onlineMap.remove(channelId);
            if (suddenDeathMap.containsKey(channelId))
                suddenDeathMap.remove(channelId);
            map.put(KEY_WAIT, 0);
            suddenDeathMap.put(channelId, map);
        }
    }

    public void suddenDeathToDead(ChannelId channelId) {
        if (suddenDeathMap.containsKey(channelId)) {
            Map<String, Object> map = suddenDeathMap.remove(channelId);
            if (deadMap.containsKey(channelId))
                deadMap.remove(channelId);
            deadMap.put(channelId, map);
        }
    }

    public void addFetures(ChannelId channelId, List<Common.DevFeature> fetures) {
        Map<String, Object> tmp = onlineMap.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_FEATURES))
            tmp.remove(KEY_FEATURES);
        tmp.put(KEY_FEATURES, fetures);
        this.onlineMap.put(channelId, tmp);
    }

    public void addTimeout(ChannelId channelId, int timeout) {
        Map<String, Object> tmp = onlineMap.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_TIMEOUT))
            tmp.remove(KEY_TIMEOUT);
        tmp.put(KEY_TIMEOUT, timeout);
        this.onlineMap.put(channelId, tmp);
    }

    public int getWaitCount(ChannelId channelId) {
        if (suddenDeathMap.containsKey(channelId)) {
            int waitCount = (int) suddenDeathMap.get(channelId).getOrDefault(KEY_WAIT, Integer.MAX_VALUE);
            suddenDeathMap.get(channelId).put(KEY_WAIT, ++waitCount);
            return waitCount;
        } else {
            return Integer.MAX_VALUE;
        }
    }

}
