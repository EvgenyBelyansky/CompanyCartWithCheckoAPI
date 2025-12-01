package ru.companycart.dto;

import lombok.Data;
import ru.companycart.dto.checko.AddressComponents;
import ru.companycart.dto.checko.NameComponents;

import java.time.LocalDateTime;

@Data
public class CompanyDto {
    private String id;
    private String inn;
    private String kpp;
    private String ogrn;
    private String fullName;
    private String shortName;
    private String status;
    private LocalDateTime registerDate;
    private AddressComponents address;
    private OkvedDto okved;
    private ContactDto contact;
    private NameComponents manager;
    private LocalDateTime lastUpdate;
}
