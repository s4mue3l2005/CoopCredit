package com.coopcredit.core.infrastructure.adapter.in.web.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleIllegalArgument(IllegalArgumentException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Argument", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleIllegalState(IllegalStateException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Illegal State", ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleAccessDenied(AccessDeniedException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied",
                "You do not have permission to access this resource.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleGeneral(Exception ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.",
                request);
    }

    private ResponseEntity<org.springframework.http.ProblemDetail> buildResponse(HttpStatus status, String title,
            String detail, HttpServletRequest request) {
        org.springframework.http.ProblemDetail problemDetail = org.springframework.http.ProblemDetail
                .forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(java.net.URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(problemDetail);
    }
}
