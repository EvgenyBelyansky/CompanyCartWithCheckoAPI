package ru.companycart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.companycart.entity.CompanyCartEntity;
import ru.companycart.service.CompanyCartService;

@RestController
@RequestMapping("/companycart")
@RequiredArgsConstructor
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
}
