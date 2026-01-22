package ru.companycart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.companycart.dto.company.CompanyDto;
import ru.companycart.dto.company.CompanyInnDto;
import ru.companycart.service.CompanyCartService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/companycart")
@RequiredArgsConstructor
@Slf4j
public class CompanyCartController {

    private final CompanyCartService companyCartService;

    @GetMapping("/{inn}")
    public CompanyDto getCompany(@PathVariable String inn) {
        return companyCartService.fetchCompanyByInn(inn);
    }

    @PostMapping("/save")
    public CompanyDto saveCompany(@RequestBody CompanyInnDto companyInnDto) {
        return companyCartService.saveCompanyFromChecko(companyInnDto.inn());
    }

    @PostMapping("save/batch")
    public Collection<CompanyDto> saveCompaniesBatch(@RequestBody List<String> innList) {
        return companyCartService.fetchAndSaveCompanyByInnBatch(innList);
    }

    @PostMapping("/update/{inn}")
    public CompanyDto updateCompanyCart(@PathVariable String inn) {
        return companyCartService.fetchAndUpdateCompanyByInn(inn);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> testMetod() {
        final byte[] oldestActualCompanyAsByteArray = companyCartService.getOldestActualCompanyAsByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s.csv".formatted(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")))
                )
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(oldestActualCompanyAsByteArray);
    }
}
