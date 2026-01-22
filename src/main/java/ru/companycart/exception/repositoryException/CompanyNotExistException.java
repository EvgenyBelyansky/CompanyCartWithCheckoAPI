package ru.companycart.exception.repositoryException;

import org.springframework.http.HttpStatus;
import ru.companycart.exception.CompanyCartErrorCode;
import ru.companycart.exception.CompanyCartException;

public class CompanyNotExistException extends CompanyCartException {
    public CompanyNotExistException(String inn) {
        super(CompanyCartErrorCode.COMPANY_NOT_EXIST,
                "Компания с ИНН [%s] отсутствует в репозитории!".formatted(inn),
                HttpStatus.BAD_REQUEST
        );
    }
}
