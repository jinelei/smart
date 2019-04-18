package cn.jinelei.rainbow.smart.model;

public class JySmartProto {
    public static final short magic = 0x6A79;
    private byte version;
    private byte crc;
    private byte[] srcAddr;
    private byte[] dstAddr;
    private long timestamp;
    private byte seq;
    private byte category;
    private byte tag;
    private short last;
    private short length;
    private byte[] data;

    public JySmartProto() {
    }

    public JySmartProto(byte version, byte crc, byte[] srcAddr, byte[] dstAddr, long timestamp, byte seq, byte category, byte tag, short last, short length, byte[] data) {
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

    public void setSrcAddr(byte[] srcAddr) {
        this.srcAddr = srcAddr;
    }

    public byte[] getDstAddr() {
        return this.dstAddr;
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

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
            " magic='" + getMagic() + "'" +
            ", version='" + getVersion() + "'" +
            ", crc='" + getCrc() + "'" +
            ", srcAddr='" + getSrcAddr() + "'" +
            ", dstAddr='" + getDstAddr() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", seq='" + getSeq() + "'" +
            ", category='" + getCategory() + "'" +
            ", tag='" + getTag() + "'" +
            ", last='" + getLast() + "'" +
            ", length='" + getLength() + "'" +
            ", data='" + getData() + "'" +
            "}";
    }
    
}