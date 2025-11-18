package ru.companycart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CompanyCartException extends RuntimeException {

    private final CompanyCartErrorCode code;
    private final HttpStatus httpStatus;

    public CompanyCartException(CompanyCartErrorCode code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public CompanyCartException(CompanyCartErrorCode code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.I_AM_A_TEAPOT;
    }
}

