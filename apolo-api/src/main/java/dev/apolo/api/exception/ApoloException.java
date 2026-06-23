package dev.apolo.api.exception;

public class ApoloException extends RuntimeException {
    public ApoloException(String message) {
        super(message);
    }

    public ApoloException(String message, Throwable cause) {
        super(message, cause);
    }
}
