package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.port.in.UpdateAffiliateUseCase;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AffiliateUpdateService implements UpdateAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepositoryPort;

    @Override
    @Transactional
    public Affiliate update(Long affiliateId, String name, String email, String address, String phone, BigDecimal salary) {
        // Find affiliate
        Affiliate affiliate = affiliateRepositoryPort.findById(affiliateId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found with id: " + affiliateId));

        // Validate salary if provided
        if (salary != null && salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than 0");
        }

        // Update fields (only non-null values)
        if (name != null && !name.isBlank()) {
            affiliate.setName(name);
        }
        if (email != null && !email.isBlank()) {
            affiliate.setEmail(email);
        }
        if (salary != null) {
            affiliate.setSalary(salary);
        }
        // Note: address and phone are not in the current Affiliate model
        // If needed, they should be added to the domain model first

        return affiliateRepositoryPort.save(affiliate);
    }
}

