package cn.jinelei.rainbow.smart.model;

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
