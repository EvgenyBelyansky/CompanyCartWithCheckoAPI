package ru.companycart.dto.checko;

import lombok.Data;

@Data
public class AddressComponents {
    private String postalCode;
    private String country;
    private String region;
    private String city;
    private String district;
    private String street;
    private String house;
    private String building;
    private String apartment;
    private String fullHouseNumber;
    private String fullAddress;

    public AddressComponents() {
        this.country = "Россия";
    }
}