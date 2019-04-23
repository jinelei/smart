package cn.jinelei.rainbow.smart.model;

import cn.jinelei.rainbow.smart.helper.Endian;
import cn.jinelei.rainbow.smart.helper.HexHelper;

public class L1Bean extends BaseBean {
    public static final short magic = 0x6A79;
    private byte crc; // crc
    private byte version; // 版本
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

    public static final int LEN_MAGIC = 2;
    public static final int IDX_MAGIC = 0;
    public static final int LEN_CRC = 1;
    public static final int IDX_CRC = IDX_MAGIC + LEN_MAGIC;
    public static final int LEN_VERSION = 1;
    public static final int IDX_VERSION = IDX_CRC + LEN_CRC;
    public static final int LEN_SRC_ADDR = 6;
    public static final int IDX_SRC_ADDR = IDX_VERSION + LEN_VERSION;
    public static final int LEN_DST_ADDR = 6;
    public static final int IDX_DST_ADDR = IDX_SRC_ADDR + LEN_SRC_ADDR;
    public static final int LEN_TIMESTAMP = 8;
    public static final int IDX_TIMESTAMP = IDX_DST_ADDR + LEN_DST_ADDR;
    public static final int LEN_SEQ = 1;
    public static final int IDX_SEQ = IDX_TIMESTAMP + LEN_TIMESTAMP;
    public static final int LEN_CATEGORY = 1;
    public static final int IDX_CATEGORY = IDX_SEQ + LEN_SEQ;
    public static final int LEN_TAG = 1;
    public static final int IDX_TAG = IDX_CATEGORY + LEN_CATEGORY;
    public static final int LEN_LAST = 2;
    public static final int IDX_LAST = IDX_TAG + LEN_TAG;
    public static final int LEN_LENGTH = 2;
    public static final int IDX_LENGTH = IDX_LAST + LEN_LAST;
    public static final int LEN_RESERVED = 1;
    public static final int IDX_RESERVED = IDX_LENGTH + LEN_LENGTH;
    public static final int IDX_DATA = IDX_RESERVED + LEN_RESERVED;
    public static final int MIN_PACK_LEN = IDX_RESERVED;

    public void putTo(byte[] dest, int offset) {
        byte[] raw = this.toBytes();
        System.arraycopy(raw, 0, dest, offset, raw.length);
    }

    public void putTo(byte[] dest) {
        this.putTo(dest, 0);
    }

    @Override
    protected void parse(byte[] bytes, int offset) throws Exception {
        L1BeanBuilder builder = new L1BeanBuilder();
        if (Endian.Big.toUnsignedShort(bytes, IDX_MAGIC + offset) != magic)
            throw new Exception("bytes is not support");
        builder.withMagic(magic);
        builder.withCrc((byte) Endian.Big.toUnsignedByte(bytes, IDX_CRC + offset));
        builder.withVersion((byte) Endian.Big.toUnsignedByte(bytes, IDX_VERSION + offset));
        byte[] tmpSrc = new byte[6];
        byte[] tmpDst = new byte[6];
        Endian.Big.put(tmpSrc, 0, bytes, IDX_SRC_ADDR + offset, LEN_SRC_ADDR);
        Endian.Big.put(tmpDst, 0, bytes, IDX_DST_ADDR + offset, LEN_DST_ADDR);
        builder.withSrcAddr(tmpSrc);
        builder.withSrcAddr(tmpDst);
        builder.withTimestamp(Endian.Big.toLong(bytes, IDX_TIMESTAMP + offset));
        builder.withSeq((byte) Endian.Big.toUnsignedByte(bytes, IDX_SEQ + offset));
        builder.withCategory((byte) Endian.Big.toUnsignedByte(bytes, IDX_CATEGORY + offset));
        builder.withTag((byte) Endian.Big.toUnsignedByte(bytes, IDX_TAG + offset));
        builder.withLast(Endian.Big.toShort(bytes, IDX_LAST + offset));
        builder.withLength(Endian.Big.toShort(bytes, IDX_LENGTH + offset));
        builder.withReserved((byte) Endian.Big.toUnsignedByte(bytes, IDX_RESERVED + offset));
        byte[] tmpData = new byte[builder.length];
        Endian.Big.put(tmpData, 0, bytes, IDX_DATA + offset, builder.length);
    }

    @Override
    protected void parse(byte[] bytes) throws Exception {
        this.parse(bytes, 0);
    }

    public byte[] toBytes() {
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

    private L1Bean() {
    }

    public static short getMagic() {
        return magic;
    }

    public byte getVersion() {
        return version;
    }

    public byte getCrc() {
        return crc;
    }

    public byte[] getSrcAddr() {
        return srcAddr;
    }

    public byte[] getDstAddr() {
        return dstAddr;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public byte getSeq() {
        return seq;
    }

    public byte getCategory() {
        return category;
    }

    public byte getTag() {
        return tag;
    }

    public short getLast() {
        return last;
    }

    public short getLength() {
        return length;
    }

    public byte getReserved() {
        return reserved;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "{" + " magic='" + getMagic() + "'" + ", version='" + getVersion() + "'" + ", crc='" + getCrc() + "'"
                + ", srcAddr='" + HexHelper.toHexString(getSrcAddr()) + "'" + ", dstAddr='"
                + HexHelper.toHexString(getDstAddr()) + "'" + ", timestamp='" + getTimestamp() + "'" + ", seq='"
                + getSeq() + "'" + ", category='" + getCategory() + "'" + ", tag='" + getTag() + "'" + ", last='"
                + getLast() + "'" + ", length='" + getLength() + "'" + ", data='" + HexHelper.toHexString(getData())
                + "'" + "}";
    }

    public static final class L1BeanBuilder {
        public static short magic = 0x6A79;
        private byte crc; // crc
        private byte version; // 版本
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

        public L1BeanBuilder() {
        }

        public static L1BeanBuilder aL1Bean() {
            return new L1BeanBuilder();
        }

        public L1BeanBuilder withMagic(short magic) {
            this.magic = magic;
            return this;
        }

        public L1BeanBuilder withCrc(int crc) {
            this.crc = (byte) crc;
            return this;
        }

        public L1BeanBuilder withVersion(int version) {
            this.version = (byte) version;
            return this;
        }

        public L1BeanBuilder withSrcAddr(byte[] srcAddr) {
            this.srcAddr = srcAddr;
            return this;
        }

        public L1BeanBuilder withDstAddr(byte[] dstAddr) {
            this.dstAddr = dstAddr;
            return this;
        }

        public L1BeanBuilder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public L1BeanBuilder withSeq(int seq) {
            this.seq = (byte) seq;
            return this;
        }

        public L1BeanBuilder withCategory(int category) {
            this.category = (byte) category;
            return this;
        }

        public L1BeanBuilder withTag(int tag) {
            this.tag = (byte) tag;
            return this;
        }

        public L1BeanBuilder withLast(int last) {
            this.last = (short) last;
            return this;
        }

        public L1BeanBuilder withLength(int length) {
            this.length = (short) length;
            return this;
        }

        public L1BeanBuilder withReserved(int reserved) {
            this.reserved = (byte) reserved;
            return this;
        }

        public L1BeanBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public L1Bean build() {
            L1Bean l1Bean = new L1Bean();
            l1Bean.dstAddr = this.dstAddr;
//            l1Bean.magic = this.magic;
            l1Bean.timestamp = this.timestamp;
            l1Bean.data = this.data;
            l1Bean.srcAddr = this.srcAddr;
            l1Bean.reserved = this.reserved;
            l1Bean.category = this.category;
            l1Bean.seq = this.seq;
            l1Bean.crc = this.crc;
            l1Bean.last = this.last;
            l1Bean.version = this.version;
            l1Bean.length = this.length;
            l1Bean.tag = this.tag;
            return l1Bean;
        }
    }
}