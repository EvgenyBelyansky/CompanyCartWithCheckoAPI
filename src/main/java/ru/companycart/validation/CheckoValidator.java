package ru.companycart.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.companycart.checko.dto.CheckoResponse;
import ru.companycart.exception.apiException.CompanyNotFoundInChecko;

@Slf4j
@Component
public class CheckoValidator {

    public void validateCompanyExistsInChecko(CheckoResponse response, String inn) {
        if (response == null || response.getData() == null) {
            log.error("Компания с ИНН {} не найдена в Checko", inn);
            throw new CompanyNotFoundInChecko(inn);
        }
    }

    public boolean isCompanyExistsInChecko(CheckoResponse response, String inn) {
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            log.debug("Компания с ИНН {} не найдена в Checko", inn);
            return false;
        }
        return true;
    }
}


//        if (response.getInn() == null || response.getInn().trim().isEmpty()) {
//            return false;
//        }
//        if (response.getFullName() == null || response.getFullName().trim().isEmpty()) {
//            return false;
//        }
//        return true;