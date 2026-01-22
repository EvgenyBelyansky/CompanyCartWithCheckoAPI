package ru.companycart.dto;

import lombok.Data;

@Data
public class AddressComponents {
    private String postalCode;
    private String country = "Россия";
    private String region;
    private String city;
    private String district;
    private String street;
    private String house;
    private String building;
    private String apartment;
    private String fullHouseNumber;
    private String fullAddress;

}