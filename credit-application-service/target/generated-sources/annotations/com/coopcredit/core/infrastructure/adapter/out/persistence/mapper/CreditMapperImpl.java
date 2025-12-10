package com.coopcredit.core.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.CreditEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T21:14:16-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class CreditMapperImpl implements CreditMapper {

    @Autowired
    private AffiliateMapper affiliateMapper;

    @Override
    public Credit toDomain(CreditEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Credit.CreditBuilder credit = Credit.builder();

        credit.affiliate( affiliateMapper.toDomain( entity.getAffiliate() ) );
        credit.amount( entity.getAmount() );
        credit.id( entity.getId() );
        credit.rationale( entity.getRationale() );
        if ( entity.getStatus() != null ) {
            credit.status( Enum.valueOf( CreditStatus.class, entity.getStatus() ) );
        }
        credit.term( entity.getTerm() );

        return credit.build();
    }

    @Override
    public CreditEntity toEntity(Credit domain) {
        if ( domain == null ) {
            return null;
        }

        CreditEntity.CreditEntityBuilder creditEntity = CreditEntity.builder();

        creditEntity.affiliate( affiliateMapper.toEntity( domain.getAffiliate() ) );
        creditEntity.amount( domain.getAmount() );
        creditEntity.id( domain.getId() );
        creditEntity.rationale( domain.getRationale() );
        if ( domain.getStatus() != null ) {
            creditEntity.status( domain.getStatus().name() );
        }
        creditEntity.term( domain.getTerm() );

        return creditEntity.build();
    }
}
