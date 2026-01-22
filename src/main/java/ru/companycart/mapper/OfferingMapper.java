package ru.companycart.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.companycart.dto.offering.OfferingInputDto;
import ru.companycart.dto.offering.OfferingCsvDto;
import ru.companycart.dto.offering.OfferingOutputDto;
import ru.companycart.entity.CompanyCartEntity;
import ru.companycart.entity.OfferingEntity;
import ru.companycart.repository.CompanyCartRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OfferingMapper {

    private final CompanyCartRepository companyCartRepository;

    public OfferingCsvDto fromEntityToCsvDto(OfferingEntity entity) {
        return OfferingCsvDto.builder()
                .companyId(entity.getCompanyCartEntity().getCompanyId().toString())
                .name(entity.getName())
                .registerDate(fromInstantToLong(entity.getCreatedDate()))
                .updateDate(fromInstantToLong(entity.getUpdateDate()))
                .unregisterDate(fromInstantToLong(entity.getUnregisterDate()))
                .additionalInfo(entity.getAdditionalInfo())
                .build();
    }

    public OfferingOutputDto fromEntityToOutputDto(OfferingEntity entity) {
        return OfferingOutputDto.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyCartEntity().getCompanyId())
                .name(entity.getName())
                .registerDate(entity.getCreatedDate())
                .updateDate(entity.getUpdateDate())
                .unregisterDate(entity.getUnregisterDate())
                .additionalInfo(entity.getAdditionalInfo())
                .build();
    }

    public OfferingEntity fromInputDtoToEntity(long companyId, OfferingInputDto dto) {

        CompanyCartEntity companyCartEntity = companyCartRepository.getCompanyCartEntitiesByCompanyId(companyId);

        return OfferingEntity.builder()
                .companyCart(companyCartEntity)
                .name(dto.getName())
                .additionalInfo(dto.getAdditionalInfo())
                .build();
    }

    private Long fromInstantToLong(Instant date) {
        if (date == null) {
            return null;
        }
        return date.getEpochSecond();
    }
}
