package pl.koder95.sbp.backend.exception;

public class ExamplesInstallationException extends RuntimeException {
    public ExamplesInstallationException(String message) {
        super(message);
    }

    public ExamplesInstallationException(String message, Throwable cause) {
        super(message, cause);
    }
}
