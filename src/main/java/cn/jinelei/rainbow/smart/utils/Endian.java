package cn.jinelei.rainbow.smart.utils;

public class Endian {
    public static class Reversed {
        public static short toShort(short value) {
            short result = 0;
            result |= (value & 0xFF) << 8;
            result |= (value & 0xFF00) >> 8;
            return result;
        }

        public static int toInt(int value) {
            int result = 0;
            result |= (value & 0xFF) << 24;
            result |= (value & 0xFF00) << 8;
            result |= (value & 0xFF0000) >> 8;
            result |= (value >> 24) & 0xFF;
            return result;
        }
    }

    public static class Big {
        public static short toShort(byte[] bytes, int offset) {
            return (short) (((bytes[offset] & 0xFF) << 8)
                    | (bytes[offset + 1] & 0xFF));
        }

        public static int toMedium(byte[] bytes, int offset) {
            return (bytes[offset] << 16)
                    | ((bytes[offset + 1] & 0xFF) << 8)
                    | (bytes[offset + 2] & 0xFF);
        }

        public static int toInt(byte[] bytes, int offset) {
            return ((bytes[offset] & 0xFF) << 24)
                    | ((bytes[offset + 1] & 0xFF) << 16)
                    | ((bytes[offset + 2] & 0xFF) << 8)
                    | (bytes[offset + 3] & 0xFF);
        }

        public static long toLong(byte[] bytes, int offset) {
            return ((bytes[offset] & 0xFFL) << 56)
                    | ((bytes[offset + 1] & 0xFFL) << 48)
                    | ((bytes[offset + 2] & 0xFFL) << 40)
                    | ((bytes[offset + 3] & 0xFFL) << 32)
                    | ((bytes[offset + 4] & 0xFFL) << 24)
                    | ((bytes[offset + 5] & 0xFFL) << 16)
                    | ((bytes[offset + 6] & 0xFFL) << 8)
                    | (bytes[offset + 7] & 0xFFL);
        }

        public static void put(byte[] dest, int offset, byte[] src) {
            for (int i = 0; i < src.length; i++) {
                dest[offset + i] = src[i];
            }
        }

        public static void put(byte[] dest, int destOffset, byte[] src, int srcOffset, int length) throws Exception {
            if (dest.length - destOffset - length < 0) {
                throw new Exception("dest byte[] length smaller than offset + length");
            }
            for (int i = 0; i < length; i++) {
                dest[destOffset + i] = src[srcOffset + i];
            }
        }

        public static void put(byte[] dest, int offset, byte value) {
            dest[offset] = (byte) (value & 0xFF);
        }

        public static void put(byte[] dest, int offset, short value) {
            dest[offset] = (byte) ((value >>> 8) & 0xFF);
            dest[offset + 1] = (byte) (value & 0xFF);
        }

        public static void put(byte[] dest, int offset, int value) {
            dest[offset] = (byte) ((value >>> 24) & 0xFF);
            dest[offset + 1] = (byte) ((value >>> 16) & 0xFF);
            dest[offset + 2] = (byte) ((value >>> 8) & 0xFF);
            dest[offset + 3] = (byte) (value & 0xFF);
        }

        public static void put(byte[] dest, int offset, long value) {
            dest[offset] = (byte) ((value >>> 56) & 0xFF);
            dest[offset + 1] = (byte) ((value >>> 48) & 0xFF);
            dest[offset + 2] = (byte) ((value >>> 40) & 0xFF);
            dest[offset + 3] = (byte) ((value >>> 32) & 0xFF);
            dest[offset + 4] = (byte) ((value >>> 24) & 0xFF);
            dest[offset + 5] = (byte) ((value >>> 16) & 0xFF);
            dest[offset + 6] = (byte) ((value >>> 8) & 0xFF);
            dest[offset + 7] = (byte) ((value) & 0xFF);
        }

        public static void putMedium(byte[] dest, int offset, int value) {
            dest[offset] = (byte) ((value >>> 24) & 0xFF);
            dest[offset + 1] = (byte) ((value >>> 16) & 0xFF);
            dest[offset + 2] = (byte) ((value >>> 8) & 0xFF);
        }

        public static void putUnsignedByte(byte[] bytes, int offset, short value) {
            bytes[offset] = (byte) (value & 0xFF);
        }

        public static void putUnsignedShort(byte[] bytes, int offset, int value) {
            put(bytes, offset, (short) (value & 0xFFFF));
        }

        public static void putUnsignedMedium(byte[] bytes, int offset, int value) {
            putMedium(bytes, offset, value & 0xFFFFFF);
        }

        public static void putUnsignedInt(byte[] bytes, int offset, long value) {
            put(bytes, offset, (int) (value & 0xFFFFFFFFL));
        }

        public static short toShort(byte[] bytes) {
            return toShort(bytes, 0);
        }

        public static int toMedium(byte[] bytes) {
            return toMedium(bytes, 0);
        }

        public static int toInt(byte[] bytes) {
            return toInt(bytes, 0);
        }

        public static long toLong(byte[] bytes) {
            return toLong(bytes, 0);
        }

        public static short toUnsignedByte(byte[] bytes, int offset) {
            return (short) (bytes[offset] & 0xFF);
        }

        public static int toUnsignedShort(byte[] bytes, int offset) {
            return toShort(bytes, offset) & 0xFFFF;
        }

        public static int toUnsignedMedium(byte[] bytes, int offset) {
            return toMedium(bytes, offset) & 0xFFFFFF;
        }

        public static long toUnsignedInt(byte[] bytes, int offset) {
            return toInt(bytes, offset) & 0xFFFFFFFFL;
        }

        public static int toUnsignedShort(byte[] bytes) {
            return toUnsignedShort(bytes, 0);
        }

        public static int toUnsignedMedium(byte[] bytes) {
            return toUnsignedMedium(bytes, 0);
        }

        public static long toUnsignedInt(byte[] bytes) {
            return toUnsignedInt(bytes, 0);
        }

        public static byte toInt8(byte[] bytes, int offset) {
            return bytes[offset];
        }

        public static short toInt16(byte[] bytes, int offset) {
            return toShort(bytes, offset);
        }

        public static int toInt24(byte[] bytes, int offset) {
            return toMedium(bytes, offset);
        }

        public static int toInt32(byte[] bytes, int offset) {
            return toInt(bytes, offset);
        }

        public static long toInt64(byte[] bytes, int offset) {
            return toLong(bytes, offset);
        }

        public static byte toInt8(byte[] bytes) {
            return toInt8(bytes, 0);
        }

        public static short toInt16(byte[] bytes) {
            return toInt16(bytes, 0);
        }

        public static int toInt24(byte[] bytes) {
            return toInt24(bytes, 0);
        }

        public static int toInt32(byte[] bytes) {
            return toInt32(bytes, 0);
        }

        public static long toInt64(byte[] bytes) {
            return toInt64(bytes, 0);
        }

        public static short toUint8(byte[] bytes, int offset) {
            return toUnsignedByte(bytes, offset);
        }

        public static int toUint16(byte[] bytes, int offset) {
            return toUnsignedShort(bytes, offset);
        }

        public static int toUint24(byte[] bytes, int offset) {
            return toUnsignedMedium(bytes, offset);
        }

        public static long toUint32(byte[] bytes, int offset) {
            return toUnsignedInt(bytes, offset);
        }

        public static short toUint8(byte[] bytes) {
            return toUint8(bytes, 0);
        }

        public static int toUint16(byte[] bytes) {
            return toUint16(bytes, 0);
        }

        public static int toUint24(byte[] bytes) {
            return toUint24(bytes, 0);
        }

        public static long toUint23(byte[] bytes) {
            return toUint32(bytes, 0);
        }

        public static void put(byte[] dest, short value) {
            put(dest, 0, value);
        }

        public static void put(byte[] dest, int value) {
            put(dest, 0, value);
        }

        public static void put(byte[] dest, long value) {
            put(dest, 0, value);
        }

        public static void putUnsignedByte(byte[] dest, short value) {
            putUnsignedByte(dest, 0, value);
        }

        public static void putUnsignedShort(byte[] dest, int value) {
            putUnsignedShort(dest, 0, value);
        }

        public static void putUnsignedMedium(byte[] dest, int value) {
            putUnsignedMedium(dest, 0, value);
        }

        public static void putUnsignedInt(byte[] dest, int value) {
            putUnsignedInt(dest, 0, value);
        }

        public static void putInt8(byte[] dest, int offset, byte value) {
            dest[offset] = value;
        }

        public static void putInt16(byte[] dest, int offset, short value) {
            put(dest, offset, value);
        }

        public static void putInt24(byte[] dest, int offset, int value) {
            putMedium(dest, offset, value);
        }

        public static void putInt32(byte[] dest, int offset, int value) {
            put(dest, offset, value);
        }

        public static void putInt64(byte[] dest, int offset, long value) {
            put(dest, offset, value);
        }

        public static void putInt8(byte[] dest, byte value) {
            putInt8(dest, 0, value);
        }

        public static void putInt16(byte[] dest, short value) {
            putInt16(dest, 0, value);
        }

        public static void putInt24(byte[] dest, int value) {
            putInt24(dest, 0, value);
        }

        public static void putInt32(byte[] dest, int value) {
            putInt32(dest, 0, value);
        }

        public static void putInt64(byte[] dest, long value) {
            putInt64(dest, 0, value);
        }

        public static void putUint8(byte[] dest, int offset, short value) {
            putUnsignedByte(dest, offset, value);
        }

        public static void putUint8(byte[] dest, int offset, int value) {
            putUnsignedByte(dest, offset, (short) value);
        }

        public static void putUint16(byte[] dest, int offset, int value) {
            putUnsignedShort(dest, offset, value);
        }

        public static void putUint24(byte[] dest, int offset, int value) {
            putUnsignedMedium(dest, offset, value);
        }

        public static void putUint32(byte[] dest, int offset, long value) {
            putUnsignedInt(dest, offset, value);
        }

        public static void putUint64(byte[] dest, int offset, long value) {
            put(dest, offset, value);
        }

        public static void putUint8(byte[] dest, short value) {
            putUint8(dest, 0, value);
        }

        public static void putUint16(byte[] dest, int value) {
            putUint16(dest, 0, value);
        }

        public static void putUint24(byte[] dest, int value) {
            putUint24(dest, 0, value);
        }

        public static void putUint32(byte[] dest, long value) {
            putUint32(dest, 0, value);
        }

        public static void putUint64(byte[] dest, long value) {
            putUint64(dest, 0, value);
        }

        public static byte[] toBytes(short value) {
            byte[] bytes = new byte[2];
            put(bytes, value);
            return bytes;
        }

        public static byte[] toBytes(int value) {
            byte[] bytes = new byte[4];
            put(bytes, value);
            return bytes;
        }

        public static byte[] toBytes(long value) {
            byte[] bytes = new byte[8];
            put(bytes, value);
            return bytes;
        }
    }

    public static class Little {
        public static short toShort(byte[] bytes, int offset) {
            return (short) (((bytes[offset + 1] & 0xFF) << 8)
                    | (bytes[offset] & 0xFF));
        }

        public static int toMedium(byte[] bytes, int offset) {
            return (bytes[offset + 2] << 16)
                    | ((bytes[offset + 1] & 0xFF) << 8)
                    | (bytes[offset] & 0xFF);
        }

        public static int toInt(byte[] bytes, int offset) {
            return ((bytes[offset + 3] & 0xFF) << 24)
                    | ((bytes[offset + 2] & 0xFF) << 16)
                    | ((bytes[offset + 1] & 0xFF) << 8)
                    | (bytes[offset] & 0xFF);
        }

        public static long toLong(byte[] bytes, int offset) {
            return ((bytes[offset + 7] & 0xFFL) << 56)
                    | ((bytes[offset + 6] & 0xFFL) << 48)
                    | ((bytes[offset + 5] & 0xFFL) << 40)
                    | ((bytes[offset + 4] & 0xFFL) << 32)
                    | ((bytes[offset + 3] & 0xFFL) << 24)
                    | ((bytes[offset + 2] & 0xFFL) << 16)
                    | ((bytes[offset + 1] & 0xFFL) << 8)
                    | (bytes[offset] & 0xFFL);
        }

        public static void put(byte[] dest, int offset, short value) {
            dest[offset + 1] = (byte) ((value >>> 8) & 0xFF);
            dest[offset] = (byte) (value & 0xFF);
        }

        public static void put(byte[] dest, int offset, int value) {
            dest[offset + 3] = (byte) ((value >>> 24) & 0xFF);
            dest[offset + 2] = (byte) ((value >>> 16) & 0xFF);
            dest[offset + 1] = (byte) ((value >>> 8) & 0xFF);
            dest[offset] = (byte) (value & 0xFF);
        }

        public static void put(byte[] dest, int offset, long value) {
            dest[offset + 7] = (byte) ((value >>> 56) & 0xFF);
            dest[offset + 6] = (byte) ((value >>> 48) & 0xFF);
            dest[offset + 5] = (byte) ((value >>> 40) & 0xFF);
            dest[offset + 4] = (byte) ((value >>> 32) & 0xFF);
            dest[offset + 3] = (byte) ((value >>> 24) & 0xFF);
            dest[offset + 2] = (byte) ((value >>> 16) & 0xFF);
            dest[offset + 1] = (byte) ((value >>> 8) & 0xFF);
            dest[offset] = (byte) ((value) & 0xFF);
        }

        public static void putMedium(byte[] dest, int offset, int value) {
            dest[offset + 2] = (byte) ((value >>> 24) & 0xFF);
            dest[offset + 1] = (byte) ((value >>> 16) & 0xFF);
            dest[offset] = (byte) ((value >>> 8) & 0xFF);
        }

        public static void putUnsignedByte(byte[] bytes, int offset, short value) {
            bytes[offset] = (byte) (value & 0xFF);
        }

        public static void putUnsignedShort(byte[] bytes, int offset, int value) {
            put(bytes, offset, (short) (value & 0xFFFF));
        }

        public static void putUnsignedMedium(byte[] bytes, int offset, int value) {
            putMedium(bytes, offset, value & 0xFFFFFF);
        }

        public static void putUnsignedInt(byte[] bytes, int offset, long value) {
            put(bytes, offset, (int) (value & 0xFFFFFFFFL));
        }

        public static short toShort(byte[] bytes) {
            return toShort(bytes, 0);
        }

        public static int toMedium(byte[] bytes) {
            return toMedium(bytes, 0);
        }

        public static int toInt(byte[] bytes) {
            return toInt(bytes, 0);
        }

        public static long toLong(byte[] bytes) {
            return toLong(bytes, 0);
        }

        public static short toUnsignedByte(byte[] bytes, int offset) {
            return (short) (bytes[offset] & 0xFF);
        }

        public static int toUnsignedShort(byte[] bytes, int offset) {
            return toShort(bytes, offset) & 0xFFFF;
        }

        public static int toUnsignedMedium(byte[] bytes, int offset) {
            return toMedium(bytes, offset) & 0xFFFFFF;
        }

        public static long toUnsignedInt(byte[] bytes, int offset) {
            return toInt(bytes, offset) & 0xFFFFFFFFL;
        }

        public static int toUnsignedShort(byte[] bytes) {
            return toUnsignedShort(bytes, 0);
        }

        public static int toUnsignedMedium(byte[] bytes) {
            return toUnsignedMedium(bytes, 0);
        }

        public static long toUnsignedInt(byte[] bytes) {
            return toUnsignedInt(bytes, 0);
        }

        public static byte toInt8(byte[] bytes, int offset) {
            return bytes[offset];
        }

        public static short toInt16(byte[] bytes, int offset) {
            return toShort(bytes, offset);
        }

        public static int toInt24(byte[] bytes, int offset) {
            return toMedium(bytes, offset);
        }

        public static int toInt32(byte[] bytes, int offset) {
            return toInt(bytes, offset);
        }

        public static long toInt64(byte[] bytes, int offset) {
            return toLong(bytes, offset);
        }

        public static byte toInt8(byte[] bytes) {
            return toInt8(bytes, 0);
        }

        public static short toInt16(byte[] bytes) {
            return toInt16(bytes, 0);
        }

        public static int toInt24(byte[] bytes) {
            return toInt24(bytes, 0);
        }

        public static int toInt32(byte[] bytes) {
            return toInt32(bytes, 0);
        }

        public static long toInt64(byte[] bytes) {
            return toInt64(bytes, 0);
        }

        public static short toUint8(byte[] bytes, int offset) {
            return toUnsignedByte(bytes, offset);
        }

        public static int toUint16(byte[] bytes, int offset) {
            return toUnsignedShort(bytes, offset);
        }

        public static int toUint24(byte[] bytes, int offset) {
            return toUnsignedMedium(bytes, offset);
        }

        public static long toUint32(byte[] bytes, int offset) {
            return toUnsignedInt(bytes, offset);
        }

        public static short toUint8(byte[] bytes) {
            return toUint8(bytes, 0);
        }

        public static int toUint16(byte[] bytes) {
            return toUint16(bytes, 0);
        }

        public static int toUint24(byte[] bytes) {
            return toUint24(bytes, 0);
        }

        public static long toUint23(byte[] bytes) {
            return toUint32(bytes, 0);
        }

        public static void put(byte[] dest, short value) {
            put(dest, 0, value);
        }

        public static void put(byte[] dest, int value) {
            put(dest, 0, value);
        }

        public static void put(byte[] dest, long value) {
            put(dest, 0, value);
        }

        public static void putUnsignedByte(byte[] dest, short value) {
            putUnsignedByte(dest, 0, value);
        }

        public static void putUnsignedShort(byte[] dest, int value) {
            putUnsignedShort(dest, 0, value);
        }

        public static void putUnsignedMedium(byte[] dest, int value) {
            putUnsignedMedium(dest, 0, value);
        }

        public static void putUnsignedInt(byte[] dest, int value) {
            putUnsignedInt(dest, 0, value);
        }

        public static void putInt8(byte[] dest, int offset, byte value) {
            dest[offset] = value;
        }

        public static void putInt16(byte[] dest, int offset, short value) {
            put(dest, offset, value);
        }

        public static void putInt24(byte[] dest, int offset, int value) {
            putMedium(dest, offset, value);
        }

        public static void putInt32(byte[] dest, int offset, int value) {
            put(dest, offset, value);
        }

        public static void putInt64(byte[] dest, int offset, long value) {
            put(dest, offset, value);
        }

        public static void putInt8(byte[] dest, byte value) {
            putInt8(dest, 0, value);
        }

        public static void putInt16(byte[] dest, short value) {
            putInt16(dest, 0, value);
        }

        public static void putInt24(byte[] dest, int value) {
            putInt24(dest, 0, value);
        }

        public static void putInt32(byte[] dest, int value) {
            putInt32(dest, 0, value);
        }

        public static void putInt64(byte[] dest, long value) {
            putInt64(dest, 0, value);
        }

        public static void putUint8(byte[] dest, int offset, short value) {
            putUnsignedByte(dest, offset, value);
        }

        public static void putUint8(byte[] dest, int offset, int value) {
            putUnsignedByte(dest, offset, (short) value);
        }

        public static void putUint16(byte[] dest, int offset, int value) {
            putUnsignedShort(dest, offset, value);
        }

        public static void putUint24(byte[] dest, int offset, int value) {
            putUnsignedMedium(dest, offset, value);
        }

        public static void putUint32(byte[] dest, int offset, long value) {
            putUnsignedInt(dest, offset, value);
        }

        public static void putUint64(byte[] dest, int offset, long value) {
            put(dest, offset, value);
        }

        public static void putUint8(byte[] dest, short value) {
            putUint8(dest, 0, value);
        }

        public static void putUint16(byte[] dest, int value) {
            putUint16(dest, 0, value);
        }

        public static void putUint24(byte[] dest, int value) {
            putUint24(dest, 0, value);
        }

        public static void putUint32(byte[] dest, long value) {
            putUint32(dest, 0, value);
        }

        public static void putUint64(byte[] dest, long value) {
            putUint64(dest, 0, value);
        }

        public static byte[] toBytes(short value) {
            byte[] bytes = new byte[2];
            put(bytes, value);
            return bytes;
        }

        public static byte[] toBytes(int value) {
            byte[] bytes = new byte[4];
            put(bytes, value);
            return bytes;
        }

        public static byte[] toBytes(long value) {
            byte[] bytes = new byte[8];
            put(bytes, value);
            return bytes;
        }
    }
}