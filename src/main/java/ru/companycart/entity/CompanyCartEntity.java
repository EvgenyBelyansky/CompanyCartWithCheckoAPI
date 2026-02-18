package ru.companycart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.companycart.dto.company.CompanyUpdateDto;
import ru.companycart.enums.CompanyUpdateState;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "company_cart")
@Getter
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class CompanyCartEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_id", unique = true)
    private Long companyId;

    private int kind = 4;

    private String registerIp;

    private LocalDate contractConclusionDate;

    private LocalDate contractTerminationDate;

    private String contractNumber;

    @Column(name = "inn", unique = true, nullable = false)
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

    @Column(name = "url")
    private String url;

    @Column(name = "register_timestamp")
    private String registerDateTimestamp;

    @Column(name = "zip")
    private String zip;

    @Column(name = "country")
    private String country;

    @Column(name = "region")
    private String region;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "building")
    private String building;

    @Column(name = "msisdn")
    private List<String> msisdn;

    @Column(name = "email")
    private String email;

    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "representative_middle_name")
    private String representativeMiddleName;

    @Column(name = "representative_last_name")
    private String representativeLastName;

    @Column(name = "representative_inn")
    private String representativeInn;

    @Column(name = "representative_position")
    private String representativePosition;

    @CreationTimestamp
    private Instant createdDate;

    @UpdateTimestamp
    private Instant updateDate;

    private Instant lastSentDate;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "companyCartEntity")
    private List<OfferingEntity> offerings;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "companyCartEntity")
    private List<ContentAccessEntity> contentAccess;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private CompanyUpdateState state;

    @Builder
    public CompanyCartEntity(Long companyId,
                             String inn,
                             String kpp,
                             String ogrn,
                             String fullName,
                             String shortName,
                             String status,
                             String address,
                             String mainOkved,
                             List<String> additionalOkveds,
                             String companyUrl,
                             String companyRegisterDateTimestamp,
                             String companyZip,
                             String companyCountry,
                             String companyRegion,
                             String companyCity,
                             String companyStreet,
                             String companyBuilding,
                             List<String> companyMsisdn,
                             String companyEmail,
                             String companyRepresentativeName,
                             String companyRepresentativeMiddleName,
                             String companyRepresentativeLastName,
                             String companyRepresentativeInn,
                             String companyRepresentativePosition
    ) {
        this.companyId = companyId;
        this.inn = Optional.ofNullable(inn).orElseThrow();
        this.kpp = Optional.ofNullable(kpp).orElseThrow();
        this.ogrn = ogrn;
        this.fullName = Optional.ofNullable(fullName).orElseThrow();
        this.shortName = shortName;
        this.status = status;
        this.address = Optional.ofNullable(address).orElseThrow();
        this.mainOkved = Optional.ofNullable(mainOkved).orElseThrow();
        this.additionalOkveds = additionalOkveds;
        this.url = companyUrl;
        this.registerDateTimestamp = companyRegisterDateTimestamp;
        this.zip = companyZip;
        this.country = companyCountry;
        this.region = companyRegion;
        this.city = companyCity;
        this.street = companyStreet;
        this.building = companyBuilding;
        this.msisdn = companyMsisdn;
        this.email = companyEmail;
        this.representativeName = companyRepresentativeName;
        this.representativeMiddleName = companyRepresentativeMiddleName;
        this.representativeLastName = companyRepresentativeLastName;
        this.representativeInn = companyRepresentativeInn;
        this.representativePosition = companyRepresentativePosition;
        this.state = CompanyUpdateState.ACTUAL;
    }

    public void updateCompanyEntity(CompanyUpdateDto dto) {
        this.status = dto.getStatus();
        this.address = Optional.ofNullable(dto.getAddress()).orElseThrow();
        this.mainOkved = Optional.ofNullable(dto.getMainOkved()).orElseThrow();
        this.additionalOkveds = dto.getAdditionalOkveds();
        this.url = dto.getCompanyUrl();
        this.registerDateTimestamp = dto.getCompanyRegisterDateTimestamp();
        this.zip = dto.getCompanyZip();
        this.country = dto.getCompanyCountry();
        this.region = dto.getCompanyRegion();
        this.city = dto.getCompanyCity();
        this.street = dto.getCompanyStreet();
        this.building = dto.getCompanyBuilding();
        this.msisdn = dto.getCompanyMsisdn();
        this.email = dto.getCompanyEmail();
        this.representativeName = dto.getCompanyRepresentativeName();
        this.representativeMiddleName = dto.getCompanyRepresentativeMiddleName();
        this.representativeLastName = dto.getCompanyRepresentativeLastName();
        this.representativeInn = dto.getCompanyRepresentativeInn();
        this.representativePosition = dto.getCompanyRepresentativePosition();
        this.updateDate = Instant.now();
        this.state = CompanyUpdateState.ACTUAL;
    }

    private String formatWithSingleQuotes(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String escaped = value.replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }

    public void markAsSent() {
        this.state = CompanyUpdateState.SENT;
        this.lastSentDate = Instant.now();
    }
}
