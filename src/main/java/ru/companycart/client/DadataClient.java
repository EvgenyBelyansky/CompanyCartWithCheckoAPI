package ru.companycart.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.companycart.dto.dadata.DadataRequest;
import ru.companycart.dto.dadata.DadataResponse;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class DadataClient {

    private final RestTemplate dadataRestTemplate;

    @Value("${dadata.api.key}")
    private String apiKey;

    @Value("${dadata.api.url}")
    private String apiUrl;

    /**
     * Поиск организации по ИНН
     */
    public DadataResponse findCompanyByInn(String inn) {
        log.info("Поиск организации по ИНН: %s".formatted(inn));

        DadataRequest request = new DadataRequest(inn);
        HttpEntity<DadataRequest> httpEntity = createHttpEntity(request);

        ResponseEntity<DadataResponse> response = dadataRestTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                httpEntity,
                DadataResponse.class
        );

        log.info("Найдено организаций: {}", response.getBody().getSuggestions().size());
        return response.getBody();
    }

    /**
     * Проверка существования организации по ИНН
     */
    public boolean companyExists(String inn) {
        DadataResponse response = findCompanyByInn(inn);
        return response.getSuggestions() != null && !response.getSuggestions().isEmpty();
    }

    /**
     * Получить статус организации
     */
    public String getCompanyStatus(String inn) {
        DadataResponse response = findCompanyByInn(inn);
        return response.getSuggestions().get(0).getData().getState().getStatus();
    }

    /**
     * Получить полное наименование организации
     */
    public String getCompanyFullName(String inn) {
        DadataResponse response = findCompanyByInn(inn);
        return response.getSuggestions().get(0).getData().getName().getFull_with_opf();
    }

    /**
     * Создание HTTP entity с заголовками
     */
    private HttpEntity<DadataRequest> createHttpEntity(DadataRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + apiKey);

        log.debug("Отправка запроса к DaData: {}", request.getQuery());
        return new HttpEntity<>(request, headers);
    }
}
