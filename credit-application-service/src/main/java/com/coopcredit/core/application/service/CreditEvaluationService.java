package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.domain.port.in.EvaluateCreditUseCase;
import com.coopcredit.core.domain.port.out.CreditRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditEvaluationService implements EvaluateCreditUseCase {

    private final CreditRepositoryPort creditRepositoryPort;

    @Override
    @Transactional
    public Credit evaluate(Long creditId, String status, String rationale) {
        // Find credit
        Credit credit = creditRepositoryPort.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Credit not found with id: " + creditId));

        // Verify credit is PENDING
        if (credit.getStatus() != CreditStatus.PENDING) {
            throw new IllegalStateException("Credit must be in PENDING status to be evaluated. Current status: " + credit.getStatus());
        }

        // Validate status
        CreditStatus newStatus;
        try {
            newStatus = CreditStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Must be APPROVED or REJECTED");
        }

        if (newStatus != CreditStatus.APPROVED && newStatus != CreditStatus.REJECTED) {
            throw new IllegalArgumentException("Status must be APPROVED or REJECTED");
        }

        // Update credit
        credit.setStatus(newStatus);
        credit.setRationale(rationale);

        return creditRepositoryPort.save(credit);
    }
}

