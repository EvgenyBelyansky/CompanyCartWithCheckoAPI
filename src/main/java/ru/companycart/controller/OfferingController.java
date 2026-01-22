package ru.companycart.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.companycart.dto.offering.OfferingInputDto;
import ru.companycart.dto.offering.OfferingOutputDto;
import ru.companycart.service.OfferingService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/offering")
@RequiredArgsConstructor
@Slf4j
public class OfferingController {

    private final OfferingService offeringService;

    @PostMapping("/add")
    public OfferingOutputDto addOffering(@RequestBody OfferingInputDto inputDto) {
        return offeringService.addOffering(inputDto);
    }

    @GetMapping("/{id}")
    public OfferingOutputDto getOfferingById(@PathVariable UUID id) {
        return offeringService.getOfferingById(id);
    }

    @GetMapping("/{companyId}")
    public Collection<OfferingOutputDto> findOfferingsByCompanyId(@PathVariable long companyId) {
        return offeringService.findOfferingByCompanyId(companyId);
    }

    @PatchMapping("/file")
    @Operation(summary = "Возвращает CSV файл и меняет статус ")
    public ResponseEntity<byte[]> getOldestActualOffering() {
        final byte[] oldestActualOfferingAsByteArray = offeringService.getOldestActualOfferingAsByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s.csv".formatted(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")))
                )
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(oldestActualOfferingAsByteArray);
    }
}