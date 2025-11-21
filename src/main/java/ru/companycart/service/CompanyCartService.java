package ru.companycart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.companycart.client.CheckoClient;
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
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompanyCartService {

    private final CheckoClient checkoClient;
    private final AddressParserService addressParserService;
    private final NameParserService nameParserService;
    private final CompanyCartRepository companyCartRepository;

    @Qualifier("apiTaskExecutor")
    private final Executor apiTaskExecutor;

    public CompanyCartService(CheckoClient checkoClient,
                              AddressParserService addressParserService,
                              NameParserService nameParserService,
                              CompanyCartRepository companyCartRepository,
                              Executor apiTaskExecutor) {
        this.checkoClient = checkoClient;
        this.addressParserService = addressParserService;
        this.nameParserService = nameParserService;
        this.companyCartRepository = companyCartRepository;
        this.apiTaskExecutor = apiTaskExecutor;
    }

    private final com.google.common.util.concurrent.RateLimiter rateLimiter =
            com.google.common.util.concurrent.RateLimiter.create(2.0);

    /**
     * Попытка в многопоточность
     * НОВЫЙ МЕТОД: Параллельная обработка списка INN
     */
    public List<CompanyCartEntity> getCompaniesByInnBatch(List<String> innList) {
        if (innList == null || innList.isEmpty()) {
            return List.of();
        }

        log.info("Starting parallel processing of {} INNs", innList.size());
        long startTime = System.currentTimeMillis();

        try {
            List<CompletableFuture<CompanyCartEntity>> futures = innList.stream()
                    .map(inn -> CompletableFuture.supplyAsync(
                            () -> getCompanyByInn(inn),
                            apiTaskExecutor
                    ))
                    .collect(Collectors.toList());

            List<CompanyCartEntity> results = futures.stream()
                    .map(this::safeGetFuture)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed processing {}/{} companies in {} ms",
                    results.size(), innList.size(), duration);

            return results;

        } catch (Exception e) {
            log.error("Error in parallel processing", e);
            return List.of();
        }
    }

    /**
     * НОВЫЙ МЕТОД: Параллельное сохранение компаний
     */
    @Transactional
    public List<CompanyCartEntity> saveCompaniesBatch(List<String> innList) {
        if (innList == null || innList.isEmpty()) {
            return List.of();
        }

        log.info("💾 Starting BATCH SAVE transaction for {} companies", innList.size());
        long startTime = System.currentTimeMillis();

        try {
            List<CompanyCartEntity> companies = getCompaniesParallelOptimized(innList);

            List<CompanyCartEntity> validCompanies = companies.stream()
                    .filter(company -> company.getInn() != null && !company.getInn().trim().isEmpty())
                    .collect(Collectors.toList());

            if (validCompanies.size() < companies.size()) {
                log.warn("⚠️  Filtered out {} companies with null/missing INN",
                        companies.size() - validCompanies.size());
            }

            List<CompanyCartEntity> savedCompanies = companyCartRepository.saveAll(validCompanies);

            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ BATCH SAVE SUCCESS: Saved {}/{} companies in {} ms (ACID compliant)",
                    savedCompanies.size(), innList.size(), duration);

            return savedCompanies;

        } catch (Exception e) {
            log.error("❌ BATCH SAVE FAILED: Transaction rolled back for all companies", e);
            throw new RuntimeException("Batch save transaction failed", e);
        }
    }

    /**
     * Безопасное получение результата из Future
     */
    private <T> T safeGetFuture(CompletableFuture<T> future) {
        try {
            return future.get(30, TimeUnit.SECONDS); // Таймаут 30 секунд
        } catch (Exception e) {
            log.error("Error waiting for future result", e);
            return null;
        }
    }

    /**
     * Альтернативный метод получения компаний
     * */
    public List<CompanyCartEntity> getCompaniesParallelOptimized(List<String> innList) {
        if (innList == null || innList.isEmpty()) {
            return List.of();
        }

        log.info("🚀 Starting OPTIMIZED PARALLEL processing of {} INNs", innList.size());
        long startTime = System.currentTimeMillis();

        try {
            if (!rateLimiter.tryAcquire(innList.size(), 30, TimeUnit.SECONDS)) {
                log.error("Rate limit exceeded for {} requests", innList.size());
                return createErrorResponses(innList, "Rate limit exceeded");
            }

            List<CompletableFuture<CheckoResponse>> apiFutures = innList.stream()
                    .map(inn -> CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    log.debug("🔁 API call for INN: {}", inn);
                                    return checkoClient.findCompanyByInn(inn);
                                } catch (Exception e) {
                                    log.error("❌ API call failed for INN: {}", inn, e);
                                    return null;
                                }
                            },
                            apiTaskExecutor
                    ))
                    .collect(Collectors.toList());

            List<CheckoResponse> apiResponses = apiFutures.stream()
                    .map(future -> {
                        try {
                            return future.get(10, TimeUnit.SECONDS);
                        } catch (TimeoutException e) {
                            log.warn("⏰ API timeout for INN");
                            future.cancel(true);
                            return null;
                        } catch (Exception e) {
                            log.error("❌ Future error", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            long apiDuration = System.currentTimeMillis() - startTime;
            log.info("📡 API calls completed: {}/{} responses in {} ms",
                    apiResponses.size(), innList.size(), apiDuration);

            List<CompanyCartEntity> results = apiResponses.stream()
                    .map(response -> {
                        try {
                            if (response == null || response.getData() == null) {
                                return createErrorEntity(response != null ? response.getInn() : "UNKNOWN", "No data");
                            }
                            return convertResponseToEntity(response);
                        } catch (Exception e) {
                            log.error("❌ Conversion error for INN: {}",
                                    response != null ? response.getInn() : "UNKNOWN", e);
                            return createErrorEntity(response != null ? response.getInn() : "UNKNOWN", "Conversion error");
                        }
                    })
                    .collect(Collectors.toList());

            long totalDuration = System.currentTimeMillis() - startTime;
            log.info("✅ Completed OPTIMIZED PARALLEL processing: {}/{} companies in {} ms (API: {} ms, Convert: {} ms)",
                    results.size(), innList.size(), totalDuration,
                    apiDuration, totalDuration - apiDuration);

            return results;

        } catch (Exception e) {
            log.error("💥 Critical error in optimized parallel processing", e);
            return createErrorResponses(innList, "Processing error: " + e.getMessage());
        }
    }


    private List<CompanyCartEntity> createErrorResponses(List<String> innList, String errorMessage) {
        return innList.stream()
                .map(inn -> createErrorEntity(inn, errorMessage))
                .collect(Collectors.toList());
    }

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

    /**
     * Создание сущности с ошибкой
     */
    private CompanyCartEntity createErrorEntity(String inn, String errorMessage) {
        CompanyCartEntity entity = new CompanyCartEntity();
        entity.setInn(inn);
        entity.setStatus("ERROR");
        return entity;
    }


    /**
     * Методы по поиску и сохранению в DB одной компании
     */
    public CompanyCartEntity getCompanyByInn(String inn) {
        CheckoResponse response = checkoClient.findCompanyByInn(inn);

        if (response == null || response.getData() == null) {
            throw new RuntimeException("Компания с ИНН " + inn + " не найдена в Checko");
        }

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

    @Transactional
    public CompanyCartEntity saveCompany(String inn) {
        return companyCartRepository.save(getCompanyByInn(inn));
    }


}
