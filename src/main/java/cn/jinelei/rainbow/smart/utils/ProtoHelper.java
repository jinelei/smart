package cn.jinelei.rainbow.smart.utils;

import cn.jinelei.rainbow.smart.model.L1Bean;

import java.util.Date;

public class ProtoHelper {
    public static L1Bean genRspFromReq(L1Bean req) {
        L1Bean rsp = new L1Bean.L1BeanBuilder()
                .withVersion(req.getVersion())
                .withSrcAddr(req.getDstAddr())
                .withDstAddr(req.getSrcAddr())
                .withTimestamp(new Date().getTime())
                .withSeq((byte) (req.getSeq() + 1))
                .withCategory(req.getCategory())
                .withTag(req.getTag())
                .withLast((short) 0)
                .build();
        return rsp;
    }
}