package com.coopcredit.core.infrastructure.adapter.out.persistence.repository;

import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaAffiliateRepository extends JpaRepository<AffiliateEntity, Long> {
    Optional<AffiliateEntity> findByDocument(String document);
    boolean existsByDocument(String document);
}
