package matthias.cookbook.common;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import matthias.cookbook.common.exceptions.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    private static final HttpHeaders HEADERS = new HttpHeaders();

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<Object> notFoundExceptionHandler(Exception ex, WebRequest request) {
        log.error("Returning 404 (NOT_FOUND) for " + request.getDescription(false), ex);
        return handleExceptionInternal(ex, null, HEADERS, NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> defaultExceptionHandler(Exception ex, WebRequest request) {
        log.error("returning 500 (INTERNAL_SERVER_ERROR) for " + request.getDescription(false), ex);
        return handleExceptionInternal(ex, null, HEADERS, INTERNAL_SERVER_ERROR, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, @NonNull HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        Map<String, Object> errors = ImmutableMap.of(
                "timestamp", Instant.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ofNullable(body).orElse(ex.getMessage()),
                "path", request.getDescription(false).replace("uri=", "")
        );

        return super.handleExceptionInternal(ex, errors, headers, status, request);
    }
}
