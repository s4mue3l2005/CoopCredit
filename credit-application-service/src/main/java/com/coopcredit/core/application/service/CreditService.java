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

        // 2. Call Risk Service
        RiskEvaluationResult riskResult = riskServicePort.evaluateRisk(affiliate.getDocument(), amount, term);

        // 3. Evaluate Policies
        CreditStatus status = CreditStatus.APPROVED;
        String rationale = riskResult.getRationale();

        // Policy: High Risk -> Rejected
        if ("HIGH".equals(riskResult.getRiskLevel())) {
            status = CreditStatus.REJECTED;
            rationale = "Rejected due to High Risk. " + rationale;
        }

        // Policy: Max amount vs Salary (Example: amount cannot exceed 10x salary)
        // Note: Assuming monthly salary, simple rule.
        if (amount.compareTo(affiliate.getSalary().multiply(new BigDecimal("10"))) > 0) {
            status = CreditStatus.REJECTED;
            rationale = "Amount exceeds 10x salary limit.";
        }

        // 4. Create and Save Credit
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
}
