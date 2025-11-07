package ru.companycart.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "company_cart")
@Getter
@NoArgsConstructor
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

    @Column(name = "registration_date")
    private Instant registrationDate;

    @Column(name = "liquidation_date")
    private Instant liquidationDate;

    private String address;

    @Column(name = "main_okved")
    private String mainOkved;

    @Column(name = "additional_okveds")
    private List<String> additionalOkveds;

    // Поля для хостинг-провайдера
    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "user_kind")
    private Integer userKind;

    @Column(name = "register_ip")
    private String registerIP;

    @Column(name = "register_timestamp")
    private Long registerTimestamp;

    @Column(name = "unregister_timestamp")
    private Long unregisterTimestamp;

    @Column(name = "contract_number")
    private String contract;

    @Column(name = "company_small_name")
    private String companySmallName;

    @Column(name = "company_full_name")
    private String companyFullName;

    @Column(name = "company_grn")
    private String companyGrn;

    @Column(name = "company_url")
    private String companyUrl;

    @Column(name = "company_register_timestamp")
    private Long companyRegisterDateTimestamp;

    @Column(name = "company_egrul")
    private String companyEgrul;

    @Column(name = "company_egrip")
    private String companyEgrip;

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
    private String companyMsisdn;

    @Column(name = "company_email")
    private String companyEmail;

    // Представитель компании
    @Column(name = "company_representative_name")
    private String companyRepresentativeName;

    @Column(name = "company_representative_last_name")
    private String companyRepresentativeLastName;

    @Column(name = "company_representative_middle_name")
    private String companyRepresentativeMiddleName;

    @Column(name = "company_representative_inn")
    private String companyRepresentativeInn;

    @Column(name = "company_representative_position")
    private String companyRepresentativePosition;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    //Конструктор заглушка
    @Builder
    public CompanyCartEntity(String inn) {
        this.inn = inn;
    }



}
