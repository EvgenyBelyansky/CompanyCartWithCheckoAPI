package ru.companycart.exception.apiException;

import org.springframework.http.HttpStatus;
import ru.companycart.exception.CompanyCartErrorCode;
import ru.companycart.exception.CompanyCartException;

public class CompanyNotFoundInChecko extends CompanyCartException {
    public CompanyNotFoundInChecko(String inn) {
        super(CompanyCartErrorCode.COMPANY_IS_DUPLICATE,
                "Компания с ИНН [%s] не найдена в Checko!!".formatted(inn),
                HttpStatus.BAD_REQUEST
        );
    }
}
