package ru.companycart.dto.company;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@ToString
public class CompanyCsvDto {
    private String id; //1
    @Getter
    @Builder.Default
    private int kind = 4; //2
    @Builder.Default
    private String registerIp = ""; //3
    @Getter
    @Builder.Default
    private Long registerDate = null; //4
    @Getter
    @Builder.Default
    private Long contractTerminationDate = null; //5
    private String contractNumber; //6
    @Builder.Default
    private String shortName = ""; //7
    private String fullName; //8
    private String ogrn; //9
    @Builder.Default
    private String url = ""; //10
    @Getter
    private Long registerDateTimestamp; //11
    private List<String> okveds; //12
    @Builder.Default
    private String egrul = ""; //13
    @Builder.Default
    private String egrip = "";  //14
    private String zip; //15
    private String country; //16
    @Builder.Default
    private String region = ""; //17
    private String city; //18
    private String street; //19
    private String building; //20
    private List<String> msisdn; //21
    @Builder.Default
    private String email = ""; //22
    private String representativeName; //23
    private String representativeMiddleName; //24
    private String representativeLastName; //25
    @Builder.Default
    private String representativeInn = ""; //26
    @Builder.Default
    private String representativePosition = ""; //27

    public String getId() {
        return formatWithSingleQuotes(id);
    }

    public String getRegisterIp() {
        return formatWithSingleQuotes(registerIp);
    }

    public String getContractNumber() {
        return formatWithSingleQuotes(contractNumber);
    }

    public String getShortName() {
        return formatWithSingleQuotes(shortName);
    }

    public String getFullName() {
        return formatWithSingleQuotes(fullName);
    }

    public String getOgrn() {
        return formatWithSingleQuotes(ogrn);
    }

    public String getUrl() {
        return formatWithSingleQuotes(url);
    }

    public String getOkveds() {
        return formatListWithSingleQuotes(okveds);
    }

    public String getEgrul() {
        return formatWithSingleQuotes(egrul);
    }

    public String getEgrip() {
        return formatWithSingleQuotes(egrip);
    }

    public String getZip() {
        return formatWithSingleQuotes(zip);
    }

    public String getCountry() {
        return formatWithSingleQuotes(country);
    }

    public String getRegion() {
        return formatWithSingleQuotes(region);
    }

    public String getCity() {
        return formatWithSingleQuotes(city);
    }

    public String getStreet() {
        return formatWithSingleQuotes(street);
    }

    public String getBuilding() {
        return formatWithSingleQuotes(building);
    }

    public String getMsisdn() {
        return formatListWithSingleQuotes(msisdn);
    }

    public String getEmail() {
        return formatWithSingleQuotes(email);
    }

    public String getRepresentativeName() {
        return formatWithSingleQuotes(representativeName);
    }

    public String getRepresentativeMiddleName() {
        return formatWithSingleQuotes(representativeMiddleName);
    }

    public String getRepresentativeLastName() {
        return formatWithSingleQuotes(representativeLastName);
    }

    public String getRepresentativeInn() {
        return formatWithSingleQuotes(representativeInn);
    }

    public String getRepresentativePosition() {
        return formatWithSingleQuotes(representativePosition);
    }

    private String formatWithSingleQuotes(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String escaped = value.replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }

    private String formatWithSingleQuotesForListElements(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return "'" + value.replace("'", "\\") + "'";
    }

    private String formatListWithSingleQuotes(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(this::formatWithSingleQuotesForListElements)
                .collect(Collectors.joining(", ", "[", "]"));
    }

}
