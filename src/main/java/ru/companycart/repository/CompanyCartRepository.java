package ru.companycart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.companycart.entity.CompanyCartEntity;

import java.util.UUID;

public interface CompanyCartRepository extends JpaRepository<CompanyCartEntity, UUID> {

}
