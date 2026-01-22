package ru.companycart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import ru.companycart.dto.offering.OfferingCsvDto;
import ru.companycart.dto.offering.OfferingInputDto;
import ru.companycart.dto.offering.OfferingOutputDto;
import ru.companycart.entity.OfferingEntity;
import ru.companycart.mapper.OfferingMapper;
import ru.companycart.repository.OfferingRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final OfferingMapper offeringMapper;
    private final Clock clock;

    @Transactional
    public OfferingOutputDto addOffering(OfferingInputDto inputDto) {
        return offeringMapper.fromEntityToOutputDto(
                offeringRepository.save(offeringMapper.fromInputDtoToEntity(inputDto.getCompanyId(), inputDto))
        );
    }

    public OfferingOutputDto getOfferingById(UUID id) {
        return offeringMapper.fromEntityToOutputDto(offeringRepository.getCompanyOfferingEntityById(id));
    }

    public Collection<OfferingOutputDto> findOfferingByCompanyId(long id) {

        List<OfferingEntity> offeringEntityList = offeringRepository
                .getCompanyOfferingEntitiesByCompanyCartEntity_CompanyId(id).stream()
                .toList();
        return offeringEntityList.stream()
                .map(offeringMapper::fromEntityToOutputDto)
                .toList();
    }

    public byte[] getOldestActualOfferingAsByteArray() {
        OfferingEntity entity = offeringRepository.getOldestActualCompany();
        OfferingCsvDto offeringCsvDto = offeringMapper.fromEntityToCsvDto(entity);
        log.info("Что-то [{}]", entity);
        log.info("Что-то 2 [{}]", offeringCsvDto);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ICsvBeanWriter writer = new CsvBeanWriter(
                     new OutputStreamWriter(byteArrayOutputStream),
                     CsvPreference.STANDARD_PREFERENCE
             )) {

            final String[] nameMapping = {
                    "companyId",
                    "name",
                    "registerDate",
                    "updateDate",
                    "unregisterDate",
                    "additionalInfo"
            };

            writer.write(offeringCsvDto, nameMapping);
            writer.flush();

            String result = byteArrayOutputStream.toString(StandardCharsets.UTF_8);

            result = result.replaceAll("\"\"\"", "\"");
            result = result.replaceAll("\"\"", "\"");
            result = result.replace("\\\"", "\"");

            entity.markAsInProgress(clock.instant());
            return result.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
