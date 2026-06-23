package dev.apolo.api.exception;

public class ApoloServiceException extends ApoloException {
    public ApoloServiceException(String message) {
        super(message);
    }

    public ApoloServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
