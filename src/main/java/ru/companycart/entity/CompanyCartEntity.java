package ru.companycart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "company_cart")
@Getter
@NoArgsConstructor
@Setter
public class CompanyCartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String inn;

    private String kpp;

    private String ogrn;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "short_name")
    private String shortName;

    private String status;

    private String address;

    @Column(name = "main_okved")
    private String mainOkved;

    @Column(name = "additional_okveds")
    private List<String> additionalOkveds;

    @Column(name = "company_url")
    private String companyUrl;

    @Column(name = "company_register_timestamp")
    private String companyRegisterDateTimestamp;

    @Column(name = "company_zip")
    private String companyZip;

    @Column(name = "company_country")
    private String companyCountry;

    @Column(name = "company_region")
    private String companyRegion;

    @Column(name = "company_city")
    private String companyCity;

    @Column(name = "company_street")
    private String companyStreet;

    @Column(name = "company_building")
    private String companyBuilding;

    @Column(name = "company_msisdn")
    private List<String> companyMsisdn;

    @Column(name = "company_email")
    private String companyEmail;

    // Представитель компании
    @Column(name = "company_representative_name")
    private String companyRepresentativeName;

    @Column(name = "company_representative_middle_name")
    private String companyRepresentativeMiddleName;

    @Column(name = "company_representative_last_name")
    private String companyRepresentativeLastName;

    @Column(name = "company_representative_inn")
    private String companyRepresentativeInn;

    @Column(name = "company_representative_position")
    private String companyRepresentativePosition;


}
