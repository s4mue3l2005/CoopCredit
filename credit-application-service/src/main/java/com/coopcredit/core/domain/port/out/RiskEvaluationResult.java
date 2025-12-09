package com.coopcredit.core.domain.port.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskEvaluationResult {
    private Integer score;
    private String riskLevel; // Keeping as String to decouple from specific Enum in external service if we want
    private String rationale;
}
