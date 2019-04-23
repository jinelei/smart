package test.unit;

import cn.jinelei.rainbow.smart.exception.FormatException;
import cn.jinelei.rainbow.smart.model.L1Bean;
import cn.jinelei.rainbow.smart.model.enums.Constants;
import cn.jinelei.rainbow.smart.helper.PktHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static cn.jinelei.rainbow.smart.helper.PktHelper.*;

public class TestPktHelper {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testMacConvert() throws FormatException {
        Assert.assertArrayEquals(Constants.SERVER_ADDR_BYTES, macStringToBytes(Constants.SERVER_ADDR_String));
        Assert.assertEquals(Constants.SERVER_ADDR_String, macBytesToString(Constants.SERVER_ADDR_BYTES));
    }

    @Test
    public void testGenRspFromReq() {
        L1Bean req = new L1Bean.L1BeanBuilder().withVersion(0x01)
                .withCrc(0x01)
                .withSrcAddr(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06})
                .withDstAddr(new byte[]{0x06, 0x05, 0x04, 0x03, 0x02, 0x01})
                .withTimestamp(1L)
                .withSeq(0x01)
                .withCategory(0x02)
                .withTag(0x03)
                .withLast(0x00)
                .withLength(0x01)
                .withData(new byte[0])
                .build();
        L1Bean rsp = null;
        try {
            rsp = PktHelper.genRspFromReq(req);
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(req.getMagic(), rsp.getMagic());
        Assert.assertEquals(0, rsp.getCrc());
        Assert.assertEquals(req.getVersion(), rsp.getVersion());
        Assert.assertArrayEquals(req.getDstAddr(), rsp.getSrcAddr());
        Assert.assertArrayEquals(req.getSrcAddr(), rsp.getDstAddr());
        Assert.assertNotEquals(req.getTimestamp(), rsp.getTimestamp());
        Assert.assertEquals(req.getSeq() + 1, rsp.getSeq());
        Assert.assertEquals(req.getCategory(), rsp.getCategory());
        Assert.assertEquals(req.getTag(), rsp.getTag());
        Assert.assertEquals(req.getLast(), rsp.getLast());
        Assert.assertEquals(req.getReserved(), rsp.getReserved());
        Assert.assertEquals(0, rsp.getData().length);
    }

    @Test
    public void testGenRspFromReqWithException() throws FormatException {
        exceptionRule.expect(FormatException.class);
        exceptionRule.expectMessage(REQ_MUST_NOT_NULL);
        L1Bean rsp = PktHelper.genRspFromReq(null);
    }

}
