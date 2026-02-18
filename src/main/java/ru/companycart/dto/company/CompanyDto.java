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
    private String url;
    private String registerDateTimestamp;
    private String zip;
    private String country;
    private String region;
    private String city;
    private String street;
    private String building;
    private List<String> msisdn;
    private String email;
    private String representativeName;
    private String representativeMiddleName;
    private String representativeLastName;
    private String representativeInn;
    private String representativePosition;
}
