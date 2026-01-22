package ru.companycart.exception.inputException;

import org.springframework.http.HttpStatus;
import ru.companycart.exception.CompanyCartErrorCode;
import ru.companycart.exception.CompanyCartException;

public class CompanyIsDuplicateException extends CompanyCartException {
    public CompanyIsDuplicateException(Class clazz, Object object) {
        super(CompanyCartErrorCode.COMPANY_IS_DUPLICATE,
                "Переданный ИНН %s [%s] уже есть в хранилище!".formatted(clazz.getSimpleName(), object),
                HttpStatus.BAD_REQUEST
        );
    }
}
