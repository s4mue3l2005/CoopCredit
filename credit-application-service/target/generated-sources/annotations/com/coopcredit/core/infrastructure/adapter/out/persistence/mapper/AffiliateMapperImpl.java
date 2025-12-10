package com.coopcredit.core.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.AffiliateEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T21:14:15-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class AffiliateMapperImpl implements AffiliateMapper {

    @Override
    public Affiliate toDomain(AffiliateEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Affiliate.AffiliateBuilder affiliate = Affiliate.builder();

        affiliate.active( entity.isActive() );
        affiliate.document( entity.getDocument() );
        affiliate.email( entity.getEmail() );
        affiliate.enrollmentDate( entity.getEnrollmentDate() );
        affiliate.id( entity.getId() );
        affiliate.name( entity.getName() );
        affiliate.salary( entity.getSalary() );

        return affiliate.build();
    }

    @Override
    public AffiliateEntity toEntity(Affiliate domain) {
        if ( domain == null ) {
            return null;
        }

        AffiliateEntity.AffiliateEntityBuilder affiliateEntity = AffiliateEntity.builder();

        affiliateEntity.active( domain.isActive() );
        affiliateEntity.document( domain.getDocument() );
        affiliateEntity.email( domain.getEmail() );
        affiliateEntity.enrollmentDate( domain.getEnrollmentDate() );
        affiliateEntity.id( domain.getId() );
        affiliateEntity.name( domain.getName() );
        affiliateEntity.salary( domain.getSalary() );

        return affiliateEntity.build();
    }
}
