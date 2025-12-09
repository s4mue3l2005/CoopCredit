package com.coopcredit.core.domain.port.out;

import com.coopcredit.core.domain.model.Affiliate;
import java.util.Optional;

public interface AffiliateRepositoryPort {
    Affiliate save(Affiliate affiliate);
    Optional<Affiliate> findById(Long id);
    Optional<Affiliate> findByDocument(String document);
    boolean existsByDocument(String document);
}
