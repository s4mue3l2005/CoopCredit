package com.coopcredit.core.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.CreditEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T14:50:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
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

        credit.id( entity.getId() );
        credit.affiliate( affiliateMapper.toDomain( entity.getAffiliate() ) );
        credit.amount( entity.getAmount() );
        credit.term( entity.getTerm() );
        if ( entity.getStatus() != null ) {
            credit.status( Enum.valueOf( CreditStatus.class, entity.getStatus() ) );
        }
        credit.rationale( entity.getRationale() );

        return credit.build();
    }

    @Override
    public CreditEntity toEntity(Credit domain) {
        if ( domain == null ) {
            return null;
        }

        CreditEntity.CreditEntityBuilder creditEntity = CreditEntity.builder();

        creditEntity.id( domain.getId() );
        creditEntity.affiliate( affiliateMapper.toEntity( domain.getAffiliate() ) );
        creditEntity.amount( domain.getAmount() );
        creditEntity.term( domain.getTerm() );
        if ( domain.getStatus() != null ) {
            creditEntity.status( domain.getStatus().name() );
        }
        creditEntity.rationale( domain.getRationale() );

        return creditEntity.build();
    }
}
