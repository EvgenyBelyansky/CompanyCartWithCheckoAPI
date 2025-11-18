package ru.companycart.exception;

import org.springframework.http.HttpStatus;

public class CompanyIsDuplicateException extends CompanyCartException {
    public CompanyIsDuplicateException(Class clazz, Object object) {
        super(CompanyCartErrorCode.COMPANY_IS_DUPLICATE,
                "Переданный ИНН %s [%s] уже есть в хранилище!".formatted(clazz.getSimpleName(), object),
                HttpStatus.BAD_REQUEST
        );
    }
}
