package cn.jinelei.rainbow.smart.model.enums;

public class Constants {
    public static final byte[] SERVER_ADDR_BYTES = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    public static final String SERVER_ADDR_String = "00:00:00:00:00:00";

    public static class Default {
        public static final int DEFAULT_MAGIC = 0x6A79;
        public static final int DEFAULT_VERSION = 0x01;
    }
}