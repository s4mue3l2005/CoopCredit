package com.coopcredit.core.infrastructure.adapter.out.persistence.adapter;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.AffiliateEntity;
import com.coopcredit.core.infrastructure.adapter.out.persistence.mapper.AffiliateMapper;
import com.coopcredit.core.infrastructure.adapter.out.persistence.repository.JpaAffiliateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AffiliateRepositoryAdapter implements AffiliateRepositoryPort {

    private final JpaAffiliateRepository jpaRepository;
    private final AffiliateMapper mapper;

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = mapper.toEntity(affiliate);
        AffiliateEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByDocument(String document) {
        return jpaRepository.findByDocument(document).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }
}
