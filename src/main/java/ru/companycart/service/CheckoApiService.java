package ru.companycart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.companycart.client.CheckoFeignClient;
import ru.companycart.dto.checko.CheckoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoApiService {

    private final CheckoFeignClient checkoFeignClient;

    @Value("${checko.api.key}")
    private String apiKey;

    /**
     * Быстрый поиск организации по ИНН с минимальной логикой
     */
    public CheckoResponse findCompanyByInn(String inn) {
        long startTime = System.currentTimeMillis();

        try {
            CheckoResponse response = checkoFeignClient.findCompanyByInn(apiKey, inn);

            // Быстрая проверка без лишней логики
            if (response != null && response.getData() != null) {
                log.debug("✅ Found: {} in {}ms", response.getFullName(),
                        System.currentTimeMillis() - startTime);
                return response;
            } else {
                log.debug("⚠️ Not found: {} in {}ms", inn,
                        System.currentTimeMillis() - startTime);
                return null;
            }

        } catch (Exception e) {
            log.debug("❌ Error: {} in {}ms - {}", inn,
                    System.currentTimeMillis() - startTime, e.getMessage());
            return null;
        }
    }
}
