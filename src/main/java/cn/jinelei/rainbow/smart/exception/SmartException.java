package cn.jinelei.rainbow.smart.exception;

public class SmartException extends Exception {
    public SmartException() {
        super();
    }

    public SmartException(String message) {
        super(message);
    }

    public SmartException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmartException(Throwable cause) {
        super(cause);
    }

    protected SmartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
