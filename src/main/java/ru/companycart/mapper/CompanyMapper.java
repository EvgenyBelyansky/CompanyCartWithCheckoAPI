package ru.companycart.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.companycart.checko.dto.CheckoResponse;
import ru.companycart.checko.service.AddressParserService;
import ru.companycart.checko.service.NameParserService;
import ru.companycart.dto.AddressComponents;
import ru.companycart.dto.company.CompanyCsvDto;
import ru.companycart.dto.company.CompanyDto;
import ru.companycart.dto.NameComponents;
import ru.companycart.entity.CompanyCartEntity;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final AddressParserService addressParserService;
    private final NameParserService nameParserService;

    /**
     * Конвертация CheckoResponse в CompanyCartEntity
     */
    public CompanyCartEntity convertToEntity(CheckoResponse response) {
        if (response == null) {
            return null;
        }

        AddressComponents addressComponents = addressParserService.parseAddress(response.getAddress());
        NameComponents nameComponents = nameParserService.parseFullName(response.getManagerName());

        return CompanyCartEntity.builder()
                .inn(response.getInn())
                .kpp(response.getKpp())
                .ogrn(response.getOgrn())
                .fullName(response.getFullName())
                .shortName(response.getShortName())
                .status(response.getStatus())
                .companyRegisterDateTimestamp(response.getRegistrationDate())
                .mainOkved(response.getMainOkved())
                .additionalOkveds(response.getAdditionalOkveds())
                .companyMsisdn(response.getPhone())
                .companyEmail(response.getEmail())
                .companyUrl(response.getWebsite())
                .companyRepresentativePosition(response.getManagerPosition())
                .companyRepresentativeInn(response.getManagerInn())
                .address(addressComponents.getFullAddress())
                .companyZip(addressComponents.getPostalCode())
                .companyCountry(addressComponents.getCountry())
                .companyRegion(addressComponents.getRegion())
                .companyCity(addressComponents.getCity())
                .companyStreet(addressComponents.getStreet())
                .companyBuilding(addressComponents.getFullHouseNumber())
                .companyRepresentativeName(nameComponents.getName())
                .companyRepresentativeLastName(nameComponents.getLastName())
                .companyRepresentativeMiddleName(nameComponents.getMiddleName())
                .build();
    }

    public CompanyDto fromEntityToDto(CompanyCartEntity entity) {

        CompanyDto dto = new CompanyDto();

        dto.setInn(entity.getInn());
        dto.setKpp(entity.getKpp());
        dto.setOgrn(entity.getOgrn());
        dto.setFullName(entity.getFullName());
        dto.setShortName(entity.getShortName());
        dto.setStatus(entity.getStatus());
        dto.setAddress(entity.getAddress());
        dto.setMainOkved(entity.getMainOkved());
        dto.setAdditionalOkveds(entity.getAdditionalOkveds());
        dto.setCompanyUrl(entity.getCompanyUrl());
        dto.setCompanyRegisterDateTimestamp(entity.getCompanyRegisterDateTimestamp());
        dto.setCompanyZip(entity.getCompanyZip());
        dto.setCompanyCountry(entity.getCompanyCountry());
        dto.setCompanyRegion(entity.getCompanyRegion());
        dto.setCompanyCity(entity.getCompanyCity());
        dto.setCompanyStreet(entity.getCompanyStreet());
        dto.setCompanyBuilding(entity.getCompanyBuilding());
        dto.setCompanyMsisdn(entity.getCompanyMsisdn());
        dto.setCompanyEmail(entity.getCompanyEmail());
        dto.setCompanyRepresentativeName(entity.getCompanyRepresentativeName());
        dto.setCompanyRepresentativeMiddleName(entity.getCompanyRepresentativeMiddleName());
        dto.setCompanyRepresentativeLastName(entity.getCompanyRepresentativeLastName());
        dto.setCompanyRepresentativeInn(entity.getCompanyRepresentativeInn());
        dto.setCompanyRepresentativePosition(entity.getCompanyRepresentativePosition());

        return dto;
    }

    public CompanyCartEntity fromDtoToEntity(CompanyDto dto) {

        return CompanyCartEntity.builder()
                .inn(dto.getInn())
                .kpp(dto.getKpp())
                .ogrn(dto.getOgrn())
                .fullName(dto.getFullName())
                .shortName(dto.getShortName())
                .status(dto.getStatus())
                .companyRegisterDateTimestamp(dto.getCompanyRegisterDateTimestamp())
                .mainOkved(dto.getMainOkved())
                .additionalOkveds(dto.getAdditionalOkveds())
                .companyMsisdn(dto.getCompanyMsisdn())
                .companyEmail(dto.getCompanyEmail())
                .companyUrl(dto.getCompanyUrl())
                .companyRepresentativePosition(dto.getCompanyRepresentativePosition())
                .companyRepresentativeInn(dto.getCompanyRepresentativeInn())
                .address(dto.getAddress())
                .companyZip(dto.getCompanyZip())
                .companyCountry(dto.getCompanyCountry())
                .companyRegion(dto.getCompanyRegion())
                .companyCity(dto.getCompanyCity())
                .companyStreet(dto.getCompanyStreet())
                .companyBuilding(dto.getCompanyBuilding())
                .companyRepresentativeName(dto.getCompanyRepresentativeName())
                .companyRepresentativeLastName(dto.getCompanyRepresentativeLastName())
                .companyRepresentativeMiddleName(dto.getCompanyRepresentativeMiddleName())
                .build();
    }


    public CompanyDto fromResponseToDto(CheckoResponse response) {
        if (response == null) {
            return null;
        }

        CompanyDto dto = new CompanyDto();

        AddressComponents addressComponents = addressParserService.parseAddress(response.getAddress());
        NameComponents nameComponents = nameParserService.parseFullName(response.getManagerName());

        dto.setInn(response.getInn());
        dto.setKpp(response.getKpp());
        dto.setOgrn(response.getOgrn());
        dto.setFullName(response.getFullName());
        dto.setShortName(response.getShortName());
        dto.setStatus(response.getStatus());
        dto.setAddress(addressComponents.getFullAddress());
        dto.setMainOkved(response.getMainOkved());
        dto.setAdditionalOkveds(response.getAdditionalOkveds());
        dto.setCompanyUrl(response.getWebsite());
        dto.setCompanyRegisterDateTimestamp(response.getRegistrationDate());
        dto.setCompanyZip(addressComponents.getPostalCode());
        dto.setCompanyCountry(addressComponents.getCountry());
        dto.setCompanyRegion(addressComponents.getRegion());
        dto.setCompanyCity(addressComponents.getCity());
        dto.setCompanyStreet(addressComponents.getStreet());
        dto.setCompanyBuilding(addressComponents.getFullHouseNumber());
        dto.setCompanyMsisdn(response.getPhone());
        dto.setCompanyEmail(response.getEmail());
        dto.setCompanyRepresentativeName(nameComponents.getName());
        dto.setCompanyRepresentativeMiddleName(nameComponents.getMiddleName());
        dto.setCompanyRepresentativeLastName(nameComponents.getLastName());
        dto.setCompanyRepresentativeInn(response.getManagerInn());
        dto.setCompanyRepresentativePosition(response.getManagerPosition());

        return dto;
    }

    public CompanyCsvDto fromEntityToCompanyCsvDto(CompanyCartEntity entity) {
        ArrayList<String> allOkveds = new ArrayList<>();
        allOkveds.add(entity.getMainOkved());
        allOkveds.addAll(entity.getAdditionalOkveds());

        return CompanyCsvDto.builder()
                .id(entity.getCompanyId().toString())
                .companyKind(entity.getCompanyKind())
                .companyRegisterIp(entity.getCompanyRegisterIp())
                .companyContractConclusionDate(fromLocalDateToLong(entity.getCompanyContractConclusionDate()))
                .companyContractTerminationDate(fromLocalDateToLong(entity.getCompanyContractTerminationDate()))
                .companyContractNumber(entity.getCompanyContractNumber())
                .inn(entity.getInn())
                .ogrn(entity.getOgrn())
                .fullName(cleanCompanyName(entity.getFullName()))
                .shortName(cleanCompanyName(entity.getShortName()))
                .status(entity.getStatus())
                .address(entity.getAddress().replace(",", ""))
                .okveds(allOkveds)
                .companyUrl(entity.getCompanyUrl())
                .companyRegisterDateTimestamp(fromStringDateToLong(entity.getCompanyRegisterDateTimestamp()))
                .companyZip(entity.getCompanyZip())
                .companyCountry(entity.getCompanyCountry())
                .companyRegion(entity.getCompanyRegion())
                .companyCity(entity.getCompanyCity())
                .companyStreet(entity.getCompanyStreet())
                .companyBuilding(entity.getCompanyBuilding())
                .companyMsisdn(entity.getCompanyMsisdn())
                .companyEmail(entity.getCompanyEmail())
                .companyRepresentativeName(entity.getCompanyRepresentativeName())
                .companyRepresentativeMiddleName(entity.getCompanyRepresentativeMiddleName())
                .companyRepresentativeLastName(entity.getCompanyRepresentativeLastName())
                .companyRepresentativeInn(entity.getCompanyRepresentativeInn())
                .companyRepresentativePosition(entity.getCompanyRepresentativePosition())
                .build();
    }



    private String cleanCompanyName(String name) {
        if (name == null) {
            return null;
        }

        return "\"" + name.replace("\"", "") + "\"";
    }

    private Long fromLocalDateToLong(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
    }

    private Long fromStringDateToLong(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(date);
        return localDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * Batch конвертация
     */
    public List<CompanyCartEntity> convertToList(List<CheckoResponse> responses) {
        if (responses == null) {
            return Collections.emptyList();
        }

        return responses.stream().map(this::convertToEntity).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
