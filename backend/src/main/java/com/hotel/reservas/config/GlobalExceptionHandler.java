package com.hotel.reservas.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor";

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (msg.contains("no encontrado") || msg.contains("not found")) status = HttpStatus.NOT_FOUND;
        else if (msg.contains("Ya existe") || msg.contains("duplicado")) status = HttpStatus.CONFLICT;
        else if (msg.contains("inválido") || msg.contains("obligatorio")) status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(Map.of("error", msg));
    }
}
