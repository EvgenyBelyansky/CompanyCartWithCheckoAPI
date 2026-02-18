package ru.companycart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.companycart.entity.ContentAccessEntity;

import java.util.UUID;

public interface ContentAccessRepository extends JpaRepository<ContentAccessEntity, UUID> {
}
