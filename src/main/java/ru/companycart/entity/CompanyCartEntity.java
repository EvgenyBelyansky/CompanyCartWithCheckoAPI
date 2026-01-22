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

    private int companyKind = 4;

    private String companyRegisterIp;

    private LocalDate companyContractConclusionDate;

    private LocalDate companyContractTerminationDate;

    private String companyContractNumber;

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

    @CreationTimestamp
    private Instant createdDate;

    @UpdateTimestamp
    private Instant updateDate;

    private Instant lastSentDate;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "companyCartEntity")
    private List<OfferingEntity> offerings;

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
        this.companyUrl = companyUrl;
        this.companyRegisterDateTimestamp = companyRegisterDateTimestamp;
        this.companyZip = companyZip;
        this.companyCountry = companyCountry;
        this.companyRegion = companyRegion;
        this.companyCity = companyCity;
        this.companyStreet = companyStreet;
        this.companyBuilding = companyBuilding;
        this.companyMsisdn = companyMsisdn;
        this.companyEmail = companyEmail;
        this.companyRepresentativeName = companyRepresentativeName;
        this.companyRepresentativeMiddleName = companyRepresentativeMiddleName;
        this.companyRepresentativeLastName = companyRepresentativeLastName;
        this.companyRepresentativeInn = companyRepresentativeInn;
        this.companyRepresentativePosition = companyRepresentativePosition;
        this.state = CompanyUpdateState.ACTUAL;
    }

    public void updateCompanyEntity(CompanyUpdateDto dto) {
        this.status = dto.getStatus();
        this.address = Optional.ofNullable(dto.getAddress()).orElseThrow();
        this.mainOkved = Optional.ofNullable(dto.getMainOkved()).orElseThrow();
        this.additionalOkveds = dto.getAdditionalOkveds();
        this.companyUrl = dto.getCompanyUrl();
        this.companyRegisterDateTimestamp = dto.getCompanyRegisterDateTimestamp();
        this.companyZip = dto.getCompanyZip();
        this.companyCountry = dto.getCompanyCountry();
        this.companyRegion = dto.getCompanyRegion();
        this.companyCity = dto.getCompanyCity();
        this.companyStreet = dto.getCompanyStreet();
        this.companyBuilding = dto.getCompanyBuilding();
        this.companyMsisdn = dto.getCompanyMsisdn();
        this.companyEmail = dto.getCompanyEmail();
        this.companyRepresentativeName = dto.getCompanyRepresentativeName();
        this.companyRepresentativeMiddleName = dto.getCompanyRepresentativeMiddleName();
        this.companyRepresentativeLastName = dto.getCompanyRepresentativeLastName();
        this.companyRepresentativeInn = dto.getCompanyRepresentativeInn();
        this.companyRepresentativePosition = dto.getCompanyRepresentativePosition();
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
