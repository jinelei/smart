package cn.jinelei.rainbow.smart.utils;

import java.util.Date;

import cn.jinelei.rainbow.smart.model.L1Bean;

public class ProtoHelper {
    public static L1Bean genRspFromReq(L1Bean req) {
        L1Bean rsp = new L1Bean();
        rsp.setVersion(req.getVersion());
        rsp.setSrcAddr(req.getDstAddr());
        rsp.setDstAddr(req.getSrcAddr());
        rsp.setTimestamp(new Date().getTime());
        rsp.setSeq((byte) (req.getSeq() + 1));
        rsp.setCategory(req.getCategory());
        rsp.setTag(req.getTag());
        rsp.setLast((short) 0);
        return rsp;
    }
}