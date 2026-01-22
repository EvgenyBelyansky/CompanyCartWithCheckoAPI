package ru.companycart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.companycart.enums.OfferingState;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "offering")
@Getter
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
public class OfferingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @CreationTimestamp
    private Instant createdDate;

    @UpdateTimestamp
    private Instant updateDate;

    private Instant unregisterDate;

    private Instant lastSentDate;

    private String additionalInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private OfferingState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "company_id",
            referencedColumnName = "company_id"
    )
    private CompanyCartEntity companyCartEntity;

    @Builder
    public OfferingEntity(String name, String additionalInfo, CompanyCartEntity companyCart) {
        this.name = Objects.requireNonNull(name);
        this.additionalInfo = Objects.requireNonNull(additionalInfo);
        this.companyCartEntity = Objects.requireNonNull(companyCart);
        this.state = OfferingState.NEW;
    }

    public void markAsInProgress(Instant instant) {
        this.state = OfferingState.IN_PROGRESS;
        this.lastSentDate = instant;
    }

    @Override
    public String toString() {
        return "OfferingEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyCartId=" + (companyCartEntity != null ? companyCartEntity.getId() : "null") +
                ", createdDate=" + createdDate +
                ", updateDate=" + updateDate +
                ", unregisterDate=" + unregisterDate +
                '}';
    }
}