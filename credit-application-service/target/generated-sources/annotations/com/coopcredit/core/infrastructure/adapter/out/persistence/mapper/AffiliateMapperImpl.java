package com.coopcredit.core.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.AffiliateEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:45:11-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class AffiliateMapperImpl implements AffiliateMapper {

    @Override
    public Affiliate toDomain(AffiliateEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Affiliate.AffiliateBuilder affiliate = Affiliate.builder();

        affiliate.id( entity.getId() );
        affiliate.name( entity.getName() );
        affiliate.email( entity.getEmail() );
        affiliate.document( entity.getDocument() );
        affiliate.salary( entity.getSalary() );
        affiliate.enrollmentDate( entity.getEnrollmentDate() );
        affiliate.active( entity.isActive() );

        return affiliate.build();
    }

    @Override
    public AffiliateEntity toEntity(Affiliate domain) {
        if ( domain == null ) {
            return null;
        }

        AffiliateEntity.AffiliateEntityBuilder affiliateEntity = AffiliateEntity.builder();

        affiliateEntity.id( domain.getId() );
        affiliateEntity.name( domain.getName() );
        affiliateEntity.email( domain.getEmail() );
        affiliateEntity.document( domain.getDocument() );
        affiliateEntity.salary( domain.getSalary() );
        affiliateEntity.enrollmentDate( domain.getEnrollmentDate() );
        affiliateEntity.active( domain.isActive() );

        return affiliateEntity.build();
    }
}
