//package ru.companycart.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import ru.companycart.entity.CompanyCartEntity;
//import ru.companycart.repository.CompanyCartRepository;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class UpdateCompanyCartService {
//
//    private final CompanyCartRepository companyCartRepository;
//    private final CompanyCartService companyCartService;
//
//    // Лимит API: 100 запросов в день, оставляем 10 для ручных операций
//    private static final int DAILY_UPDATE_LIMIT = 90;
//
//    public UpdateCompanyCartService(CompanyCartRepository companyCartRepository,
//                                    CompanyCartService companyCartService) {
//        this.companyCartRepository = companyCartRepository;
//        this.companyCartService = companyCartService;
//    }
//
//    /**
//     * Ежедневное обновление части компаний (в пределах лимита API)
//     * Запускается каждый день в 2:00 ночи
//     */
//    //todo убрать в проперти
//    @Scheduled(cron = "0 0 2 * * *") // Ежедневно в 2:00
//    public void updateCompaniesDailyWithinLimit() {
//        log.info("Starting DAILY company update within API limits");
//        long startTime = System.currentTimeMillis();
//
//
//            // 1. Получаем все ИНН из базы
//            List<String> allInns = companyCartRepository.findAllInns();
//            int totalCompanies = allInns.size();
//
//            if (totalCompanies == 0) {
//                log.info("📭 No companies found in database");
//                return;
//            }
//
//            //todo убрать лимит компаний в запрос репозитория
////            // 2. Рассчитываем, сколько компаний обновить сегодня
////            int companiesToUpdateToday = Math.min(
////                    DAILY_UPDATE_LIMIT,
////                    (int) Math.ceil((double) totalCompanies / 30) // Распределяем на месяц
////            );
////
////            if (companiesToUpdateToday == 0) {
////                log.info("⏸️ No companies scheduled for update today");
////                return;
////            }
//
//            // 3. Выбираем компании для обновления (ротация по дням месяца)
//            List<String> innsToUpdate = selectCompaniesForUpdate(allInns, companiesToUpdateToday);
//
//            // 4. Выполняем обновление
//            List<CompanyCartEntity> updatedCompanies = companyCartService
//                    .saveCompaniesLargeBatchAtomic(innsToUpdate);
//
//            long duration = System.currentTimeMillis() - startTime;
//
//            // 5. Логируем результат
//            log.info("✅ DAILY UPDATE COMPLETED: Updated {}/{} companies in {} ms",
//                    updatedCompanies.size(), innsToUpdate.size(), duration);
//    }
//
//    /**
//     * Выбор компаний для обновления по алгоритму ротации
//     */
//    //todo убрать выбор компаний в бд
//    private List<String> selectCompaniesForUpdate(List<String> allInns, int batchSize) {
//        int dayOfMonth = LocalDate.now().getDayOfMonth();
//
//        List<String> sortedInns = allInns.stream()
//                .sorted()
//                .collect(Collectors.toList());
//
//        int startIndex = (dayOfMonth - 1) * batchSize % sortedInns.size();
//
//        return sortedInns.stream()
//                .skip(startIndex)
//                .limit(batchSize)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Обновление старых компаний (более 60 дней)
//     * Запускается каждый день в 3:00 ночи
//     */
//    @Scheduled(cron = "0 0 3 * * *")
//    public void updateOldCompanies() {
//        log.info("🎯 Starting OLD companies update");
//        long startTime = System.currentTimeMillis();
//
//        try {
//            LocalDateTime oldThreshold = LocalDateTime.now().minusDays(60);
//            List<String> oldCompanyInns = companyCartRepository
//                    .findInnsByLastUpdateBefore(oldThreshold);
//
//            if (oldCompanyInns.isEmpty()) {
//                log.info("📭 No old companies found for update");
//                return;
//            }
//
//            // Ограничиваем количество старых компаний для обновления
//            List<String> innsToUpdate = oldCompanyInns.stream()
//                    .limit(20) // Максимум 20 старых компаний в день
//                    .collect(Collectors.toList());
//
//            log.info("🎯 Old companies update: {} companies", innsToUpdate.size());
//
//            List<CompanyCartEntity> updatedCompanies = companyCartService
//                    .saveCompaniesLargeBatchAtomic(innsToUpdate);
//
//            long duration = System.currentTimeMillis() - startTime;
//            log.info("✅ OLD COMPANIES UPDATE COMPLETED: Updated {}/{} companies in {} ms",
//                    updatedCompanies.size(), innsToUpdate.size(), duration);
//
//        } catch (Exception e) {
//            log.error("❌ OLD COMPANIES UPDATE FAILED", e);
//        }
//    }
//
//    /**
//     * Ручное обновление конкретных компаний с проверкой лимита
//     */
//    public void updateSpecificCompanies(List<String> innsToUpdate) {
//        if (innsToUpdate == null || innsToUpdate.isEmpty()) {
//            log.warn("🚫 No INNs provided for update");
//            return;
//        }
//
//        // Проверяем лимит
//        if (innsToUpdate.size() > DAILY_UPDATE_LIMIT) {
//            log.warn("⚠️ Requested {} companies, but daily limit is {}. Processing first {}",
//                    innsToUpdate.size(), DAILY_UPDATE_LIMIT, DAILY_UPDATE_LIMIT);
//            innsToUpdate = innsToUpdate.stream()
//                    .limit(DAILY_UPDATE_LIMIT)
//                    .collect(Collectors.toList());
//        }
//
//        log.info("🔄 Starting MANUAL update of {} companies", innsToUpdate.size());
//        long startTime = System.currentTimeMillis();
//
//        try {
//            List<CompanyCartEntity> updatedCompanies = companyCartService
//                    .saveCompaniesLargeBatchAtomic(innsToUpdate);
//
//            long duration = System.currentTimeMillis() - startTime;
//            log.info("✅ MANUAL UPDATE COMPLETED: Updated {}/{} companies in {} ms",
//                    updatedCompanies.size(), innsToUpdate.size(), duration);
//
//        } catch (Exception e) {
//            log.error("❌ MANUAL UPDATE FAILED", e);
//            throw new RuntimeException("Manual update failed", e);
//        }
//    }
//
//    /**
//     * Простой метод для получения базовой статистики (без лишнего DTO)
//     */
//    public String getUpdateStatus() {
//        List<String> allInns = companyCartRepository.findAllInns();
//        int totalCompanies = allInns.size();
//
//        if (totalCompanies == 0) {
//            return "No companies in database";
//        }
//
//        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
//        long updatedThisMonth = companyCartRepository.countByLastUpdateTimestampAfter(monthAgo);
//
//        int progress = (int) ((updatedThisMonth * 100) / totalCompanies);
//
//        return String.format("Companies: %d total, %d updated this month (%d%%)",
//                totalCompanies, updatedThisMonth, progress);
//    }
//}