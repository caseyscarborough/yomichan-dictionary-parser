package yomichan.exception;

public class YomichanException extends RuntimeException {
    public YomichanException(String message) {
        super(message);
    }

    public YomichanException(String message, Throwable cause) {
        super(message, cause);
    }
}
