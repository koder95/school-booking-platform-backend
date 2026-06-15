package pl.koder95.sbp.backend.exception;

import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

public record UniversalErrorMessageFormat(ZonedDateTime timestamp,
                                          int status,
                                          String message,
                                          String requestMethod,
                                          String path,
                                          List<String> errors) {
    public UniversalErrorMessageFormat {
    }

    public UniversalErrorMessageFormat(
            int status,
            String message,
            String requestMethod,
            String path,
            List<String> errors) {
        this(ZonedDateTime.now(), status, message, requestMethod, path, errors);
    }

    public HttpStatus statusObject() {
        return HttpStatus.valueOf(status);
    }
}
