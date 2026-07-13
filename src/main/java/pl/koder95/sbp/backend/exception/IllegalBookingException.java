package pl.koder95.sbp.backend.exception;

public class IllegalBookingException extends RuntimeException {
    public IllegalBookingException(String message) {
        super(message);
    }
}
