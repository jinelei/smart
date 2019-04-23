package cn.jinelei.rainbow.smart.exception;

public class DecoderException extends SmartException {
    public DecoderException() {
        super();
    }

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(Throwable cause) {
        super(cause);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    protected DecoderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
