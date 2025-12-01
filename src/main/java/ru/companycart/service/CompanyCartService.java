package ru.companycart.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.companycart.dto.checko.AddressComponents;
import ru.companycart.dto.checko.CheckoResponse;
import ru.companycart.dto.checko.NameComponents;
import ru.companycart.entity.CompanyCartEntity;
import ru.companycart.repository.CompanyCartRepository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CompanyCartService {

    private final CheckoApiService checkoApiService; // ← ЗАМЕНА: CheckoClient → CheckoApiService
    private final AddressParserService addressParserService;
    private final NameParserService nameParserService;
    private final CompanyCartRepository companyCartRepository;

    @Qualifier("apiTaskExecutor")
    private final Executor apiTaskExecutor;

    // ЗАМЕНА в конструкторе
    public CompanyCartService(CheckoApiService checkoApiService, // ← CheckoApiService вместо CheckoClient
                              AddressParserService addressParserService,
                              NameParserService nameParserService,
                              CompanyCartRepository companyCartRepository,
                              Executor apiTaskExecutor) {
        this.checkoApiService = checkoApiService;
        this.addressParserService = addressParserService;
        this.nameParserService = nameParserService;
        this.companyCartRepository = companyCartRepository;
        this.apiTaskExecutor = apiTaskExecutor;
    }

    private final com.google.common.util.concurrent.RateLimiter rateLimiter =
            com.google.common.util.concurrent.RateLimiter.create(2.0);

    /**
     * АТОМАРНАЯ обработка с гарантией целостности данных
     */
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            timeout = 120,
            rollbackFor = Exception.class
    )
    public List<CompanyCartEntity> saveCompaniesBatchAtomic(List<String> innList) {
        if (innList == null || innList.isEmpty()) {
            return List.of();
        }

        log.info("🛡️ Starting OPTIMIZED PARALLEL ATOMIC BATCH for {} companies", innList.size());
        long startTime = System.currentTimeMillis();

        int maxAtomicBatchSize = 10;
        List<String> processingInns = innList.size() > maxAtomicBatchSize
                ? innList.subList(0, maxAtomicBatchSize)
                : innList;

        try {
            List<CompletableFuture<CompanyCartEntity>> futures = processingInns.stream()
                    .map(inn -> CompletableFuture.supplyAsync(
                                            () -> processCompanyAtomically(inn),
                                            apiTaskExecutor
                                    )
                                    .orTimeout(20, TimeUnit.SECONDS)
                                    .exceptionally(throwable -> {
                                        log.warn("⚠️ Company processing failed: {}", throwable.getMessage());
                                        return null;
                                    })
                    )
                    .collect(Collectors.toList());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );

            List<CompanyCartEntity> allResults = allFutures
                    .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()))
                    .get(30, TimeUnit.SECONDS);

            List<CompanyCartEntity> validCompanies = allResults.stream()
                    .filter(result -> result != null && isValidEntityForSave(result))
                    .collect(Collectors.toList());

            log.info("📊 Parallel processing results: {}/{} successful, {} valid",
                    allResults.stream().filter(Objects::nonNull).count(),
                    processingInns.size(),
                    validCompanies.size());

            if (validCompanies.isEmpty()) {
                log.warn("⚠️ No valid companies to save");
                return List.of();
            }

            List<CompanyCartEntity> savedEntities = companyCartRepository.saveAll(validCompanies);

            long duration = System.currentTimeMillis() - startTime;
            log.info("🛡️ OPTIMIZED PARALLEL ATOMIC SUCCESS: Saved {}/{} companies in {} ms",
                    savedEntities.size(), processingInns.size(), duration);

            return savedEntities;

        } catch (Exception e) {
            log.error("🛡️ OPTIMIZED PARALLEL ATOMIC FAILED", e);
            throw new RuntimeException("Optimized parallel atomic processing failed", e);
        }
    }

    /**
     * Атомарная обработка одной компании с улучшенной валидацией
     */
    private CompanyCartEntity processCompanyAtomically(String inn) {
        try {
            if (!rateLimiter.tryAcquire(5, TimeUnit.SECONDS)) {
                log.error("🚫 Rate limit exceeded for INN: {}", inn);
                return null;
            }

            // ЗАМЕНА: executeApiCallWithTimeout → прямой вызов checkoApiService
            CheckoResponse response = checkoApiService.findCompanyByInn(inn);

            if (response == null) {
                log.warn("⚠️ No API response for INN: {}", inn);
                return null;
            }

            // Проверка response.getData() уже сделана в checkoApiService
            // Но оставляем для дополнительной безопасности
            if (response.getData() == null) {
                log.warn("⚠️ No data in API response for INN: {}", inn);
                return null;
            }

            if (!isValidCompanyDataForSave(response)) {
                log.warn("⚠️ Invalid company data for INN: {}", inn);
                return null;
            }

            CompanyCartEntity entity = convertResponseToEntity(response);

            return isValidEntityForSave(entity) ? entity : null;

        } catch (Exception e) {
            log.error("❌ Atomic processing failed for INN: {}", inn, e);
            return null;
        }
    }

    /**
     * УДАЛЯЕМ метод executeApiCallWithTimeout - он больше не нужен
     * Вся логика API вызовов теперь в CheckoApiService
     */
    // ❌ УДАЛЯЕМ этот метод:
    // private CheckoResponse executeApiCallWithTimeout(String inn) { ... }

    /**
     * Обработка больших объемов с атомарными батчами
     */
    public List<CompanyCartEntity> saveCompaniesLargeBatchAtomic(List<String> innList) {
        log.info("🏗️ Starting LARGE ATOMIC BATCH processing for {} companies", innList.size());

        return Lists.partition(innList, 10)
                .stream()
                .flatMap(batch -> {
                    try {
                        List<CompanyCartEntity> saved = saveCompaniesBatchAtomic(batch);
                        log.info("✅ Batch processed: {}/{} companies saved", saved.size(), batch.size());
                        return saved.stream();
                    } catch (Exception e) {
                        log.error("🔴 Atomic batch failed for {} companies, skipping...", batch.size(), e);
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Методы по поиску и сохранению в DB одной компании
     * ОБНОВЛЯЕМ для использования checkoApiService
     */
    public CompanyCartEntity getCompanyByInn(String inn) {
        CheckoResponse response = checkoApiService.findCompanyByInn(inn);

        if (response == null) {
            throw new RuntimeException("Компания с ИНН " + inn + " не найдена в Checko");
        }

        return convertResponseToEntity(response);
    }

    @Transactional
    public CompanyCartEntity saveCompany(String inn) {
        return companyCartRepository.save(getCompanyByInn(inn));
    }

    /**
     * Строгая валидация сущности перед сохранением в БД
     */
    private boolean isValidEntityForSave(CompanyCartEntity entity) {
        if (entity == null) {
            return false;
        }
        if (entity.getInn() == null || entity.getInn().trim().isEmpty()) {
            return false;
        }
        if (entity.getFullName() == null || entity.getFullName().trim().isEmpty()) {
            return false;
        }
        if (entity.getStatus() != null && entity.getStatus().contains("ERROR")) {
            return false;
        }
        return true;
    }

    /**
     * Усиленная валидация данных компании перед сохранением
     */
    private boolean isValidCompanyDataForSave(CheckoResponse response) {
        if (response == null || response.getData() == null) {
            return false;
        }
        if (response.getInn() == null || response.getInn().trim().isEmpty()) {
            return false;
        }
        if (response.getFullName() == null || response.getFullName().trim().isEmpty()) {
            return false;
        }
        if (response.getStatus() != null && response.getStatus().equals("LIQUIDATED")) {
            log.warn("⚠️ Company is liquidated for INN: {}", response.getInn());
            return false;
        }
        return true;
    }

    /**
     * Конвертация Response в Entity
     */
    private CompanyCartEntity convertResponseToEntity(CheckoResponse response) {
        CompanyCartEntity companyCart = new CompanyCartEntity();

        companyCart.setInn(response.getInn());
        companyCart.setKpp(response.getKpp());
        companyCart.setOgrn(response.getOgrn());
        companyCart.setFullName(response.getFullName());
        companyCart.setShortName(response.getShortName());
        companyCart.setStatus(response.getStatus());
        companyCart.setCompanyRegisterDateTimestamp(response.getRegistrationDate());

        String fullAddress = response.getAddress();
        if (fullAddress != null) {
            AddressComponents addressComponents = addressParserService.parseAddress(fullAddress);
            companyCart.setAddress(addressComponents.getFullAddress());
            companyCart.setCompanyZip(addressComponents.getPostalCode());
            companyCart.setCompanyCountry(addressComponents.getCountry());
            companyCart.setCompanyRegion(addressComponents.getRegion());
            companyCart.setCompanyCity(addressComponents.getCity());
            companyCart.setCompanyStreet(addressComponents.getStreet());
            companyCart.setCompanyBuilding(addressComponents.getFullHouseNumber());
        }

        companyCart.setMainOkved(response.getMainOkved());
        companyCart.setAdditionalOkveds(response.getAdditionalOkveds());
        companyCart.setCompanyMsisdn(response.getPhone());
        companyCart.setCompanyEmail(response.getEmail());
        companyCart.setCompanyUrl(response.getWebsite());

        String managerName = response.getManagerName();
        if (managerName != null) {
            NameComponents parsedName = nameParserService.parseFullName(managerName);
            companyCart.setCompanyRepresentativeName(parsedName.getName());
            companyCart.setCompanyRepresentativeLastName(parsedName.getLastName());
            companyCart.setCompanyRepresentativeMiddleName(parsedName.getMidlName());
        }
        companyCart.setCompanyRepresentativePosition(response.getManagerPosition());
        companyCart.setCompanyRepresentativeInn(response.getManagerInn());

        return companyCart;
    }
}