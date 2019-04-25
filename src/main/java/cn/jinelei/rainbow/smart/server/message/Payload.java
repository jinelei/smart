package cn.jinelei.rainbow.smart.server.message;

import cn.jinelei.rainbow.smart.helper.Endian;
import cn.jinelei.rainbow.smart.helper.HexHelper;

import static cn.jinelei.rainbow.smart.server.message.RainMessage.*;

public class Payload {
    private byte type;
    private byte length; // length * count + 3 = layout total count
    private byte count;
    private byte[] data;

    @Override
    public String toString() {
        return "Payload{" +
                "type=" + type +
                ", length=" + length +
                ", count=" + count +
                ", data=" + HexHelper.toHexString(data) +
                '}';
    }

    public byte[] toBytes() {
        byte[] dest = new byte[MIN_PAYLOAD_LEN + (data == null ? 0 : data.length)];
        Endian.Big.put(dest, OFFSET_PAYLOAD_TYPE, type);
        Endian.Big.put(dest, OFFSET_PAYLOAD_LENGTH, length);
        Endian.Big.put(dest, OFFSET_PAYLOAD_COUNT, count);
        Endian.Big.put(dest, OFFSET_PAYLOAD_DATA, data);
        return dest;
    }

    public Payload(byte type, byte length, byte count, byte[] data) {
        this.type = type;
        this.length = length;
        this.count = count;
        this.data = data;
    }

    public int getLength() {
        return MIN_PAYLOAD_LEN + (data != null ? data.length : 0);
    }

    public static final class PayloadBuilder {
        private byte type;
        private byte length; // length * count + 3 = layout total count
        private byte count;
        private byte[] data;

        private PayloadBuilder() {
        }

        public static PayloadBuilder aPayload() {
            return new PayloadBuilder();
        }

        public PayloadBuilder withType(byte type) {
            this.type = type;
            return this;
        }

        public PayloadBuilder withLength(byte length) {
            this.length = length;
            return this;
        }

        public PayloadBuilder withCount(byte count) {
            this.count = count;
            return this;
        }

        public PayloadBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public Payload build() {
            return new Payload(type, length, count, data);
        }
    }
}
