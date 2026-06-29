package com.caiooccardoso.tea_shop_api.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                resolveValidationMessage(request),
                request
        );
        response.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido",
                request
        );

        Map<String, String> fieldErrors = resolveMalformedFields(exception);
        if (!fieldErrors.isEmpty()) {
            response.put("fields", fieldErrors);
        }

        return ResponseEntity.badRequest().body(response);
    }

    private Map<String, String> resolveMalformedFields(HttpMessageNotReadableException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof InvalidFormatException invalidFormatException
                    && LocalDate.class.equals(invalidFormatException.getTargetType())) {
                fieldErrors.put("birthDate", "A data de nascimento deve estar no formato yyyy-MM-dd");
                return fieldErrors;
            }

            String message = cause.getMessage();
            if (message != null && (message.contains(LocalDate.class.getName()) || message.contains("birthDate"))) {
                fieldErrors.put("birthDate", "A data de nascimento deve estar no formato yyyy-MM-dd");
                return fieldErrors;
            }
            cause = cause.getCause();
        }

        String exceptionMessage = exception.getMessage();
        if (exceptionMessage != null
                && (exceptionMessage.contains(LocalDate.class.getName()) || exceptionMessage.contains("birthDate"))) {
            fieldErrors.put("birthDate", "A data de nascimento deve estar no formato yyyy-MM-dd");
        }

        return fieldErrors;
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleProductAlreadyExists(
            ProductAlreadyExistsException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(
            ProductNotFoundException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(
            UserAlreadyExistsException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(
            OrderNotFoundException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(
            InsufficientStockException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedError(HttpServletRequest request) {
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno ao processar requisição",
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String resolveValidationMessage(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/user")) {
            return "Dados inválidos para operação de usuário";
        }
        if (path.startsWith("/api/order")) {
            return "Dados inválidos para operação de pedido";
        }
        return "Dados inválidos para criação do produto";
    }

    private Map<String, Object> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("path", request.getRequestURI());
        return response;
    }
}