package com.coopcredit.core.infrastructure.adapter.out.persistence.repository;

import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.CreditEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaCreditRepository extends JpaRepository<CreditEntity, Long> {
    
    // Avoid N+1 problem fetching affiliate
    @EntityGraph(attributePaths = "affiliate")
    List<CreditEntity> findByAffiliateId(Long affiliateId);
}
