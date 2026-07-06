package com.library.infrastructure.web.exception;

import com.library.core.application.port.exception.ActiveBorrowRecordNotFoundException;
import com.library.core.application.port.exception.BookNotFoundException;
import com.library.core.application.port.exception.DuplicateIsbnException;
import com.library.core.application.port.exception.InvalidCommandException;
import com.library.core.domain.exception.BookIsBorrowedException;
import com.library.core.domain.exception.BookNotAvailableException;
import com.library.core.domain.exception.InvalidDomainStateException;
import com.library.core.domain.exception.UnauthorizedDomainActionException;
import com.library.infrastructure.security.exception.EmailAlreadyRegisteredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class  GlobalExceptionHandler {

    // ──────────────────────────────────────────────────────────────────
    // 404 NOT FOUND
    // ──────────────────────────────────────────────────────────────────

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBookNotFound(
            BookNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ActiveBorrowRecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleActiveBorrowRecordNotFound(
            ActiveBorrowRecordNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ──────────────────────────────────────────────────────────────────
    // 401 UNAUTHORIZED
    // ──────────────────────────────────────────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    // ──────────────────────────────────────────────────────────────────
    // 403 FORBIDDEN
    // ──────────────────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "Access denied", request);
    }

    @ExceptionHandler(UnauthorizedDomainActionException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedDomainActionException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    // ──────────────────────────────────────────────────────────────────
    // 422 UNPROCESSABLE ENTITY  (business / domain state violations)
    // ──────────────────────────────────────────────────────────────────

    @ExceptionHandler(BookIsBorrowedException.class)
    public ResponseEntity<Map<String, Object>> handleBookIsBorrowed(
            BookIsBorrowedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleBookNotAvailable(
            BookNotAvailableException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLockingFailure(
            OptimisticLockingFailureException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Concurrent modification detected. Please retry.", request);
    }

    // ──────────────────────────────────────────────────────────────────
    // 400 BAD REQUEST  (invalid input / command / domain state)
    // ──────────────────────────────────────────────────────────────────

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateIsbn(
            DuplicateIsbnException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyRegistered(
            EmailAlreadyRegisteredException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Data integrity violation", request);
    }

    @ExceptionHandler(InvalidDomainStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDomainState(
            InvalidDomainStateException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCommandException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCommand(
            InvalidCommandException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /** Bean-validation failures on @RequestBody (@Valid). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Request validation failed", request);
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** Bean-validation failures on @RequestParam / @PathVariable (@Validated). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<String> violations = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Request validation failed", request);
        body.put("fieldErrors", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // ──────────────────────────────────────────────────────────────────
    // 500 INTERNAL SERVER ERROR  (catch-all)
    // ──────────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }

    // ──────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(baseBody(status, message, request));
    }

    /**
     * Produces the canonical error envelope matching the rest.md spec:
     * { "timestamp", "status", "error", "message", "path" }
     */
    private Map<String, Object> baseBody(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return body;
    }
}
