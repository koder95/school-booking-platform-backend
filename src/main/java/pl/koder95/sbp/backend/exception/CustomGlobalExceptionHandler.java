package pl.koder95.sbp.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static ResponseEntity<Object> createUniversalErrorMessageFormat(
            HttpServletRequest request, HttpStatus status, List<String> errors, HttpHeaders headers
    ) {
        return new ResponseEntity<>(new UniversalErrorMessageFormat(
                status.value(),
                status.getReasonPhrase(),
                request.getMethod(),
                request.getRequestURI(),
                errors
        ), headers, status);
    }

    private static ResponseEntity<Object> createUniversalErrorMessageFormat(
            HttpServletRequest request, HttpStatus status, List<String> errors
    ) {
        return createUniversalErrorMessageFormat(request, status, errors, new HttpHeaders());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        return createUniversalErrorMessageFormat(request, status, List.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidEmailValueException.class)
    protected ResponseEntity<Object> handleInvalidEmailValueException(
            InvalidEmailValueException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return createUniversalErrorMessageFormat(request, status, List.of(ex.getMessage()));
    }
}
