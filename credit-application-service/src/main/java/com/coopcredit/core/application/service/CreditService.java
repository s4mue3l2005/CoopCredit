package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.domain.port.in.ApplyForCreditUseCase;
import com.coopcredit.core.domain.port.in.GetCreditApplicationsUseCase;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.core.domain.port.out.CreditRepositoryPort;
import com.coopcredit.core.domain.port.out.RiskEvaluationResult;
import com.coopcredit.core.domain.port.out.RiskServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService implements ApplyForCreditUseCase, GetCreditApplicationsUseCase {

    private final CreditRepositoryPort creditRepositoryPort;
    private final AffiliateRepositoryPort affiliateRepositoryPort;
    private final RiskServicePort riskServicePort;

    @Override
    @Transactional
    public Credit apply(Long affiliateId, BigDecimal amount, Integer term) {
        // 1. Validate Affiliate
        Affiliate affiliate = affiliateRepositoryPort.findById(affiliateId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));

        if (!affiliate.isActive()) {
            throw new IllegalStateException("Affiliate is not active");
        }

        // 1.1 Check Seniority (Must be > 6 months)
        if (affiliate.getEnrollmentDate() != null &&
                java.time.LocalDate.now().minusMonths(6).isBefore(affiliate.getEnrollmentDate())) {
            throw new IllegalStateException("Affiliate seniority must be at least 6 months");
        }

        // 1.2 Check Max Amount (Amount <= 10 * Salary)
        BigDecimal maxAmount = affiliate.getSalary().multiply(BigDecimal.valueOf(10));
        if (amount.compareTo(maxAmount) > 0) {
            throw new IllegalStateException("Amount exceeds limit (10x salary). Max allowed: " + maxAmount);
        }

        // 1.3 Check Quota/Income Ratio (Installment <= 50% Salary)
        // Installment = Amount / Term
        BigDecimal installment = amount.divide(BigDecimal.valueOf(term), java.math.RoundingMode.CEILING);
        BigDecimal maxInstallment = affiliate.getSalary().multiply(BigDecimal.valueOf(0.5));

        if (installment.compareTo(maxInstallment) > 0) {
            throw new IllegalStateException("Installment exceeds 50% of salary. Estimated Installment: " + installment
                    + ", Max: " + maxInstallment);
        }

        // 2. Call Risk Service
        RiskEvaluationResult riskResult = riskServicePort.evaluateRisk(affiliate.getDocument(), amount, term);

        // 3. Initial evaluation - All credits start as PENDING for Analyst review
        // Business rule: Credits must be evaluated by an Analyst before
        // approval/rejection
        CreditStatus status = CreditStatus.PENDING;
        String rationale = "Credit application submitted. Awaiting analyst evaluation. Risk assessment: "
                + riskResult.getRationale();

        // 4. Create and Save Credit (status will be PENDING until Analyst evaluates)
        Credit credit = Credit.builder()
                .affiliate(affiliate)
                .amount(amount)
                .term(term)
                .status(status)
                .rationale(rationale)
                .build();

        return creditRepositoryPort.save(credit);
    }

    @Override
    public List<Credit> getByAffiliate(Long affiliateId) {
        return creditRepositoryPort.findByAffiliateId(affiliateId);
    }

    @Override
    public List<Credit> getAll() {
        return creditRepositoryPort.findAll();
    }

    @Override
    public List<Credit> getPending() {
        return creditRepositoryPort.findByStatus(CreditStatus.PENDING.name());
    }

}
