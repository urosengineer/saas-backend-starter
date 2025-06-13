package com.urke.saasbackendstarter.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    private final MessageSource messageSource;

    public RestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("user.notfound", null, locale);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO("not_found", message));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("user.exists", null, locale);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDTO("user_exists", message));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDTO> handleAuthException(AuthException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = ex.getMessage() != null ? ex.getMessage() :
                messageSource.getMessage("auth.invalid.credentials", null, locale);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("auth_error", message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("access.denied", null, locale);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorDTO("forbidden", message));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorDTO> handleFileUpload(FileUploadException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("file.upload.error", null, locale);
        return ResponseEntity.badRequest()
                .body(new ErrorDTO("file_upload_error", message));
    }

    @ExceptionHandler(FileNotFoundOrForbiddenException.class)
    public ResponseEntity<ErrorDTO> handleFileNotFoundOrForbidden(FileNotFoundOrForbiddenException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("file.notfound", null, locale);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO("file_not_found", message));
    }
    
    @ExceptionHandler(OrganizationAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleOrgExists(OrganizationAlreadyExistsException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("org.exists", null, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("org_exists", message));
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleOrgNotFound(OrganizationNotFoundException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("org.notfound", null, locale);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO("org_not_found", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + messageSource.getMessage(fe, locale))
                .collect(Collectors.joining("; "));
        String title = messageSource.getMessage("validation.error", null, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDTO("validation_error", title + ": " + details));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> handleResponseStatus(ResponseStatusException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String msgKey = ex.getReason();
        String message = messageSource.getMessage(msgKey, null, msgKey, locale);
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorDTO("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleUnknown(Exception ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("server.error", null, "Internal server error.", locale);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO("error", message));
    }
    
    @ExceptionHandler(PasswordResetTokenInvalidException.class)
    public ResponseEntity<ErrorDTO> handlePasswordResetTokenInvalid(PasswordResetTokenInvalidException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("password.reset.invalid", null, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("reset_token_invalid", message));
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    public ResponseEntity<ErrorDTO> handlePasswordResetTokenExpired(PasswordResetTokenExpiredException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("password.reset.expired", null, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("reset_token_expired", message));
    }
    
    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleRoleAlreadyExists(RoleAlreadyExistsException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("role.exists", null, locale);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDTO("role_exists", message));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleRoleNotFound(RoleNotFoundException ex, WebRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage("role.notfound", null, locale);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO("role_not_found", message));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorDTO {
        private String status;
        private String message;
    }
}