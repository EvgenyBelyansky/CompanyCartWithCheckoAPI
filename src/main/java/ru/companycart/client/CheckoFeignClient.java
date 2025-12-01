package ru.companycart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.companycart.config.CheckoFeignConfig;
import ru.companycart.dto.checko.CheckoResponse;

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
}