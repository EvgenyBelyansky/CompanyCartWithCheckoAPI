package ru.companycart.checko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.companycart.checko.client.CheckoFeignClient;
import ru.companycart.checko.dto.CheckoBatchResponse;
import ru.companycart.checko.dto.CheckoResponse;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    /**
     * BATCH запрос для нескольких ИНН одним запросом
     * Проверьте документацию Checko API - есть ли batch endpoint
     */
    public Map<String, CheckoResponse> findCompaniesByInnsBatch(List<String> inns) {
        if (inns == null || inns.isEmpty()) {
            return Collections.emptyMap();
        }

        log.info("🔍 BATCH запрос для {} компаний", inns.size());
        long startTime = System.currentTimeMillis();

        // Проверяем какие ИНН есть в списке
        log.debug("ИНН для запроса: {}", inns);

        try {
            // ВАЖНО: Проверьте документацию Checko API!
            // Если Checko НЕ поддерживает batch, нужно реализовать параллельные запросы

            Map<String, CheckoResponse> results = new HashMap<>();

            // Вариант 1: Если Checko API поддерживает batch (идеальный вариант)
            if (checkoApiSupportsBatch()) {
                // Пример batch endpoint: /v2/companies/batch?inns=7719408327,7707083893
                String innsParam = String.join(",", inns);
                CheckoBatchResponse batchResponse = checkoFeignClient.findCompaniesBatch(apiKey, innsParam);
                results = batchResponse != null ? batchResponse.getData() : Collections.emptyMap();
            }
            // Вариант 2: Если Checko НЕ поддерживает batch - используем параллельные запросы
            else {
                results = makeParallelApiCalls(inns);
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("📊 BATCH API результат: {}/{} компаний за {} мс",
                    results.size(), inns.size(), duration);

            return results;

        } catch (Exception e) {
            log.error("❌ Ошибка BATCH запроса для {} компаний: {}", inns.size(), e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Метод для параллельных запросов если API не поддерживает batch
     */
    private Map<String, CheckoResponse> makeParallelApiCalls(List<String> inns) {
        // Ограничиваем параллельные запросы (например, 5 одновременно)
        int maxConcurrentRequests = 5;

        // Используем CompletableFuture для параллельных запросов
        List<CompletableFuture<Map.Entry<String, CheckoResponse>>> futures = inns.stream()
                .map(inn -> CompletableFuture.supplyAsync(() -> {
                    try {
                        CheckoResponse response = findCompanyByInn(inn);
                        return response != null ? Map.entry(inn, response) : null;
                    } catch (Exception e) {
                        log.debug("⚠️ Ошибка для ИНН {}: {}", inn, e.getMessage());
                        return null;
                    }
                }))
                .collect(Collectors.toList());

        // Собираем результаты
        return futures.stream()
                .map(future -> {
                    try {
                        return future.get(10, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * Проверка поддержки batch API (нужно реализовать по документации)
     */
    private boolean checkoApiSupportsBatch() {
        // Проверьте документацию Checko API!
        // Если поддерживает batch - вернуть true
        // По умолчанию предположим, что не поддерживает
        return false; // ИЗМЕНИТЕ НА true если Checko поддерживает batch
    }
}
