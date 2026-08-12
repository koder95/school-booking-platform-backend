package pl.koder95.sbp.backend.exception;

public class RequestRateLimitException extends RuntimeException {
    public RequestRateLimitException(String msg) {
        super(msg);
    }
}
