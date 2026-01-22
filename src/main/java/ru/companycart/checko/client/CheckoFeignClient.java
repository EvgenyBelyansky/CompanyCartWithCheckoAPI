package ru.companycart.checko.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.companycart.checko.CheckoFeignConfig;
import ru.companycart.checko.dto.CheckoBatchResponse;
import ru.companycart.checko.dto.CheckoResponse;

@FeignClient(
        name = "checko-client",
        url = "${checko.api.url}",
        configuration = CheckoFeignConfig.class
)
public interface CheckoFeignClient {

    @GetMapping
    CheckoResponse findCompanyByInn(
            @RequestParam("key") String apiKey,
            @RequestParam("inn") String inn
    );

    @GetMapping("/v2/companies/batch")
    CheckoBatchResponse findCompaniesBatch(
            @RequestParam("key") String apiKey,
            @RequestParam("inns") String inns
    );
}