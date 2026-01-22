package ru.companycart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import ru.companycart.checko.dto.CheckoResponse;
import ru.companycart.checko.service.AddressParserService;
import ru.companycart.checko.service.CheckoApiService;
import ru.companycart.checko.service.NameParserService;
import ru.companycart.dto.*;
import ru.companycart.dto.company.CompanyCsvDto;
import ru.companycart.dto.company.CompanyDto;
import ru.companycart.dto.company.CompanyUpdateDto;
import ru.companycart.entity.CompanyCartEntity;
import ru.companycart.exception.repositoryException.CompanyNotExistException;
import ru.companycart.mapper.CompanyMapper;
import ru.companycart.repository.CompanyCartRepository;
import ru.companycart.validation.CheckoValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyCartService {

    private final CheckoApiService checkoApiService;
    private final CompanyMapper companyMapper;
    private final CompanyCartRepository companyCartRepository;
    private final CheckoValidator checkoValidator;
    private final AddressParserService addressParserService;
    private final NameParserService nameParserService;

    /**
     * Поиск одной компании по ИНН
     * @param inn ИНН компании
     * @return CompanyCartEntity
     */
    public CompanyDto fetchCompanyByInn(String inn) {
        log.info("Поиск компании по ИНН: {}", inn);

        CheckoResponse response = checkoApiService.findCompanyByInn(inn);

        checkoValidator.validateCompanyExistsInChecko(response, inn);

        CompanyDto companyDto = companyMapper.fromResponseToDto(response);

        log.info("Компания найдена: {} ({})", companyDto.getFullName(), inn);
        return companyDto;
    }

    /**
     * Пакетный поиск компаний
     * @param innList список ИНН
     * @return список найденных компаний
     */
    private Collection<CompanyCartEntity> fetchCompanyByInnBatch(List<String> innList) {
        log.info("Поиск компаний по списку ИНН ({}): {}", innList.size(), innList);

        final Map<String, CheckoResponse> responses = checkoApiService.findCompaniesByInnsBatch(innList);

        List<CompanyCartEntity> result = new ArrayList<>();
        List<String> foundInns = new ArrayList<>();
        List<String> notFoundInns = new ArrayList<>();

        //todo проверить необходимость еще одной проверки
        for (Map.Entry<String, CheckoResponse> entry : responses.entrySet()) {
            String inn = entry.getKey();
            CheckoResponse response = entry.getValue();

            if (checkoValidator.isCompanyExistsInChecko(response, inn)) {
                result.add(companyMapper.convertToEntity(response));
                foundInns.add(inn);
            } else {
                notFoundInns.add(inn);
            }
        }

        log.info("Результаты поиска:");
        log.info("Найдено компаний: {} с ИНН {}", result.size(), foundInns);
        log.warn("Не найдено: {} компаний с ИНН {}", notFoundInns.size(), notFoundInns);

        if (responses.size() < innList.size()) {
            Set<String> respondedInns = responses.keySet();
            List<String> missingInns = innList.stream()
                    .filter(inn -> !respondedInns.contains(inn))
                    .collect(Collectors.toList());
            log.warn("Нет ответа от API для: {} компаний {}", missingInns.size(), missingInns);
        }
        return result;
    }

    /**
     * Сохранение одной компании в БД
     * @param inn ИНН компании
     * @return сохраненная CompanyCartEntity
     */
    @Transactional
    public CompanyDto saveCompanyFromChecko(String inn) {
        log.info("Сохранение компании по ИНН: {}", inn);

        CompanyDto savedCompany = fetchCompanyByInn(inn);
        companyCartRepository.save(companyMapper.fromDtoToEntity(savedCompany));

        log.info("Компания сохранена в БД: {} (ID: {})", savedCompany.getFullName(), savedCompany.getId());
        return savedCompany;
    }

    /**
     * Пакетное сохранение компаний
     * @param innList список ИНН
     * @return список сохраненных компаний
     */
    @Transactional
    public Collection<CompanyDto> fetchAndSaveCompanyByInnBatch(List<String> innList) {
        log.info("Поиск и сохранение компаний по списку ИНН: {}", innList);

        final Collection<CompanyCartEntity> companyCartEntities = fetchCompanyByInnBatch(innList);
        final List<CompanyCartEntity> companyCartList = companyCartRepository.saveAll(companyCartEntities);
        return companyCartList.stream()
                .map(companyMapper::fromEntityToDto)
                .toList();
    }

    /**
     * Поиск и обновление компании
     * @param inn ИНН компании
     */
    @Transactional
    public CompanyDto fetchAndUpdateCompanyByInn(String inn) {
        log.info("Поиск и обновление компании по ИНН: {}", inn);

        CompanyCartEntity existingCompany = companyCartRepository.findByInn(inn)
                .orElseThrow(() -> new CompanyNotExistException(inn));

        CheckoResponse freshData = checkoApiService.findCompanyByInn(inn);
        checkoValidator.validateCompanyExistsInChecko(freshData, inn);

        existingCompany.updateCompanyEntity(fromResponseToCompanyUpdateDto(freshData));

        log.info("Компания обновлена: {} (ID: {})",
                existingCompany.getFullName(), existingCompany.getId());

        return companyMapper.fromEntityToDto(existingCompany);
    }

    public byte[] getOldestActualCompanyAsByteArray() {
        CompanyCartEntity entity = companyCartRepository.getOldestActualCompany();
        CompanyCsvDto companyCsvDto = companyMapper.fromEntityToCompanyCsvDto(entity);
        log.info("Что-то [{}]", entity);
        log.info("Что-то 2 [{}]", companyCsvDto);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ICsvBeanWriter writer = new CsvBeanWriter(
                     new OutputStreamWriter(byteArrayOutputStream),
                     CsvPreference.STANDARD_PREFERENCE
             )) {

            final String[] nameMapping = {
                    "id",
                    "companyKind",
                    "companyRegisterIp",
                    "companyRegisterDate",
                    "companyUpdateDate",
                    "companyContractTerminationDate",
                    "companyContractNumber",
                    "companyContractConclusionDate",
                    "ufName",
                    "ufLastName",
                    "ufMiddleName",
                    "ufBirthday",
                    "ufActualZip",
                    "ufActualCountry",
                    "ufActualRegion",
                    "ufActualZone",
                    "ufActualCity",
                    "ufActualStreet",
                    "ufActualBuilding",
                    "ufActualBuildSect",
                    "ufActualApartment",
                    "ufRegisteredZip",
                    "ufRegisteredCountry",
                    "ufRegisteredRegion",
                    "ufRegisteredZone",
                    "ufRegisteredCity",
                    "ufRegisteredStreet",
                    "ufRegisteredBuilding",
                    "ufRegisteredBuildSect",
                    "ufRegisteredApartment",
                    "ufPassportSeries",
                    "ufPassportNumber",
                    "ufPassportIssuedBy",
                    "ufMsisdn",
                    "ufEmail",
                    "AdditionalInfo",
                    "shortName",
                    "fullName",
                    "ogrn",
                    "inn",
                    "companyUrl",
                    "companyRegisterDateTimestamp",
                    "okveds",
                    "address",
                    "companyZip",
                    "companyCountry",
                    "companyRegion",
                    "companyZone",
                    "companyCity",
                    "companyStreet",
                    "companyBuilding",
                    "ufCompanyBuildSect",
                    "ufCompanyApartment",
                    "companyMsisdn",
                    "companyEmail",
                    "companyRepresentativeName",
                    "companyRepresentativeLastName",
                    "companyRepresentativeInn",
                    "companyRepresentativePosition",
                    "companyBankName",
                    "companyBankAccount",
                    "companyBankCorrAccount",
                    "companyBankCardNumber",
                    "companyBankRcbic",
                    "companyBankKpp"
            };

            writer.write(companyCsvDto, nameMapping);
            writer.flush();

            String result = byteArrayOutputStream.toString(StandardCharsets.UTF_8);

            result = result.replaceAll("\"\"\"", "\"");
            result = result.replaceAll("\"\"", "\"");
            result = result.replace("\\\"", "\"");

            return result.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompanyUpdateDto fromResponseToCompanyUpdateDto(CheckoResponse response) {
        if (response == null) {
            return null;
        }

        CompanyUpdateDto updateDto = new CompanyUpdateDto();

        AddressComponents addressComponents = addressParserService.parseAddress(response.getAddress());
        NameComponents nameComponents = nameParserService.parseFullName(response.getManagerName());

        return updateDto.setStatus(response.getStatus())
                .setAddress(addressComponents.getFullAddress())
                .setMainOkved(response.getMainOkved())
                .setAdditionalOkveds(response.getAdditionalOkveds())
                .setCompanyUrl(response.getWebsite())
                .setCompanyRegisterDateTimestamp(response.getRegistrationDate())
                .setCompanyZip(addressComponents.getPostalCode())
                .setCompanyCountry(addressComponents.getCountry())
                .setCompanyRegion(addressComponents.getRegion())
                .setCompanyCity(addressComponents.getCity())
                .setCompanyStreet(addressComponents.getStreet())
                .setCompanyBuilding(addressComponents.getFullHouseNumber())
                .setCompanyMsisdn(response.getPhone())
                .setCompanyEmail(response.getEmail())
                .setCompanyRepresentativeName(nameComponents.getName())
                .setCompanyRepresentativeMiddleName(nameComponents.getMiddleName())
                .setCompanyRepresentativeLastName(nameComponents.getLastName())
                .setCompanyRepresentativeInn(response.getManagerInn())
                .setCompanyRepresentativePosition(response.getManagerPosition());
    }
}