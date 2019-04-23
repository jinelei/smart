package cn.jinelei.rainbow.smart.helper;

public class HexHelper {
    public static String toHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (byte b : bytes) {
            if (sb.length() > 1) {
                sb.append(" ");
            }
            sb.append(String.format("%02X", b));
        }
        sb.append("]");
        return sb.toString();
    }
}
