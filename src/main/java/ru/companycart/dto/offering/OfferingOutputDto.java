package ru.companycart.dto.offering;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Getter
@ToString
public class OfferingOutputDto {

    private UUID id;

    private long companyId;

    private String name;

    private Instant registerDate;

    @Builder.Default
    private Instant updateDate = null;

    @Builder.Default
    private Instant unregisterDate = null;

    @Builder.Default
    private String additionalInfo = "";
}
