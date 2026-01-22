package ru.companycart.dto.company;

import lombok.Data;

import java.util.List;

@Data
public class CompanyDto {
    private String id;
    private String inn;
    private String kpp;
    private String ogrn;
    private String fullName;
    private String shortName;
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
