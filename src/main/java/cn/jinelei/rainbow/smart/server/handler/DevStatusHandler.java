package cn.jinelei.rainbow.smart.server.handler;

import cn.jinelei.rainbow.smart.server.container.ConnectionContainer;
import com.googlecode.protobuf.format.JsonFormat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
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
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(DevStatusHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ConnectionContainer.getInstance().getOnlineMap().containsKey(ctx.channel().id())
                && msg instanceof Message.Pkt
                && ((Message.Pkt) msg).getDir()
                && Message.Tag.DEV_STATUS.equals(((Message.Pkt) msg).getTag())
        ) {
            Message.Pkt pkt = (Message.Pkt) msg;
            Message.Pkt.Builder rspBuilder = Message.Pkt.newBuilder();
            rspBuilder.setTag(pkt.getTag())
                    .setDir(!pkt.getDir())
                    .setSeq(pkt.getSeq() + 1)
                    .setTimestamp(Instant.now().toEpochMilli())
                    .setDstAddr(pkt.getSrcAddr())
                    .setSrcAddr(pkt.getDstAddr());

            if (pkt.hasOnlineDevicesReqMsg()) {
                LOGGER.debug("{}: query online_devices", ctx.channel().id());
                Message.OnlineDevicesRspMsg.Builder builder = Message.OnlineDevicesRspMsg.newBuilder();
                try {
                    builder.setCount(getInstance().getOnlineMap().size());
                    getInstance().getOnlineMap().forEach((channelId, map) -> {
                        Common.DevConnInfo.Builder builder1 = getDevConnInfoBuilder(channelId, map);
                        List<Common.DevFeature> features = (List<Common.DevFeature>) map.get(KEY_FEATURES);
                        features.forEach(builder1::addDevFeatures);
                        builder.addDevs(builder1);
                    });
                    builder.setErrcode(Common.ErrCode.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    builder.setErrcode(Common.ErrCode.FAILURE);
                } finally {
                    rspBuilder.setOnlineDevicesRspMsg(builder);
                }
            } else if (pkt.hasSuddenDeathDevicesReqMsg()) {
                LOGGER.debug("{}: query sudden_death_devices", ctx.channel().id());
                Message.SuddenDeathDevicesRspMsg.Builder builder = Message.SuddenDeathDevicesRspMsg.newBuilder();
                try {
                    builder.setCount(getInstance().getSuddenDeathMap().size());
                    getInstance().getSuddenDeathMap().forEach((channelId, map) -> {
                        Common.DevConnInfo.Builder builder1 = getDevConnInfoBuilder(channelId, map);
                        List<Common.DevFeature> features = (List<Common.DevFeature>) map.get(KEY_FEATURES);
                        features.forEach(builder1::addDevFeatures);
                        builder.addDevs(builder1);
                    });
                    builder.setErrcode(Common.ErrCode.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    builder.setErrcode(Common.ErrCode.FAILURE);
                } finally {
                    rspBuilder.setSuddenDeathDevicesRspMsg(builder);
                }
            } else if (pkt.hasDeadDevicesReqMsg()) {
                LOGGER.debug("{}: query dead_devices", ctx.channel().id());
                Message.DeadDevicesRspMsg.Builder builder = Message.DeadDevicesRspMsg.newBuilder();
                try {
                    builder.setCount(getInstance().getDeadMap().size());
                    getInstance().getDeadMap().forEach((channelId, map) -> {
                        Common.DevConnInfo.Builder builder1 = getDevConnInfoBuilder(channelId, map);
                        List<Common.DevFeature> features = (List<Common.DevFeature>) map.get(KEY_FEATURES);
                        features.forEach(builder1::addDevFeatures);
                        builder.addDevs(builder1);
                    });
                    builder.setErrcode(Common.ErrCode.SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    builder.setErrcode(Common.ErrCode.FAILURE);
                } finally {
                    rspBuilder.setDeadDevicesRspMsg(builder);
                }
            }
//            ReferenceCountUtil.release(msg);
            LOGGER.debug("dev_status response: {}", JsonFormat.printToString(rspBuilder.build()));
            ctx.writeAndFlush(rspBuilder.build());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private Common.DevConnInfo.Builder getDevConnInfoBuilder(ChannelId channelId, Map<String, Object> map) {
        Common.DevConnInfo.Builder builder = Common.DevConnInfo.newBuilder();
        builder.setId(channelId.asShortText());
        if (map.containsKey(KEY_MAC))
            builder.setMac(map.get(KEY_MAC).toString());
        if (map.containsKey(KEY_WAIT))
            builder.setWaitCount((Integer.valueOf(map.get(KEY_WAIT).toString())));
        if (map.containsKey(KEY_TIMEOUT)) {
            builder.setTimeout(((int) map.get(KEY_TIMEOUT)));
            builder.setLastConnTime(Instant.now().toEpochMilli() - builder.getTimeout() * 1000);
        }
        return builder;
    }

}
