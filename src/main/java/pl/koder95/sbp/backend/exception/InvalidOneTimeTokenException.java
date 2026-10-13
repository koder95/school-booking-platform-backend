package pl.koder95.sbp.backend.exception;

public class InvalidOneTimeTokenException extends RuntimeException {
    public InvalidOneTimeTokenException(String message) {
        super(message);
    }
}
