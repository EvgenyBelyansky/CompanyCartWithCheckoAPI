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

import java.time.Instant;
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
        dto.setUrl(entity.getUrl());
        dto.setRegisterDateTimestamp(entity.getRegisterDateTimestamp());
        dto.setZip(entity.getZip());
        dto.setCountry(entity.getCountry());
        dto.setRegion(entity.getRegion());
        dto.setCity(entity.getCity());
        dto.setStreet(entity.getStreet());
        dto.setBuilding(entity.getBuilding());
        dto.setMsisdn(entity.getMsisdn());
        dto.setEmail(entity.getEmail());
        dto.setRepresentativeName(entity.getRepresentativeName());
        dto.setRepresentativeMiddleName(entity.getRepresentativeMiddleName());
        dto.setRepresentativeLastName(entity.getRepresentativeLastName());
        dto.setRepresentativeInn(entity.getRepresentativeInn());
        dto.setRepresentativePosition(entity.getRepresentativePosition());

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
                .companyRegisterDateTimestamp(dto.getRegisterDateTimestamp())
                .mainOkved(dto.getMainOkved())
                .additionalOkveds(dto.getAdditionalOkveds())
                .companyMsisdn(dto.getMsisdn())
                .companyEmail(dto.getEmail())
                .companyUrl(dto.getUrl())
                .companyRepresentativePosition(dto.getRepresentativePosition())
                .companyRepresentativeInn(dto.getRepresentativeInn())
                .address(dto.getAddress())
                .companyZip(dto.getZip())
                .companyCountry(dto.getCountry())
                .companyRegion(dto.getRegion())
                .companyCity(dto.getCity())
                .companyStreet(dto.getStreet())
                .companyBuilding(dto.getBuilding())
                .companyRepresentativeName(dto.getRepresentativeName())
                .companyRepresentativeLastName(dto.getRepresentativeLastName())
                .companyRepresentativeMiddleName(dto.getRepresentativeMiddleName())
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
        dto.setUrl(response.getWebsite());
        dto.setRegisterDateTimestamp(response.getRegistrationDate());
        dto.setZip(addressComponents.getPostalCode());
        dto.setCountry(addressComponents.getCountry());
        dto.setRegion(addressComponents.getRegion());
        dto.setCity(addressComponents.getCity());
        dto.setStreet(addressComponents.getStreet());
        dto.setBuilding(addressComponents.getFullHouseNumber());
        dto.setMsisdn(response.getPhone());
        dto.setEmail(response.getEmail());
        dto.setRepresentativeName(nameComponents.getName());
        dto.setRepresentativeMiddleName(nameComponents.getMiddleName());
        dto.setRepresentativeLastName(nameComponents.getLastName());
        dto.setRepresentativeInn(response.getManagerInn());
        dto.setRepresentativePosition(response.getManagerPosition());

        return dto;
    }

    public CompanyCsvDto fromEntityToCompanyCsvDto(CompanyCartEntity entity) {
        ArrayList<String> allOkveds = new ArrayList<>();
        allOkveds.add(entity.getMainOkved());
        allOkveds.addAll(entity.getAdditionalOkveds());

        return CompanyCsvDto.builder()
                .id(entity.getCompanyId().toString())
                .kind(entity.getKind())
                .registerIp(entity.getRegisterIp())
                .registerDate(fromInstantToLong(entity.getCreatedDate()))
                .contractTerminationDate(fromLocalDateToLong(entity.getContractTerminationDate()))
                .contractNumber(entity.getContractNumber())
                .ogrn(entity.getOgrn())
                .shortName(cleanCompanyName(entity.getShortName()))
                .fullName(cleanCompanyName(entity.getFullName()))
                .okveds(allOkveds)
                .url(entity.getUrl())
                .registerDateTimestamp(fromStringDateToLong(entity.getRegisterDateTimestamp()))
                .zip(entity.getZip())
                .country(entity.getCountry())
                .region(entity.getRegion())
                .city(entity.getCity())
                .street(entity.getStreet())
                .building(entity.getBuilding())
                .msisdn(entity.getMsisdn())
                .email(entity.getEmail())
                .representativeName(entity.getRepresentativeName())
                .representativeMiddleName(entity.getRepresentativeMiddleName())
                .representativeLastName(entity.getRepresentativeLastName())
                .representativeInn(entity.getRepresentativeInn())
                .representativePosition(entity.getRepresentativePosition())
                .build();
    }



    private String cleanCompanyName(String name) {
        if (name == null) {
            return null;
        }
        return name.replace("\"", "").trim();
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

    private Long fromInstantToLong(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.getEpochSecond();
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
