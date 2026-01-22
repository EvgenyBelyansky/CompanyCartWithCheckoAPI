package ru.companycart.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.companycart.entity.CompanyCartEntity;
import ru.companycart.repository.CompanyCartRepository;
import ru.companycart.service.CompanyCartService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "company.update.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class CompanyUpdateScheduler {

    private final CompanyCartRepository companyCartRepository;
    private final CompanyCartService companyCartService;

    /**
     * Ежедневное обновление 10 самых старых компаний в 3:00 ночи
     */
    @Scheduled(cron = "${company.update.scheduler.cron:0 0 3 * * ?}")
    public void scheduledDailyUpdate() {
        log.info("=== Запуск планового обновления компаний ===");
        log.info("Время запуска: {}", LocalDateTime.now());

        Instant staleDate = Instant.now().minus(30, ChronoUnit.DAYS);
        List<CompanyCartEntity> staleCompanies = companyCartRepository
                .findByUpdateDateBefore(staleDate, PageRequest.of(0, 10));

        if (staleCompanies.isEmpty()) {
            log.info("Нет компаний для обновления (все актуальны)");
            return;
        }

        log.info("Найдено {} компаний для обновления", staleCompanies.size());

        int successCount = 0;
        int failCount = 0;

        for (CompanyCartEntity company : staleCompanies) {
            try {
                log.debug("Обновление компании: {} (ИНН: {})",
                        company.getFullName(), company.getInn());

                companyCartService.fetchAndUpdateCompanyByInn(company.getInn());
                successCount++;

                log.debug("Компания {} успешно обновлена", company.getInn());

                Thread.sleep(500);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Обновление прервано");
                break;
            } catch (Exception e) {
                failCount++;
                log.error("Ошибка при обновлении компании {}: {}",
                        company.getInn(), e.getMessage());
            }
        }

        log.info("=== Итог обновления ===");
        log.info("Успешно: {} компаний", successCount);
        log.info("С ошибками: {} компаний", failCount);
        log.info("=== Завершение планового обновления ===\n");
    }
}
