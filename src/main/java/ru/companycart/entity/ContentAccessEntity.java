package ru.companycart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import ru.companycart.enums.ContentAccessState;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content_access")
@Getter
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ContentAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String url;

    private String description;

    private Instant accessDate;

    private String clientIp;

    @PositiveOrZero(message = "Допустимый диапазон значений [0 - 65535]")
    @Max(value = 65535, message = "Допустимый диапазон значений [0 - 65535]")
    private Integer clientPort;

    @PositiveOrZero(message = "Допустимый диапазон значений [0 - 65535]")
    @Max(value = 65535, message = "Допустимый диапазон значений [0 - 65535]")
    private Integer clientProtocol;

    private String serverIp;

    @PositiveOrZero(message = "Допустимый диапазон значений [0 - 65535]")
    @Max(value = 65535, message = "Допустимый диапазон значений [0 - 65535]")
    private Integer serverPort;

    @PositiveOrZero(message = "Допустимый диапазон значений [0 - 65535]")
    @Max(value = 65535, message = "Допустимый диапазон значений [0 - 65535]")
    private Integer serverProtocol;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private ContentAccessState state;

    private Instant sentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "company_id",
            referencedColumnName = "company_id"
    )
    private CompanyCartEntity companyCartEntity;

    @Builder
    public ContentAccessEntity(Integer serverProtocol,
                               Integer serverPort,
                               String serverIp,
                               Integer clientProtocol,
                               Integer clientPort,
                               String clientIp,
                               Instant accessDate,
                               String description,
                               String url
    ) {
        this.serverProtocol = serverProtocol;
        this.serverPort = serverPort;
        this.serverIp = serverIp;
        this.clientProtocol = clientProtocol;
        this.clientPort = clientPort;
        this.clientIp = clientIp;
        this.accessDate = accessDate;
        this.description = description;
        this.url = url;
        this.state = ContentAccessState.NEW;
    }

    public void markAsSent() {
        this.sentDate = Instant.now();
        this.state = ContentAccessState.SENT;
    }



    @Override
    public String toString() {
        return "ContentAccessEntity{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", accessDate=" + accessDate +
                ", clientIp='" + clientIp + '\'' +
                ", clientPort=" + clientPort +
                ", clientProtocol=" + clientProtocol +
                ", serverIp='" + serverIp + '\'' +
                ", serverPort=" + serverPort +
                ", serverProtocol=" + serverProtocol +
                ", sentDate=" + sentDate +
                '}';
    }
}
