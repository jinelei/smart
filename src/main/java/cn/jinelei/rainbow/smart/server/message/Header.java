package cn.jinelei.rainbow.smart.server.message;

import cn.jinelei.rainbow.smart.helper.Endian;

import java.util.Arrays;

import static cn.jinelei.rainbow.smart.server.message.RainMessage.*;

public class Header {
    public static final short magic = 0x6A79;
    private byte crc; // crc
    private byte version; // 版本
    private byte type; // 类型
    private byte key; // 小分类
    private byte sequence; // 序列号，依次加1
    private long timestamp; // 时间戳
    private byte[] reserved; // 保留字节
    private int dataLength; // 之后的data段的长度

    public byte[] toBytes() {
        byte[] dest = new byte[HEADER_LEN];
        Endian.Big.put(dest, IDX_MAGIC, magic);
        Endian.Big.put(dest, IDX_CRC, crc);
        Endian.Big.put(dest, IDX_VERSION, version);
        Endian.Big.put(dest, IDX_TYPE, type);
        Endian.Big.put(dest, IDX_KEY, key);
        Endian.Big.put(dest, IDX_SEQUENCE, sequence);
        Endian.Big.put(dest, IDX_TIMESTAMP, timestamp);
        Endian.Big.put(dest, IDX_RESERVED, reserved);
        Endian.Big.put(dest, IDX_DATA_LENGTH, dataLength);
        return dest;
    }

    @Override
    public String toString() {
        return "Header{" +
                "crc=" + crc +
                ", version=" + version +
                ", type=" + type +
                ", key=" + key +
                ", sequence=" + sequence +
                ", timestamp=" + timestamp +
                ", reserved=" + Arrays.toString(reserved) +
                ", dataLength=" + dataLength +
                '}';
    }

    public Header(int crc, int version, int type, int key, int sequence, long timestamp, int dataLength) {
        this(((byte) crc), ((byte) version), ((byte) type), ((byte) key), ((byte) sequence), timestamp, dataLength);
    }

    public Header(byte crc, byte version, byte type, byte key, byte sequence, long timestamp, int dataLength) {
        this.crc = crc;
        this.version = version;
        this.type = type;
        this.key = key;
        this.sequence = sequence;
        this.timestamp = timestamp;
        this.reserved = new byte[LEN_RESERVED];
        this.dataLength = dataLength;
    }

    public static short getMagic() {
        return magic;
    }

    public byte getCrc() {
        return crc;
    }

    public byte getVersion() {
        return version;
    }

    public byte getType() {
        return type;
    }

    public byte getKey() {
        return key;
    }

    public byte getSequence() {
        return sequence;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public int getDataLength() {
        return dataLength;
    }

    public static final class HeaderBuilder {
        public static short magic = 0x6A79;
        private byte crc; // crc
        private byte version; // 版本
        private byte type; // 类型
        private byte key; // 小分类
        private byte sequence; // 序列号，依次加1
        private long timestamp; // 时间戳
        private byte[] reserved; // 保留字节
        private int dataLength; // 之后的data段的长度

        public HeaderBuilder() {
        }

        public static HeaderBuilder aHeader() {
            return new HeaderBuilder();
        }

        public HeaderBuilder withMagic(short magic) {
            this.magic = magic;
            return this;
        }

        public HeaderBuilder withCrc(byte crc) {
            this.crc = crc;
            return this;
        }

        public HeaderBuilder withVersion(byte version) {
            this.version = version;
            return this;
        }

        public HeaderBuilder withType(byte type) {
            this.type = type;
            return this;
        }

        public HeaderBuilder withKey(byte key) {
            this.key = key;
            return this;
        }

        public HeaderBuilder withSequence(byte sequence) {
            this.sequence = sequence;
            return this;
        }

        public HeaderBuilder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public HeaderBuilder withReserved(byte[] reserved) {
            this.reserved = reserved;
            return this;
        }

        public HeaderBuilder withDataLength(int dataLength) {
            this.dataLength = dataLength;
            return this;
        }

        public Header build() {
            Header header = new Header(crc, version, type, key, sequence, timestamp, dataLength);
            return header;
        }
    }
}