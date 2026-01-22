package ru.companycart.dto.company;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CompanyUpdateDto {

    private String status;
    private String address;
    private String mainOkved;
    private List<String> additionalOkveds;
    private String companyUrl;
    private String companyRegisterDateTimestamp;
    private String companyZip;
    private String companyCountry;
    private String companyRegion;
    private String companyCity;
    private String companyStreet;
    private String companyBuilding;
    private List<String> companyMsisdn;
    private String companyEmail;
    private String companyRepresentativeName;
    private String companyRepresentativeMiddleName;
    private String companyRepresentativeLastName;
    private String companyRepresentativeInn;
    private String companyRepresentativePosition;
}
