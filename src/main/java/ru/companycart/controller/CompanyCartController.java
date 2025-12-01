package ru.companycart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.companycart.entity.CompanyCartEntity;
import ru.companycart.service.CompanyCartService;

import java.util.List;

@RestController
@RequestMapping("/companycart")
@RequiredArgsConstructor
@Slf4j
public class CompanyCartController {

    private final CompanyCartService companyCartService;

    @GetMapping("/{inn}")
    public CompanyCartEntity getCompany(@PathVariable String inn) {
        return companyCartService.getCompanyByInn(inn);
    }

    @GetMapping("/save/{inn}")
    public CompanyCartEntity saveCompany(@PathVariable String inn) {
        return companyCartService.saveCompany(inn);
    }


    @GetMapping("/multiple/save")
    public ResponseEntity<List<CompanyCartEntity>> saveCompaniesBatch(@RequestParam List<String> inn) {
        try {
            List<CompanyCartEntity> savedCompanies = companyCartService.saveCompaniesBatchAtomic(inn);
            return ResponseEntity.ok(savedCompanies);
        } catch (Exception e) {
            log.error("Error in batch saving", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/status")
//    public String getStatus() {
//        return updateCompanyCartService.getUpdateStatus();
//    }
}
