package ru.companycart.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CompanyCartExceptionHandler {


    @ExceptionHandler(CompanyCartException.class)
    public ResponseEntity<CompanyCartError> handleExamException(CompanyCartException e) {
        CompanyCartError error = new CompanyCartError(
                e.getCode(),
                e.getMessage()
        );

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(error);
    }
}