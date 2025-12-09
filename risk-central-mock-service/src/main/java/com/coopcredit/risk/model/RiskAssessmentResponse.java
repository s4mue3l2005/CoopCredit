package com.coopcredit.risk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskAssessmentResponse {
    private Integer score;
    private RiskLevel riskLevel;
    private String rationale;
}
