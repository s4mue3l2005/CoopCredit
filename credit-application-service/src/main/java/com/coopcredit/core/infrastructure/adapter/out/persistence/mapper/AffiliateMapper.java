package com.coopcredit.core.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.AffiliateEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AffiliateMapper {
    Affiliate toDomain(AffiliateEntity entity);
    AffiliateEntity toEntity(Affiliate domain);
}
