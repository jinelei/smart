package cn.jinelei.rainbow.smart.server.container;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnContainer.class);
    public static final String KEY_CHANNEL = "key_channel";
    public static final String KEY_MAC = "key_mac";
    public static final String KEY_FEATURES = "key_features";
    public static final String KEY_TIMEOUT = "key_timeout";
    public static final String KEY_WAIT = "key_wait";
    public static final String KEY_LAST_CONNECT_TIME = "key_last_connect_time";
    private static final Map<ChannelId, Map<String, Object>> tmpMap = new ConcurrentHashMap<>();
    private static final Map<ChannelId, Map<String, Object>> onlineMap = new ConcurrentHashMap<>();
    private static final Map<ChannelId, Map<String, Object>> suddenDeathMap = new ConcurrentHashMap<>();
    private static final Map<ChannelId, Map<String, Object>> deadMap = new ConcurrentHashMap<>();

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
        private static final ConnContainer instance = new ConnContainer();
    }

    public static ConnContainer getInstance() {
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

    public static void login(ChannelId channelId, List<String> features, String mac, int timeout) {
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
        } else {
            LOGGER.info("tmp map not exist this channel({}): ignore", channelId);
        }
    }

    public static void preLogin(ChannelId channelId, Channel channel) {
        Map<String, Object> tmp = tmpMap.get(channelId);
        if (tmp == null) {
            tmp = new HashMap<>();
            LOGGER.info("tmp map is null: create");
        }
        if (tmp.containsKey(KEY_CHANNEL)) {
            tmp.remove(KEY_CHANNEL);
            LOGGER.info("channel({}) already exist: remove and readd", channelId);
        }
        tmp.put(KEY_CHANNEL, channel);
        tmpMap.put(channelId, tmp);
    }

    public static void onlineToSuddenDeath(ChannelId channelId, long time) {
        if (onlineMap.containsKey(channelId)) {
            Map<String, Object> map = onlineMap.remove(channelId);
            if (suddenDeathMap.containsKey(channelId)) {
                suddenDeathMap.remove(channelId);
                LOGGER.info("sudden death map already exist this channel({}): remove", channelId);
            }
            map.put(KEY_WAIT, 0);
            map.put(KEY_LAST_CONNECT_TIME, time);
            suddenDeathMap.put(channelId, map);
        } else {
            LOGGER.info("online map not exist this channel({}): ignore", channelId);
        }
    }

    public static void onlineToDead(ChannelId channelId, long time) {
        if (onlineMap.containsKey(channelId)) {
            Map<String, Object> map = onlineMap.remove(channelId);
            if (deadMap.containsKey(channelId)) {
                deadMap.remove(channelId);
                LOGGER.info("dead death map already exist this channel({}): remove", channelId);
            }
            map.put(KEY_LAST_CONNECT_TIME, time);
            deadMap.put(channelId, map);
        } else {
            LOGGER.info("online map not exist this channel({}): ignore", channelId);
        }
    }

    public static void suddenDeathToDead(ChannelId channelId) {
        if (suddenDeathMap.containsKey(channelId)) {
            Map<String, Object> map = suddenDeathMap.remove(channelId);
            if (deadMap.containsKey(channelId)) {
                deadMap.remove(channelId);
                LOGGER.info("dead death map already exist this channel({}): remove", channelId);
            }
            deadMap.put(channelId, map);
        } else {
            LOGGER.info("sudden death map not exist this channel({}): ignore", channelId);
        }
    }

    public static int getWaitCount(ChannelId channelId) {
        if (suddenDeathMap.containsKey(channelId)) {
            int waitCount = (int) suddenDeathMap.get(channelId).getOrDefault(KEY_WAIT, Integer.MAX_VALUE);
            suddenDeathMap.get(channelId).put(KEY_WAIT, ++waitCount);
            return waitCount;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public static Map<String, Object> getInfoById(ChannelId channelId) {
        if (tmpMap.containsKey(channelId)) {
            return tmpMap.get(channelId);
        } else if (onlineMap.containsKey(channelId)) {
            return onlineMap.get(channelId);
        } else if (suddenDeathMap.containsKey(channelId)) {
            return suddenDeathMap.get(channelId);
        } else if (deadMap.containsKey(channelId)) {
            return deadMap.get(channelId);
        } else {
            return null;
        }
    }

}
