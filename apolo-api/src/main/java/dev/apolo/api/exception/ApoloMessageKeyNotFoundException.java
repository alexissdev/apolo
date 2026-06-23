package dev.apolo.api.exception;

public class ApoloMessageKeyNotFoundException extends ApoloException {

    private final String missingPath;

    public ApoloMessageKeyNotFoundException(String missingPath) {
        super("Message key not found in messages.yml: " + missingPath);
        this.missingPath = missingPath;
    }

    public String getMissingPath() {
        return missingPath;
    }
}
