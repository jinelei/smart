package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.helper.ServerHelper;
import cn.jinelei.rainbow.smart.server.message.RainMessage;
import cn.jinelei.rainbow.smart.server.message.StateKey;
import cn.jinelei.rainbow.smart.server.message.Type;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 
 * preOnline -> online -> preOffline -> offline // 正常登录，超时关闭。 preOnline ->
 * online -> offline // 正常登录，正常退出。
 * 
 * @author jinelei
 */
public class StateHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(StateHandler.class);
    private static final int DEFAULT_RETRY_TIME = 3;

    public static final String KEY_CONN_STATE = "key_state";
    public static final String KEY_CHANNEL = "key_channel";
    public static final String KEY_MAC = "key_mac";
    public static final String KEY_FEATURES = "key_features";
    public static final String KEY_TIMEOUT = "key_timeout";
    public static final String KEY_RETRY_TIME = "key_retry_time";
    public static final String KEY_LAST_READ_TIME = "key_last_read_time";

    public static final String CONN_STATE_PRE_ONLINE = "conn_state_pre_online";
    public static final String CONN_STATE_ONLINE = "conn_state_online";
    public static final String CONN_STATE_PRE_OFFLINE = "conn_state_pre_offline";
    public static final String CONN_STATE_OFFLINE = "conn_state_offline";

    public static final Map<ChannelId, ConcurrentHashMap<String, Object>> channelMap = new ConcurrentHashMap<>();
    public static final int DEFAULT_TIMEOUT = 10; // 默认10秒钟触发一次超时

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RainMessage && ((RainMessage) msg).getHeader() != null
                && Type.STATE.equals(((RainMessage) msg).getHeader().getType())) {
            switch (StateKey.forNumber(((RainMessage) msg).getHeader().getKey())) {
            case LOGIN:
                // 设置超时
                // todo 添加超时配置
                /**
                 * ctx.pipeline().addBefore(TimeoutHandler.class.getSimpleName() + "#0",
                 * IdleStateHandler.class.getSimpleName(), new IdleStateHandler(TIMEOUT, 0, 0,
                 * TimeUnit.SECONDS));
                 */
                break;
            case LOGOUT:
                break;
            case HEARTBEAT:
                break;
            case RESERVED:
            default:
                break;
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx != null && ctx.channel() != null && ctx.channel().id() != null) {
            ConcurrentHashMap<String, Object> infoMap = channelMap.getOrDefault(ctx.channel().id(),
                    new ConcurrentHashMap<>());
            infoMap.put(KEY_CONN_STATE, CONN_STATE_PRE_ONLINE);
            infoMap.put(KEY_CHANNEL, ctx.channel());
            infoMap.put(KEY_LAST_READ_TIME, Instant.now().toEpochMilli());
            infoMap.put(KEY_RETRY_TIME, 1); // 设置重试次数为1，即10秒超时，超时后即断开连接
            channelMap.put(ctx.channel().id(), infoMap);
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx != null && ctx.channel() != null && ctx.channel().id() != null) {
            ConcurrentHashMap<String, Object> infoMap = channelMap.getOrDefault(ctx.channel().id(),
                    new ConcurrentHashMap<>());
            infoMap.put(KEY_CONN_STATE, CONN_STATE_OFFLINE);
            infoMap.put(KEY_CHANNEL, ctx.channel());
            infoMap.put(KEY_LAST_READ_TIME, Instant.now().toEpochMilli());
            channelMap.put(ctx.channel().id(), infoMap);
        }
        ctx.pipeline().remove(IdleStateHandler.class);
        super.channelUnregistered(ctx);
    }

    /**
     * 超时类型：未登录超时，登录超时； 超时触发机制：所有超时必须为10的倍数，不是整倍数的将会就近取整； 超时计算方法，now() -
     * KEY_LAST_READ_TIME >= KEY_TIMEOUT * 10000；
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            switch (((IdleStateEvent) evt).state()) {
            case READER_IDLE:
            case WRITER_IDLE:
            case ALL_IDLE:
                ConcurrentHashMap<String, Object> infoMap = channelMap.getOrDefault(ctx.channel().id(),
                        new ConcurrentHashMap<>());
                if (infoMap.getOrDefault(KEY_TIMEOUT, null) != null
                        && infoMap.getOrDefault(KEY_LAST_READ_TIME, null) != null) {
                    int retryTime = (int) infoMap.get(KEY_RETRY_TIME);
                    long lastReadTime = (long) infoMap.get(KEY_LAST_READ_TIME);
                    if (Instant.now().toEpochMilli() - lastReadTime > retryTime * 1000 * DEFAULT_TIMEOUT) {
                        // 已经超时
                        if (ctx != null && ctx.channel() != null && ctx.channel().id() != null) {
                            if (CONN_STATE_PRE_ONLINE.equals(infoMap.get(KEY_CONN_STATE))) {
                                // 如果是预登录状态，等待超时后直接关闭连接
                                ServerHelper.disconnect(ctx);
                            } else if (CONN_STATE_ONLINE.equals(infoMap.get(KEY_CONN_STATE))) {
                                // 如果是登录状态，设置重试次数为3，即30秒超时
                                infoMap.put(KEY_CONN_STATE, CONN_STATE_PRE_OFFLINE);
                                infoMap.put(KEY_RETRY_TIME, DEFAULT_RETRY_TIME);
                                channelMap.put(ctx.channel().id(), infoMap);
                            } else if (CONN_STATE_PRE_OFFLINE.equals(infoMap.get(KEY_CONN_STATE))) {
                                // 如果是预离线状态，设置重试次数为3，即30秒超时
                                infoMap.put(KEY_CONN_STATE, CONN_STATE_OFFLINE);
                                infoMap.put(KEY_RETRY_TIME, DEFAULT_RETRY_TIME);
                                channelMap.put(ctx.channel().id(), infoMap);
                                ServerHelper.disconnect(ctx);
                            } else if (CONN_STATE_OFFLINE.equals(infoMap.get(KEY_CONN_STATE))) {
                                // 理论上不会出现该情况
                                ServerHelper.disconnect(ctx);
                            } else {
                                LOGGER.error("channel state known: {}", infoMap.get(KEY_CONN_STATE));
                            }
                        }
                    } else {
                        // 未超时
                    }
                }
                break;
            default:
                break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}

/**
 * if (msg instanceof L1Bean &&
 * Constants.SERVER_ADDR_String.equals(PktHelper.macBytesToString(((L1Bean)
 * msg).getDstAddr())) && PktHelper.enumEquals(Category.DEV_STATUS, ((L1Bean)
 * msg).getCategory())) { L1Bean req = (L1Bean) msg; L1Bean.L1BeanBuilder
 * rspBuilder = PktHelper.genRspFromReqBuilder(req); if (req.getTag() ==
 * DevStatusTag.DEV_LOGIN.getValue()) { // 收到登录请求 if
 * (ConnContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
 * // 如果该连接已经存在于临时连接中 if (req.getLength() != 9) {
 * LOGGER.error(LOGIN_REQUEST_MUST_INCLUDE_FEATURES_8_BYTE_AND_TIMEOUT_1_BYTE +
 * " invalid data length: " + req.getLength());
 * ctx.writeAndFlush(rspBuilder.withData(ErrCode.INVALID_PARAM,
 * LOGIN_REQUEST_MUST_INCLUDE_FEATURES_8_BYTE_AND_TIMEOUT_1_BYTE).build()); }
 * else { long features = Endian.Big.toLong(req.getData()); int timeout =
 * Endian.Big.toUnsignedByte(req.getData(), 8); List<String> featureList =
 * resolveFeature(features); ConnContainer.login(ctx.channel().id(),
 * featureList, PktHelper.macBytesToString(req.getSrcAddr()), timeout);
 * ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS,
 * LOGIN_SUCCESS).build()); } } else { // 如果该连接不存在于临时连接中，则说明该连接已经超时，关闭该连接
 * LOGGER.error(THIS_CHANNEL_NOT_EXIST_IN_TMP_MAP + " channel id: " +
 * ctx.channel().id()); String mac =
 * ConnContainer.getChannelMac(ctx.channel().id()); if (mac != null) {
 * rspBuilder.withData(ErrCode.SUCCESS, THIS_CHANNEL_NOT_EXIST_IN_TMP_MAP);
 * ctx.writeAndFlush(rspBuilder.build()); } ReferenceCountUtil.release(msg);
 * ServerHelper.disconnect(ctx); } } else if (req.getTag() ==
 * DevStatusTag.DEV_LOGOUT.getValue()) { // received logout request: clear login
 * status and disconnect channel // not allow not login channel logout if
 * (ConnContainer.getInstance().getTmpMap().containsKey(ctx.channel().id())) {
 * LOGGER.error(THIS_CHANNEL_NOT_LOGIN + " channel id: " + ctx.channel().id());
 * ctx.writeAndFlush(rspBuilder.withData(ErrCode.OFFLINE,
 * THIS_CHANNEL_NOT_LOGIN).build()); } else if
 * (ConnContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id()))
 * { ConnContainer.onlineToDead(ctx.channel().id(),
 * Instant.now().toEpochMilli());
 * ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS,
 * LOGOUT_SUCCESS).build()); } else if
 * (ConnContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id()))
 * { ConnContainer.suddenDeathToDead(ctx.channel().id());
 * ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS,
 * LOGOUT_SUCCESS).build()); } else if
 * (ConnContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
 * LOGGER.error(CHANNEL_ALREADY_DEAD + " channel id: " + ctx.channel().id());
 * ServerHelper.disconnect(ctx); } ReferenceCountUtil.release(msg); } else if
 * (req.getTag() == DevStatusTag.DEV_HEARTBEAT.getValue()) { // 收到连接的心跳请求 if
 * (ConnContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id()))
 * { // 如果该连接已经在线，可以重新协商心跳时间 // todo: 重新协调心跳时间 //
 * ctx.writeAndFlush(getPktHeartbeatRsp(pkt)); } else if
 * (ConnContainer.getInstance().getSuddenDeathMap().containsKey(ctx.channel().id()))
 * { // 如果该连接已经进入濒死队列，则将连接移到在线队列中 // todo: 重新协调心跳时间
 * ConnContainer.getInstance().suddenDeathToOnline(ctx.channel().id());
 * LOGGER.debug("{}: suddendeath to online", ctx.channel().id());
 * ctx.writeAndFlush(rspBuilder.withData(ErrCode.SUCCESS,
 * CHANNEL_ALREADY_RESURRECTION).build()); } else if
 * (ConnContainer.getInstance().getDeadMap().containsKey(ctx.channel().id())) {
 * // 如果该队列已经进入死亡队列，则返回心跳失败 LOGGER.error(CHANNEL_ALREADY_DEAD + " channel id: "
 * + ctx.channel().id()); ServerHelper.disconnect(ctx); } else { //
 * 该连接不在认识的连接中，丢弃该连接 LOGGER.error(KNOWN_CHANNEL + " channel id: " +
 * ctx.channel().id()); ServerHelper.disconnect(ctx); } } else { // 不支持的Tag
 * ctx.fireChannelRead(msg); } } else { ctx.fireChannelRead(msg); }
 */