package cn.jinelei.rainbow.smart.server.message;

import cn.jinelei.rainbow.smart.helper.Endian;

import java.util.ArrayList;
import java.util.Arrays;

public class RainMessage {
    private Header header;
    private ArrayList<Payload> payloads;

    public Header getHeader() {
        return header;
    }

    public ArrayList<Payload> getPayloads() {
        return payloads;
    }

    public byte[] toBytes() {
        byte[] dest = new byte[header == null ? 0 : (int) (HEADER_LEN + header.getDataLength())];
        Endian.Big.put(dest, IDX_MAGIC, header.toBytes());
        int offset = MIN_PAYLOAD_LEN;
        if (payloads != null)
            for (int i = 0; i < payloads.size(); i++) {
                Endian.Big.put(dest, offset, payloads.get(i).toBytes());
                offset += payloads.get(i).getLength();
            }
        return dest;
    }

    @Override
    public String toString() {
        return "RainMessage{" +
                "header=" + header +
                ", payloads=" + Arrays.toString(payloads.toArray()) +
                '}';
    }

    public RainMessage(Header header, ArrayList<Payload> payloads) {
        this.header = header;
        this.payloads = payloads;
    }

    public static final int LEN_MAGIC = 2; // 0x02
    public static final int IDX_MAGIC = 0; // 0x00
    public static final int LEN_CRC = 1; // 0x01
    public static final int IDX_CRC = IDX_MAGIC + LEN_MAGIC; // 0x02
    public static final int LEN_VERSION = 1; // 0x01
    public static final int IDX_VERSION = IDX_CRC + LEN_CRC; // 0x03
    public static final int LEN_TYPE = 1; // 0x01
    public static final int IDX_TYPE = IDX_VERSION + LEN_VERSION; // 0x04
    public static final int LEN_KEY = 1; // 0x01
    public static final int IDX_KEY = IDX_TYPE + LEN_TYPE; // 0x05
    public static final int LEN_SEQUENCE = 1; // 0x01
    public static final int IDX_SEQUENCE = IDX_KEY + LEN_KEY; // 0x06
    public static final int LEN_TIMESTAMP = 8; // 0x08
    public static final int IDX_TIMESTAMP = IDX_SEQUENCE + LEN_SEQUENCE; // 0x07
    public static final int LEN_RESERVED = 13; // 0x0D
    public static final int IDX_RESERVED = IDX_TIMESTAMP + LEN_TIMESTAMP; // 0x0E,15
    public static final int LEN_DATA_LENGTH = 4; // 0x08
    public static final int IDX_DATA_LENGTH = IDX_RESERVED + LEN_RESERVED; // 0x1C, 28
    public static final int IDX_PAYLOAD = IDX_DATA_LENGTH + LEN_DATA_LENGTH; // 0x20, 32
    public static final int HEADER_LEN = IDX_PAYLOAD; // 0x20, 32

    public static final int LEN_PAYLOAD_TYPE = 1; // 0x01
    public static final int OFFSET_PAYLOAD_TYPE = 0; // 0x01
    public static final int LEN_PAYLOAD_LENGTH = 1; // 0x01
    public static final int OFFSET_PAYLOAD_LENGTH = LEN_PAYLOAD_TYPE + OFFSET_PAYLOAD_TYPE; // 0x01
    public static final int LEN_PAYLOAD_COUNT = 1; // 0x01
    public static final int OFFSET_PAYLOAD_COUNT = LEN_PAYLOAD_LENGTH + OFFSET_PAYLOAD_LENGTH; // 0x01
    public static final int OFFSET_PAYLOAD_DATA = LEN_PAYLOAD_COUNT + OFFSET_PAYLOAD_COUNT; // 0x01
    public static final int MIN_PAYLOAD_LEN = IDX_PAYLOAD; // 0x20, 32

    public static final class RainMessageBuilder {
        private Header header;
        private ArrayList<Payload> payloads = new ArrayList<>();

        public RainMessageBuilder() {
        }

        public static RainMessageBuilder aRainMessage() {
            return new RainMessageBuilder();
        }

        public RainMessageBuilder withHeader(Header header) {
            this.header = header;
            return this;
        }

        public RainMessageBuilder withPayloads(ArrayList<Payload> payloads) {
            this.payloads = payloads;
            return this;
        }

        public RainMessageBuilder withPayload(Payload payload) {
            if (this.payloads == null)
                this.payloads = new ArrayList<>();
            this.payloads.add(payload);
            return this;
        }

        public RainMessage build() {
            return new RainMessage(header, payloads);
        }
    }
}
