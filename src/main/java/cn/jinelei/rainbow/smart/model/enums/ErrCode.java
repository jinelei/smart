package cn.jinelei.rainbow.smart.model.enums;

public enum ErrCode {
    SUCCESS(0x00),
    FAILURE(0x01),
    INVALID_PARAM(0x02),
    NOT_IMPLEMENTED(0x03),
    DEPRECATED(0x04),
    NO_PERMISSION(0x05),
    EAUTH(0x06),
    NOT_ALLOWED(0x07),
    NOT_EXISTS(0x08),
    ALREADY_EXISTS(0x09),
    NO_DEVICE(0x0A),
    USER_OR_PASSWD_WRONG(0x0B),
    OFFLINE(0x0C),
    ALREADY_CANCELED(0x0D),
    IN_PROGRESS(0x0E),
    EXPIRED(0x0F),
    ALREADY_FINISH(0x10),
    IS_EMPTY(0x11),
    INVALIID_FULLD_PARAM(0x12),
    DATA_CONFLICT(0x13);

    byte value;

    ErrCode(int value) {
        this.value = (byte) value;
    }

    /**
     * @return the value
     */
    public byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static ErrCode valueOf(Object obj) {
        int val = Integer.parseInt(obj.toString());
        return valueOf(val);
    }

    public static ErrCode valueOf(int val) {
        for (ErrCode ErrCode : ErrCode.class.getEnumConstants())
            if (ErrCode.value == val)
                return ErrCode;
        return null;
    }
}