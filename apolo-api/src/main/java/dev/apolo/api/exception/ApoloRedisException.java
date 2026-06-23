package dev.apolo.api.exception;

public class ApoloRedisException extends ApoloException {
    public ApoloRedisException(String message) {
        super(message);
    }

    public ApoloRedisException(String message, Throwable cause) {
        super(message, cause);
    }
}
