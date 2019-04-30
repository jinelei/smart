package cn.jinelei.rainbow.smart.handler;

import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jinelei.rainbow.smart.models.Direction;
import cn.jinelei.rainbow.smart.models.Key;
import cn.jinelei.rainbow.smart.models.Message;
import cn.jinelei.rainbow.smart.models.Type;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

import static cn.jinelei.rainbow.smart.constants.Constants.*;

@Sharable
public class ConnectionStateHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionStateHandler.class);
    public static final ConcurrentHashMap<ChannelId, ConcurrentHashMap<String, Object>> channelMap = new ConcurrentHashMap<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ConcurrentHashMap<String, Object> info = channelMap.getOrDefault(ctx.channel().id(),
                new ConcurrentHashMap<String, Object>());
        info.replace(KEY_CHANNEL_STATE, CHANNEL_STATE_PRE_ONLINE);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ConcurrentHashMap<String, Object> info = channelMap.getOrDefault(ctx.channel().id(),
                new ConcurrentHashMap<String, Object>());
        info.replace(KEY_CHANNEL_STATE, CHANNEL_STATE_OFFLINE);
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ConcurrentHashMap<String, Object> info = channelMap.getOrDefault(ctx.channel().id(),
                new ConcurrentHashMap<String, Object>());
        info.replace(KEY_CHANNEL_STATE, CHANNEL_STATE_OFFLINE);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Gson gson = new Gson();
        try {
            Message message = gson.fromJson(msg.toString(), Message.class);
            if (message != null && message.getType() == Type.STATE && message.getDirection() == Direction.REQUEST) {
                LOGGER.debug("received: {}", message);
                ConcurrentHashMap<String, Object> info = channelMap.getOrDefault(ctx.channel().id(),
                        new ConcurrentHashMap<String, Object>());
                switch (message.getKey()) {
                case LOGIN:
                    int timeout = (int) message.getParams().getOrDefault(KEY_TIMEOUT, 10);
                    info.replace(KEY_TIMEOUT, timeout);
                    break;
                case LOGOUT:
                    break;
                case HEARTBEAT:
                    break;
                }
            } else {
                LOGGER.error("received: empty message");
                ctx.fireChannelRead(msg);
            }
        } catch (JsonSyntaxException e) {
            // TODO: handle exception
        }
    }

}