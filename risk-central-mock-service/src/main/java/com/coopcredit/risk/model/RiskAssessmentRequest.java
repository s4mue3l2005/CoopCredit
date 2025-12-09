package com.coopcredit.risk.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RiskAssessmentRequest {
    private String documentId;
    private BigDecimal amount;
    private Integer term;
}
