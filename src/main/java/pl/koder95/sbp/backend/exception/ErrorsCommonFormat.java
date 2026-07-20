package pl.koder95.sbp.backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

@Schema(title = "Common format for errors")
public record ErrorsCommonFormat(ZonedDateTime timestamp,
                                 int status,
                                 String message,
                                 String requestMethod,
                                 String path,
                                 List<String> errors) {
    public ErrorsCommonFormat {
        errors = List.copyOf(errors);
    }

    public ErrorsCommonFormat(
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
