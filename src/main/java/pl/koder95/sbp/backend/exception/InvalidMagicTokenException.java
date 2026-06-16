package pl.koder95.sbp.backend.exception;

public class InvalidMagicTokenException extends RuntimeException {
    public InvalidMagicTokenException(String message) {
        super(message);
    }
}
