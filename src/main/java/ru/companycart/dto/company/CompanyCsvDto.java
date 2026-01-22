package ru.companycart.dto.company;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@ToString
public class CompanyCsvDto {
    private String id;
    @Getter
    @Builder.Default
    private int companyKind = 4;
    @Builder.Default
    private String companyRegisterIp = "";
    @Getter
    private Long companyRegisterDate;
    @Getter
    @Builder.Default
    private Long companyUpdateDate = null;
    @Getter
    @Builder.Default
    private Long companyContractTerminationDate = null;
    private String companyContractNumber;
    @Getter
    private Long companyContractConclusionDate;
    @Builder.Default
    private String ufName = "";
    @Builder.Default
    private String ufLastName = "";
    @Builder.Default
    private String ufMiddleName = "";
    @Getter
    @Builder.Default
    private Long ufBirthday = null;
    @Builder.Default
    private String ufActualZip = "";
    @Builder.Default
    private String ufActualCountry = "";
    @Builder.Default
    private String ufActualRegion = "";
    @Builder.Default
    private String ufActualZone = "";
    @Builder.Default
    private String ufActualCity = "";
    @Builder.Default
    private String ufActualStreet = "";
    @Builder.Default
    private String ufActualBuilding = "";
    @Builder.Default
    private String ufActualBuildSect = "";
    @Builder.Default
    private String ufActualApartment = "";
    @Builder.Default
    private String ufRegisteredZip = "";
    @Builder.Default
    private String ufRegisteredCountry = "";
    @Builder.Default
    private String ufRegisteredRegion = "";
    @Builder.Default
    private String ufRegisteredZone = "";
    @Builder.Default
    private String ufRegisteredCity = "";
    @Builder.Default
    private String ufRegisteredStreet = "";
    @Builder.Default
    private String ufRegisteredBuilding = "";
    @Builder.Default
    private String ufRegisteredBuildSect = "";
    @Builder.Default
    private String ufRegisteredApartment = "";
    @Builder.Default
    private String ufPassportSeries = "";
    @Builder.Default
    private String ufPassportNumber = "";
    @Builder.Default
    private String ufPassportIssuedBy = "";
    @Builder.Default
    private List<String> ufMsisdn = null;
    @Builder.Default
    private List<String> ufEmail = null;
    @Builder.Default
    private String AdditionalInfo = "";
    private String inn;
    private String ogrn;
    private String fullName;
    @Builder.Default
    private String shortName = "";
    private String status;
    private String address;
    private List<String> okveds;
    @Builder.Default
    private String companyUrl = "";
    @Getter
    private Long companyRegisterDateTimestamp;
    private String companyZip;
    private String companyCountry;
    @Builder.Default
    private String companyRegion = "";
    @Builder.Default
    private String companyZone = "";
    private String companyCity;
    private String companyStreet;
    private String companyBuilding;
    @Builder.Default
    private String ufCompanyBuildSect = "";
    @Builder.Default
    private String ufCompanyApartment = "";
    private List<String> companyMsisdn;
    @Builder.Default
    private String companyEmail = "";
    private String companyRepresentativeName;
    private String companyRepresentativeMiddleName;
    private String companyRepresentativeLastName;
    @Builder.Default
    private String companyRepresentativeInn = "";
    @Builder.Default
    private String companyRepresentativePosition = "";
    @Builder.Default
    private String companyBankName = "";
    @Builder.Default
    private String companyBankAccount = "";
    @Builder.Default
    private String companyBankCorrAccount = "";
    @Builder.Default
    private String companyBankCardNumber = "";
    @Builder.Default
    private String companyBankRcbic = "";
    @Builder.Default
    private String companyBankKpp = "";

    public String getId() {
        return formatWithSingleQuotes(id);
    }

    public String getCompanyRegisterIp() {
        return formatWithSingleQuotes(companyRegisterIp);
    }

    public String getCompanyContractNumber() {
        return formatWithSingleQuotes(companyContractNumber);
    }

    public String getUfName() {
        return formatWithSingleQuotes(ufName);
    }

    public String getUfLastName() {
        return formatWithSingleQuotes(ufLastName);
    }

    public String getUfMiddleName() {
        return formatWithSingleQuotes(ufMiddleName);
    }

    public String getUfActualZip() {
        return formatWithSingleQuotes(ufActualZip);
    }

    public String getUfActualCountry() {
        return formatWithSingleQuotes(ufActualCountry);
    }

    public String getUfActualRegion() {
        return formatWithSingleQuotes(ufActualRegion);
    }

    public String getUfActualZone() {
        return formatWithSingleQuotes(ufActualZone);
    }

    public String getUfActualCity() {
        return formatWithSingleQuotes(ufActualCity);
    }

    public String getUfActualStreet() {
        return formatWithSingleQuotes(ufActualStreet);
    }

    public String getUfActualBuilding() {
        return formatWithSingleQuotes(ufActualBuilding);
    }

    public String getUfActualBuildSect() {
        return formatWithSingleQuotes(ufActualBuildSect);
    }

    public String getUfActualApartment() {
        return formatWithSingleQuotes(ufActualApartment);
    }

    public String getUfRegisteredZip() {
        return formatWithSingleQuotes(ufRegisteredZip);
    }

    public String getUfRegisteredCountry() {
        return formatWithSingleQuotes(ufRegisteredCountry);
    }

    public String getUfRegisteredRegion() {
        return formatWithSingleQuotes(ufRegisteredRegion);
    }

    public String getUfRegisteredZone() {
        return formatWithSingleQuotes(ufRegisteredZone);
    }

    public String getUfRegisteredCity() {
        return formatWithSingleQuotes(ufRegisteredCity);
    }

    public String getUfRegisteredStreet() {
        return formatWithSingleQuotes(ufRegisteredStreet);
    }

    public String getUfRegisteredBuilding() {
        return formatWithSingleQuotes(ufRegisteredBuilding);
    }

    public String getUfRegisteredBuildSect() {
        return formatWithSingleQuotes(ufRegisteredBuildSect);
    }

    public String getUfRegisteredApartment() {
        return formatWithSingleQuotes(ufRegisteredApartment);
    }

    public String getUfPassportSeries() {
        return formatWithSingleQuotes(ufPassportSeries);
    }

    public String getUfPassportNumber() {
        return formatWithSingleQuotes(ufPassportNumber);
    }

    public String getUfPassportIssuedBy() {
        return formatWithSingleQuotes(ufPassportIssuedBy);
    }

    public String getUfMsisdn() {
        return formatListWithSingleQuotes(ufMsisdn);
    }

    public String getUfEmail() {
        return formatListWithSingleQuotes(ufEmail);
    }

    public String getAdditionalInfo() {
        return formatWithSingleQuotes(AdditionalInfo);
    }

    public String getInn() {
        return formatWithSingleQuotes(inn);
    }

    public String getOgrn() {
        return formatWithSingleQuotes(ogrn);
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getStatus() {
        return formatWithSingleQuotes(status);
    }

    public String getAddress() {
        return formatWithSingleQuotes(address);
    }

    public String getOkveds() {
        return formatListWithSingleQuotes(okveds);
    }

    public String getCompanyUrl() {
        return formatWithSingleQuotes(companyUrl);
    }

    public String getCompanyZip() {
        return formatWithSingleQuotes(companyZip);
    }

    public String getCompanyCountry() {
        return formatWithSingleQuotes(companyCountry);
    }

    public String getCompanyRegion() {
        return formatWithSingleQuotes(companyRegion);
    }

    public String getCompanyZone() {
        return formatWithSingleQuotes(companyZone);
    }

    public String getCompanyCity() {
        return formatWithSingleQuotes(companyCity);
    }

    public String getCompanyStreet() {
        return formatWithSingleQuotes(companyStreet);
    }

    public String getCompanyBuilding() {
        return formatWithSingleQuotes(companyBuilding);
    }

    public String getUfCompanyBuildSect() {
        return formatWithSingleQuotes(ufCompanyBuildSect);
    }

    public String getUfCompanyApartment() {
        return formatWithSingleQuotes(ufCompanyApartment);
    }

    public String getCompanyMsisdn() {
        return formatListWithSingleQuotes(companyMsisdn);
    }

    public String getCompanyEmail() {
        return formatWithSingleQuotes(companyEmail);
    }

    public String getCompanyRepresentativeName() {
        return formatWithSingleQuotes(companyRepresentativeName);
    }

    public String getCompanyRepresentativeMiddleName() {
        return formatWithSingleQuotes(companyRepresentativeMiddleName);
    }

    public String getCompanyRepresentativeLastName() {
        return formatWithSingleQuotes(companyRepresentativeLastName);
    }

    public String getCompanyRepresentativeInn() {
        return formatWithSingleQuotes(companyRepresentativeInn);
    }

    public String getCompanyRepresentativePosition() {
        return formatWithSingleQuotes(companyRepresentativePosition);
    }

    public String getCompanyBankName() {
        return formatWithSingleQuotes(companyBankName);
    }

    public String getCompanyBankAccount() {
        return formatWithSingleQuotes(companyBankAccount);
    }

    public String getCompanyBankCorrAccount() {
        return formatWithSingleQuotes(companyBankCorrAccount);
    }

    public String getCompanyBankCardNumber() {
        return formatWithSingleQuotes(companyBankCardNumber);
    }

    public String getCompanyBankRcbic() {
        return formatWithSingleQuotes(companyBankRcbic);
    }

    public String getCompanyBankKpp() {
        return formatWithSingleQuotes(companyBankKpp);
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
