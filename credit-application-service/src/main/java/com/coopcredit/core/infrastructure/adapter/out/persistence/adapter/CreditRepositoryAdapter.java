package com.coopcredit.core.infrastructure.adapter.out.persistence.adapter;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.port.out.CreditRepositoryPort;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.CreditEntity;
import com.coopcredit.core.infrastructure.adapter.out.persistence.mapper.CreditMapper;
import com.coopcredit.core.infrastructure.adapter.out.persistence.repository.JpaCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreditRepositoryAdapter implements CreditRepositoryPort {

    private final JpaCreditRepository jpaRepository;
    private final CreditMapper mapper;

    @Override
    public Credit save(Credit credit) {
        CreditEntity entity = mapper.toEntity(credit);
        CreditEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Credit> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Credit> findByAffiliateId(Long affiliateId) {
        return jpaRepository.findByAffiliateId(affiliateId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Credit> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
