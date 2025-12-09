package com.coopcredit.core.domain.port.out;

import com.coopcredit.core.domain.model.Affiliate;
import java.math.BigDecimal;

public interface RiskServicePort {
    /**
     * @return boolean indicating if risk assessment allows credit.
     * We could also return a Risk object, but simplifying specifically for 'approved/rejected' logic as per requirements.
     * Actually, let's return a detailed result wrapper if needed, but boolean is simplest for now?
     * Requirement: "Evaluate Policies... Update State".
     * Risk Service returns Score, Risk Level and Rationale.
     * Let's create a domain value object for RiskResult if needed, or just return something simple.
     * Let's assume we need the Rationale too.
     */
     RiskEvaluationResult evaluateRisk(String documentId, BigDecimal amount, Integer term);
}
