package ru.companycart.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.companycart.entity.CompanyCartEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyCartRepository extends JpaRepository<CompanyCartEntity, UUID> {

    Optional<CompanyCartEntity> findByInn(String inn);

    void deleteByInn(String inn);

    CompanyCartEntity getCompanyCartEntitiesByInnContains(String inn);

    CompanyCartEntity getCompanyCartEntitiesByCompanyId(Long companyId);

    List<CompanyCartEntity> findByUpdateDateBefore(Instant staleDate, PageRequest of);

    @Query(nativeQuery = true, value = """
            select * from company_cart
            where state = 'ACTUAL'
            order by last_sent_date asc nulls first,
                     created_date
            limit 1;
            """)
    CompanyCartEntity getOldestActualCompany();
}
