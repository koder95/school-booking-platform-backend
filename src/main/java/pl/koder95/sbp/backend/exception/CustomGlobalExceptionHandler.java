package pl.koder95.sbp.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static ResponseEntity<Object> createUniversalErrorMessageFormat(
            HttpServletRequest request, HttpStatus status, List<String> errors, HttpHeaders headers
    ) {
        return new ResponseEntity<>(new ErrorsCommonFormat(
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

    @ExceptionHandler(RequestRateLimitException.class)
    protected ResponseEntity<Object> handleRequestRateLimitException(
            RequestRateLimitException ex,
            HttpServletRequest request, HttpServletResponse response) {
        if (ex.getMessage() == null) {
            response.resetBuffer();
            try {
                response.getOutputStream().close();
            } catch (IOException ignored) {
                // ignored
            }
        }
        HttpStatus status = HttpStatus.valueOf(429);
        return createUniversalErrorMessageFormat(request, status, List.of(ex.getMessage()));
    }

    @ExceptionHandler(ExamplesInstallationException.class)
    protected ResponseEntity<Object> handleExamplesInstallationException(
            ExamplesInstallationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Throwable cause = ex.getCause();
        return createUniversalErrorMessageFormat(request, status, cause == null
                ? List.of(ex.getMessage())
                : List.of(ex.getMessage(), cause.getMessage())
        );
    }

    @ExceptionHandler(IllegalBookingException.class)
    protected ResponseEntity<Object> handleIllegalBookingException(
            IllegalBookingException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return createUniversalErrorMessageFormat(request, status,
                List.of(ex.getMessage()));
    }

    @ExceptionHandler(AdminAccountAlreadyExists.class)
    protected ResponseEntity<Object> handleAdminAccountAlreadyExists(
            AdminAccountAlreadyExists ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return createUniversalErrorMessageFormat(request, status,
                List.of(ex.getMessage()));
    }

    @ExceptionHandler(EmailDeliveryException.class)
    protected ResponseEntity<Object> handleEmailDeliveryException(
            EmailDeliveryException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return createUniversalErrorMessageFormat(request, status,
                List.of(ex.getMessage()));
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<Object> handleSqlException(
            SQLException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return createUniversalErrorMessageFormat(request, status,
                List.of("An database error(%d) occurred with SQL state: %s"
                        .formatted(ex.getErrorCode(), ex.getSQLState()))
        );
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

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return createUniversalErrorMessageFormat(request, status, List.of(ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<Object> handleNoSuchElementException(HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return createUniversalErrorMessageFormat(request, status, List.of());
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return createUniversalErrorMessageFormat(request, status, List.of(ex.getMessage()));
    }

    @ExceptionHandler(LockedException.class)
    protected ResponseEntity<Object> handleLockedException(
            LockedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return createUniversalErrorMessageFormat(request, status, List.of(ex.getMessage()));
    }

    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<Object> handleDisabledException(
            DisabledException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return createUniversalErrorMessageFormat(request, status, List.of(ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode httpStatusCode, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(httpStatusCode.value());
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        return createUniversalErrorMessageFormat(servletRequest, status,
                ex.getBindingResult().getAllErrors().stream()
                        .map(this::getErrorMessage)
                        .toList(),
                headers);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError fieldError) {
            return fieldError.getField() + " " + e.getDefaultMessage();
        }
        return e.getDefaultMessage();
    }
}
