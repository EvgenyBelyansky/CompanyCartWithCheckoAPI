package ru.companycart.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.companycart.dto.checko.CheckoResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckoClient {

    private final RestTemplate restTemplate;

    @Value("${checko.api.url:https://api.checko.ru/v2/company}")
    private String apiUrl;

    @Value("${checko.api.key}")
    private String apiKey;

    /**
     * Поиск организации по ИНН
     */
    public CheckoResponse findCompanyByInn(String inn) {
        log.info("Поиск организации в Checko по ИНН: {}", inn);

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .queryParam("inn", inn)
                .toUriString();

        try {
            ResponseEntity<CheckoResponse> response = restTemplate.getForEntity(url, CheckoResponse.class);
            CheckoResponse checkoResponse = response.getBody();

            if (checkoResponse != null && checkoResponse.getData() != null) {
                log.info("Организация найдена: {}", checkoResponse.getFullName());
            } else {
                log.warn("Организация не найдена или данные отсутствуют");
            }

            return checkoResponse;

        } catch (Exception e) {
            log.error("Ошибка при запросе к Checko API: {}", e.getMessage());
            throw new RuntimeException("Ошибка получения данных из Checko", e);
        }
    }

    /**
     * Проверка существования организации
     */
    public boolean companyExists(String inn) {
        CheckoResponse response = findCompanyByInn(inn);
        return response != null && response.getData() != null;
    }
}

