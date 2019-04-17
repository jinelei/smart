package cn.jinelei.rainbow.smart.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Message {
    private byte version;
    private byte crc;
    private byte[] srcAddr; // 6byte mac address
    private byte[] dstAddr; // 6byte mac address
    private long timestamp;
    private byte seq;
    private byte tag;
    private byte last;
    private short length;
    private byte[] data;

    public static final class MessageBuilder {
        private byte version;
        private byte crc;
        private byte[] srcAddr; // 6byte mac address
        private byte[] dstAddr; // 6byte mac address
        private long timestamp;
        private byte seq;
        private byte tag;
        private byte last;
        private short length;
        private byte[] data;

        public static int LEN_VERSION = 1;
        public static int IDX_VERSION = 0;
        public static int LEN_CRC = 1;
        public static int IDX_CRC = LEN_VERSION;
        public static int LEN_SRC_ADDR = 6;
        public static int IDX_SRC_ADDR = IDX_CRC + LEN_CRC;
        public static int LEN_DST_ADDR = 6;
        public static int IDX_DST_ADDR = IDX_SRC_ADDR + LEN_SRC_ADDR;
        public static int LEN_TIMESTAMP = 1;
        public static int IDX_TIMESTAMP = IDX_DST_ADDR + LEN_DST_ADDR;
        public static int LEN_SEQ = 1;
        public static int IDX_SEQ = IDX_TIMESTAMP + LEN_TIMESTAMP;
        public static int LEN_TAG = 1;
        public static int IDX_TAG = IDX_SEQ + LEN_SEQ;
        public static int LEN_LAST = 1;
        public static int IDX_LAST = IDX_TAG + IDX_TAG;
        public static int LEN_LENGTH = 1;
        public static int IDX_LENGTH = IDX_LAST + LEN_LAST;
        public static int LEN_DATA = 1;
        public static int IDX_DATA = IDX_LENGTH + LEN_LENGTH;

        public byte[] getBytes() {
            byte[] raw = new byte[IDX_DATA + data.length];
            raw[IDX_VERSION] = version;
            raw[IDX_CRC] = crc;
            raw[IDX_SRC_ADDR] = srcAddr[0];
            raw[IDX_SRC_ADDR + 1] = srcAddr[1];
            raw[IDX_SRC_ADDR + 2] = srcAddr[2];
            raw[IDX_SRC_ADDR + 3] = srcAddr[3];
            raw[IDX_SRC_ADDR + 4] = srcAddr[4];
            raw[IDX_SRC_ADDR + 5] = srcAddr[5];
            raw[IDX_DST_ADDR] = dstAddr[0];
            raw[IDX_DST_ADDR + 1] = dstAddr[1];
            raw[IDX_DST_ADDR + 2] = dstAddr[2];
            raw[IDX_DST_ADDR + 3] = dstAddr[3];
            raw[IDX_DST_ADDR + 4] = dstAddr[4];
            raw[IDX_DST_ADDR + 5] = dstAddr[5];
            raw[IDX_TIMESTAMP] = (byte) ((timestamp >> 56) & 0xFF);
            raw[IDX_TIMESTAMP + 1] = (byte) ((timestamp >> 48) & 0xFF);
            raw[IDX_TIMESTAMP + 2] = (byte) ((timestamp >> 40) & 0xFF);
            raw[IDX_TIMESTAMP + 3] = (byte) ((timestamp >> 32) & 0xFF);
            raw[IDX_TIMESTAMP + 4] = (byte) ((timestamp >> 24) & 0xFF);
            raw[IDX_TIMESTAMP + 5] = (byte) ((timestamp >> 16) & 0xFF);
            raw[IDX_TIMESTAMP + 6] = (byte) ((timestamp >> 8) & 0xFF);
            raw[IDX_TIMESTAMP + 7] = (byte) (timestamp & 0xFF);
//            private byte[] data;
            raw[IDX_SEQ] = seq;
            raw[IDX_TAG] = tag;
            raw[IDX_LAST] = last;
            raw[IDX_LENGTH] = (byte) ((length >> 8) & 0xFF);
            raw[IDX_LENGTH + 1] = (byte) (length & 0xFF);
            return raw;
        }

        private MessageBuilder() {
        }

        public static MessageBuilder aMessage() {
            return new MessageBuilder();
        }

        public MessageBuilder withVersion(byte version) {
            this.version = version;
            return this;
        }

        public MessageBuilder withCrc(byte crc) {
            this.crc = crc;
            return this;
        }

        public MessageBuilder withSrcAddr(byte[] srcAddr) {
            this.srcAddr = srcAddr;
            return this;
        }

        public MessageBuilder withDstAddr(byte[] dstAddr) {
            this.dstAddr = dstAddr;
            return this;
        }

        public MessageBuilder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MessageBuilder withSeq(byte seq) {
            this.seq = seq;
            return this;
        }

        public MessageBuilder withTag(byte tag) {
            this.tag = tag;
            return this;
        }

        public MessageBuilder withLast(byte last) {
            this.last = last;
            return this;
        }

        public MessageBuilder withLength(short length) {
            this.length = length;
            return this;
        }

        public MessageBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.crc = this.crc;
            message.timestamp = this.timestamp;
            message.version = this.version;
            message.seq = this.seq;
            message.tag = this.tag;
            message.last = this.last;
            message.length = this.length;
            message.dstAddr = this.dstAddr;
            message.srcAddr = this.srcAddr;
            message.data = this.data;
            return message;
        }
    }
}
