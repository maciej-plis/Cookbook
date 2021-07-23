package matthias.cookbook.common;

import lombok.extern.slf4j.Slf4j;
import matthias.cookbook.common.exceptions.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    private static final HttpHeaders HEADERS = new HttpHeaders();

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<Object> notFoundExceptionHandler(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, HEADERS, NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> defaultExceptionHandler(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, HEADERS, INTERNAL_SERVER_ERROR, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status, @NonNull WebRequest request) {
        Map<String, String> body = ex.getFieldErrors().stream()
                .collect(toMap(FieldError::getField, e -> requireNonNullElse(e.getDefaultMessage(), "")));
        return handleExceptionInternal(ex, body, HEADERS, BAD_REQUEST, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex, @Nullable Object body, @NonNull HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        log.error(format("Returning %d (%s) for %s", status.value(), status.name(), request.getDescription(false)), ex);

        Map<String, Object> errors = new LinkedHashMap<>(5);
        errors.put("timestamp", Instant.now());
        errors.put("status", status.value());
        errors.put("error", status.getReasonPhrase());
        errors.put("message", ofNullable(body).orElse(ex.getMessage()));
        errors.put("path", request.getDescription(false).replace("uri=", ""));

        return super.handleExceptionInternal(ex, errors, headers, status, request);
    }
}
