package cn.jinelei.rainbow.smart.server.handler;

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.Common;
import protobuf.Message;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static cn.jinelei.rainbow.smart.server.container.ConnectionContainer.*;

/**
 * @author jinelei
 */
public class DevStatusHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevStatusHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message.Pkt
                && ((Message.Pkt) msg).getDir()
                && Message.Tag.DEV_STATUS.equals(((Message.Pkt) msg).getTag())
        ) {
            Message.Pkt pkt = (Message.Pkt) msg;

            if (pkt.hasOnlineDevicesReqMsg()) {
                Message.OnlineDevicesRspMsg.Builder rspBuilder = Message.OnlineDevicesRspMsg.newBuilder();
                try {
                    ImmutableMap<ChannelId, Map<String, Object>> tmp = ImmutableMap.copyOf(getInstance().getOnlineMap());
                    rspBuilder.setCount(tmp.size());
                    tmp.forEach((channelId, map) -> {
                        Common.DevConnInfo.Builder builder1 = Common.DevConnInfo.newBuilder()
                                .setMac(map.get(KEY_MAC).toString())
                                .setId(channelId.asShortText())
                                .setTimeout(((int) map.get(KEY_TIMEOUT)));
                        List<Common.DevFeature> features = (List<Common.DevFeature>) map.get(KEY_FEATURES);
                        features.forEach(builder1::addDevFeatures);
                        rspBuilder.addDevs(builder1);
                    });
                    rspBuilder.setErrcode(Common.ErrCode.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    rspBuilder.setErrcode(Common.ErrCode.FAILURE);
                } finally {
                    ctx.writeAndFlush(rspBuilder.build());
                }
            } else if (pkt.hasSuddenDeathDevicesReqMsg()) {
                Message.SuddenDeathDevicesRspMsg.Builder rspBuilder = Message.SuddenDeathDevicesRspMsg.newBuilder();
                try {
                    ImmutableMap<ChannelId, Map<String, Object>> tmp = ImmutableMap.copyOf(getInstance().getSuddenDeathMap());
                    rspBuilder.setCount(tmp.size());
                    tmp.forEach((channelId, map) -> {
                        Common.DevConnInfo.Builder builder1 = Common.DevConnInfo.newBuilder()
                                .setMac(map.get(KEY_MAC).toString())
                                .setId(channelId.asShortText())
                                .setLastConnTime(Instant.now().toEpochMilli() - ((IdleStateHandler) ctx.pipeline().get(IdleStateHandler.class.getSimpleName())).getAllIdleTimeInMillis())
                                .setWaitCount((Integer.valueOf(map.get(KEY_WAIT).toString())))
                                .setTimeout(((int) map.get(KEY_TIMEOUT)));
                        List<Common.DevFeature> features = (List<Common.DevFeature>) map.get(KEY_FEATURES);
                        features.forEach(builder1::addDevFeatures);
                        rspBuilder.addDevs(builder1);
                    });
                    rspBuilder.setErrcode(Common.ErrCode.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    rspBuilder.setErrcode(Common.ErrCode.FAILURE);
                } finally {
                    ctx.writeAndFlush(rspBuilder.build());
                }
            } else if (pkt.hasDeadDevicesReqMsg()) {
                Message.DeadDevicesRspMsg.Builder rspBuilder = Message.DeadDevicesRspMsg.newBuilder();
                try {
                    ImmutableMap<ChannelId, Map<String, Object>> tmp = ImmutableMap.copyOf(getInstance().getDeadMap());
                    rspBuilder.setCount(tmp.size());
                    tmp.forEach((channelId, map) -> {
                        Common.DevConnInfo.Builder builder1 = Common.DevConnInfo.newBuilder()
                                .setMac(map.get(KEY_MAC).toString())
                                .setLastConnTime(Instant.now().toEpochMilli() - ((IdleStateHandler) ctx.pipeline().get(IdleStateHandler.class.getSimpleName())).getAllIdleTimeInMillis())
                                .setId(channelId.asShortText())
                                .setTimeout(((int) map.get(KEY_TIMEOUT)));
                        List<Common.DevFeature> features = (List<Common.DevFeature>) map.get(KEY_FEATURES);
                        features.forEach(builder1::addDevFeatures);
                        rspBuilder.addDevs(builder1);
                    });
                    rspBuilder.setErrcode(Common.ErrCode.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    rspBuilder.setErrcode(Common.ErrCode.FAILURE);
                } finally {
                    ctx.writeAndFlush(rspBuilder.build());
                }
            }
            ReferenceCountUtil.release(msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
