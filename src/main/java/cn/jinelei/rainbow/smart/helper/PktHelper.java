package cn.jinelei.rainbow.smart.helper;

import cn.jinelei.rainbow.smart.exception.FormatException;
import cn.jinelei.rainbow.smart.model.L1Bean;

import java.util.Date;

public class PktHelper {

    public static final String REQ_MUST_NOT_NULL = "req must not null";

    /**
     * generate response from request
     *
     * @param req
     * @return L2Bean
     * @throws FormatException when req is null
     */
    public static L1Bean genRspFromReq(L1Bean req) throws FormatException {
        if (req == null)
            throw new FormatException(REQ_MUST_NOT_NULL);
        L1Bean rsp = new L1Bean.L1BeanBuilder()
                .withVersion(req.getVersion())
                .withSrcAddr(req.getDstAddr())
                .withDstAddr(req.getSrcAddr())
                .withTimestamp(new Date().getTime())
                .withSeq((byte) (req.getSeq() + 1))
                .withCategory(req.getCategory())
                .withTag(req.getTag())
                .withLast((short) 0)
                .withData(new byte[0])
                .build();
        return rsp;
    }

    /**
     * convert string to bytes
     *
     * @param mac
     * @return
     * @throws FormatException when mac format is not "xx:xx:xx:xx:xx:xx" or "xx/xx/xx/xx/xx/xx" or "xx-xx-xx-xx-xx-xx"
     */
    public static byte[] macStringToBytes(String mac) throws FormatException {
        String[] split = mac.replaceAll("/", ":").replaceAll("-", ":").split(":");
        if (split.length != 6)
            throw new FormatException("invalid mac address string: " + mac);
        byte[] dest = new byte[6];
        for (int i = 0; i < split.length; i++) {
            Endian.Big.put(dest, i, Byte.valueOf(split[i]));
        }
        return dest;
    }

    /**
     * convert bytes to string
     *
     * @param bytes
     * @return
     * @throws FormatException when bytes length not enumEquals 6
     */
    public static String macBytesToString(byte[] bytes) throws FormatException {
        if (bytes.length != 6)
            throw new FormatException("invalid mac address byte: " + HexHelper.toHexString(bytes));
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X", bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]);
    }

    /**
     * convert val to Enum
     *
     * @param e
     * @param val
     * @param <E>
     * @return
     */
    public static <E> E valueOf(E e, Object val) {
        if (null == val)
            return null;
        if (e.getClass().isEnum()) {
            E[] all = (E[]) e.getClass().getEnumConstants();
            for (int i = 0; i < all.length; i++) {
                if (val instanceof String || val instanceof Object) {
                    if (val.equals(all[i].toString()))
                        return all[i];
                }
                if (val instanceof Integer || val instanceof Long || val instanceof Short || val instanceof Byte
                        || val instanceof Float || val instanceof Double || val instanceof Boolean)
                    if (val == e)
                        return all[i];
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * compare e and obj
     *
     * @param e
     * @param obj
     * @param <E>
     * @return
     */
    public static <E> boolean enumEquals(E e, Object obj) {
        return e.toString().equals(obj.toString());
    }
}