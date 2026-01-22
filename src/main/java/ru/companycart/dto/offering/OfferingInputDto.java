package ru.companycart.dto.offering;

import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Getter
@ToString
public class OfferingInputDto {

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