package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.port.in.RegisterAffiliateUseCase;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AffiliateService implements RegisterAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepositoryPort;

    @Override
    @Transactional
    public Affiliate register(Affiliate affiliate) {
        if (affiliateRepositoryPort.existsByDocument(affiliate.getDocument())) {
            throw new IllegalArgumentException("Affiliate with document " + affiliate.getDocument() + " already exists");
        }
        affiliate.setActive(true);
        return affiliateRepositoryPort.save(affiliate);
    }
}
