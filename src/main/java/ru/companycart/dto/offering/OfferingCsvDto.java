package ru.companycart.dto.offering;

import lombok.*;

@Builder
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@ToString
public class OfferingCsvDto {

    private String companyId;

    private String name;

    @Builder.Default
    @Getter
    private Long registerDate = null;

    @Builder.Default
    @Getter
    private Long updateDate = null;

    @Builder.Default
    @Getter
    private Long unregisterDate = null;

    @Builder.Default
    private String additionalInfo = "";

    public String getCompanyId() {
        return formatWithSingleQuotes(companyId);
    }

    public String getName() {
        return formatWithSingleQuotes(name);
    }

    public String getAdditionalInfo() {
        return formatWithSingleQuotes(additionalInfo);
    }

    private String formatWithSingleQuotes(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String escaped = value.replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
}
