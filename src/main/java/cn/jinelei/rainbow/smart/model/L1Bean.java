package cn.jinelei.rainbow.smart.model;

import cn.jinelei.rainbow.smart.utils.Endian;
import cn.jinelei.rainbow.smart.utils.HexUtils;

public class L1Bean {
    public static final short magic = 0x6A79;
    private byte version; // 版本
    private byte crc; // crc
    private byte[] srcAddr; // 6byte mac address
    private byte[] dstAddr; // 6byte mac address
    private long timestamp; // 时间戳
    private byte seq; // 序列号，依次加1
    private byte category; // 大分类
    private byte tag; // 小分类
    private short last; // 分包标记，如果没有分包，则为0，分包则为剩下的包数量
    private short length; // 之后的data段的长度
    private byte reserved; // 保留字节
    private byte[] data; // 数据区

    public static int LEN_MAGIC = 2;
    public static int IDX_MAGIC = 0;
    public static int LEN_VERSION = 1;
    public static int IDX_VERSION = IDX_MAGIC + LEN_MAGIC;
    public static int LEN_CRC = 1;
    public static int IDX_CRC = IDX_VERSION + LEN_VERSION;
    public static int LEN_SRC_ADDR = 6;
    public static int IDX_SRC_ADDR = IDX_CRC + LEN_CRC;
    public static int LEN_DST_ADDR = 6;
    public static int IDX_DST_ADDR = IDX_SRC_ADDR + LEN_SRC_ADDR;
    public static int LEN_TIMESTAMP = 8;
    public static int IDX_TIMESTAMP = IDX_DST_ADDR + LEN_DST_ADDR;
    public static int LEN_SEQ = 1;
    public static int IDX_SEQ = IDX_TIMESTAMP + LEN_TIMESTAMP;
    public static int LEN_CATEGORY = 1;
    public static int IDX_CATEGORY = IDX_SEQ + LEN_SEQ;
    public static int LEN_TAG = 1;
    public static int IDX_TAG = IDX_CATEGORY + LEN_CATEGORY;
    public static int LEN_LAST = 2;
    public static int IDX_LAST = IDX_TAG + LEN_TAG;
    public static int LEN_LENGTH = 2;
    public static int IDX_LENGTH = IDX_LAST + LEN_LAST;
    public static int LEN_RESERVED = 1;
    public static int IDX_RESERVED = IDX_LENGTH + LEN_LENGTH;
    public static int IDX_DATA = IDX_RESERVED + LEN_RESERVED;
    public static int MIN_PACK_LEN = IDX_RESERVED;

    public byte[] getBytes() {
        byte[] raw = new byte[IDX_DATA + data.length];
        Endian.Big.put(raw, IDX_MAGIC, magic);
        Endian.Big.put(raw, IDX_VERSION, version);
        Endian.Big.put(raw, IDX_CRC, crc);
        Endian.Big.put(raw, IDX_SRC_ADDR, srcAddr);
        Endian.Big.put(raw, IDX_DST_ADDR, dstAddr);
        Endian.Big.put(raw, IDX_TIMESTAMP, timestamp);
        Endian.Big.put(raw, IDX_SEQ, seq);
        Endian.Big.put(raw, IDX_CATEGORY, category);
        Endian.Big.put(raw, IDX_TAG, tag);
        Endian.Big.put(raw, IDX_LAST, last);
        Endian.Big.put(raw, IDX_LENGTH, length);
        Endian.Big.put(raw, IDX_DATA, data);
        return raw;
    }

    /**
     * @return the reserved
     */
    public byte getReserved() {
        return reserved;
    }

    /**
     * @param reserved the reserved to set
     */
    public void setReserved(byte reserved) {
        this.reserved = reserved;
    }

    public L1Bean() {
    }

    public L1Bean(byte version, byte crc, byte[] srcAddr, byte[] dstAddr, long timestamp, byte seq, byte category,
            byte tag, short last, short length, byte[] data) {
        this.version = version;
        this.crc = crc;
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
        this.timestamp = timestamp;
        this.seq = seq;
        this.category = category;
        this.tag = tag;
        this.last = last;
        this.length = length;
        this.data = data;
    }

    public static short getMagic() {
        return magic;
    }

    public byte getVersion() {
        return this.version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getCrc() {
        return this.crc;
    }

    public void setCrc(byte crc) {
        this.crc = crc;
    }

    public byte[] getSrcAddr() {
        return this.srcAddr;
    }

    public String getSrcAddrrString() {
        if (this.srcAddr.length != 6)
            return null;
        else
            return String.format("%02X:%02X:%02X:%02X:%02X:%02X", this.srcAddr[0], this.srcAddr[1], this.srcAddr[2],
                    this.srcAddr[3], this.srcAddr[4], this.srcAddr[5]);
    }

    public void setSrcAddr(byte[] srcAddr) {
        this.srcAddr = srcAddr;
    }

    public byte[] getDstAddr() {
        return this.dstAddr;
    }

    public String getDstAddrString() {
        if (this.dstAddr.length != 6)
            return null;
        else
            return String.format("%02X:%02X:%02X:%02X:%02X:%02X", this.dstAddr[0], this.dstAddr[1], this.dstAddr[2],
                    this.dstAddr[3], this.dstAddr[4], this.dstAddr[5]);
    }

    public void setDstAddr(byte[] dstAddr) {
        this.dstAddr = dstAddr;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte getSeq() {
        return this.seq;
    }

    public void setSeq(byte seq) {
        this.seq = seq;
    }

    public byte getCategory() {
        return this.category;
    }

    public void setCategory(byte category) {
        this.category = category;
    }

    public byte getTag() {
        return this.tag;
    }

    public void setTag(byte tag) {
        this.tag = tag;
    }

    public short getLast() {
        return this.last;
    }

    public void setLast(short last) {
        this.last = last;
    }

    public short getLength() {
        return this.length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) throws Exception {
        if (this.length == 0) {
            if (data.length > Short.MAX_VALUE)
                throw new Exception(
                        String.format("data max length %d must smaller than %d", data.length, Short.MAX_VALUE));
            this.length = (short) data.length;
        }
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" + " magic='" + getMagic() + "'" + ", version='" + getVersion() + "'" + ", crc='" + getCrc() + "'"
                + ", srcAddr='" + HexUtils.toHexString(getSrcAddr()) + "'" + ", dstAddr='"
                + HexUtils.toHexString(getDstAddr()) + "'" + ", timestamp='" + getTimestamp() + "'" + ", seq='"
                + getSeq() + "'" + ", category='" + getCategory() + "'" + ", tag='" + getTag() + "'" + ", last='"
                + getLast() + "'" + ", length='" + getLength() + "'" + ", data='" + HexUtils.toHexString(getData())
                + "'" + "}";
    }

}