package ru.companycart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.companycart.entity.OfferingEntity;

import java.util.Collection;
import java.util.UUID;

public interface OfferingRepository extends JpaRepository<OfferingEntity, UUID> {
    OfferingEntity getCompanyOfferingEntityById(UUID id);

    Collection<OfferingEntity> getCompanyOfferingEntitiesByCompanyCartEntity_CompanyId(long id);

    @Query(nativeQuery = true, value = """
            select * from offerings
            where state = 'NEW'
            order by last_sent_date nulls first,
                     created_date
            limit 1;
            """)
    OfferingEntity getOldestActualCompany();
}
