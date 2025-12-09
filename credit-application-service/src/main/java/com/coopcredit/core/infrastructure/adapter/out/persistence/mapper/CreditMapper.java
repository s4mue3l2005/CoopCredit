package com.coopcredit.core.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.CreditEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AffiliateMapper.class})
public interface CreditMapper {
    Credit toDomain(CreditEntity entity);
    CreditEntity toEntity(Credit domain);
}
