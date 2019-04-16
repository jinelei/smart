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
    public static final String KEY_LAST_CONNECT_TIME = "key_last_connect_time";
    private final Map<ChannelId, Map<String, Object>> tmpMap = new HashMap<>();
    private final Map<ChannelId, Map<String, Object>> onlineMap = new HashMap<>();
    private final Map<ChannelId, Map<String, Object>> suddenDeathMap = new HashMap<>();
    private final Map<ChannelId, Map<String, Object>> deadMap = new HashMap<>();

    public void suddenDeathToOnline(ChannelId channelId) {
        if (suddenDeathMap.containsKey(channelId)) {
            Map<String, Object> map = suddenDeathMap.remove(channelId);
            if (onlineMap.containsKey(channelId))
                onlineMap.remove(channelId);
            map.put(KEY_WAIT, 0);
            onlineMap.put(channelId, map);
        }
    }

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

    public Map<ChannelId, Map<String, Object>> getTmpMap() {
        return tmpMap;
    }

    public void login(ChannelId channelId, List<Common.DevFeature> features, String mac, int timeout) {
        if (tmpMap.containsKey(channelId)) {
            Map<String, Object> tmp = tmpMap.remove(channelId);
            if (onlineMap.containsKey(channelId))
                onlineMap.remove(channelId);
            if (tmp.containsKey(KEY_MAC))
                tmp.remove(KEY_MAC);
            tmp.put(KEY_MAC, mac);
            if (tmp.containsKey(KEY_FEATURES))
                tmp.remove(KEY_FEATURES);
            tmp.put(KEY_FEATURES, features);
            if (tmp.containsKey(KEY_TIMEOUT))
                tmp.remove(KEY_TIMEOUT);
            tmp.put(KEY_TIMEOUT, timeout);
            onlineMap.put(channelId, tmp);
        }
    }

    public void preLogin(ChannelId channelId, Channel channel) {
        Map<String, Object> tmp = tmpMap.get(channelId);
        if (tmp == null)
            tmp = new HashMap<>();
        if (tmp.containsKey(KEY_CHANNEL))
            tmp.remove(KEY_CHANNEL);
        tmp.put(KEY_CHANNEL, channel);
        this.tmpMap.put(channelId, tmp);
    }

    public void onlineToSuddenDeath(ChannelId channelId, long time) {
        if (onlineMap.containsKey(channelId)) {
            Map<String, Object> map = onlineMap.remove(channelId);
            if (suddenDeathMap.containsKey(channelId))
                suddenDeathMap.remove(channelId);
            map.put(KEY_WAIT, 0);
            map.put(KEY_LAST_CONNECT_TIME, time);
            suddenDeathMap.put(channelId, map);
        }
    }

    public void onlineToDead(ChannelId channelId, long time) {
        if (onlineMap.containsKey(channelId)) {
            Map<String, Object> map = onlineMap.remove(channelId);
            if (deadMap.containsKey(channelId))
                deadMap.remove(channelId);
            map.put(KEY_LAST_CONNECT_TIME, time);
            deadMap.put(channelId, map);
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
