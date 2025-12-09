package com.coopcredit.core.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ApplyForCreditRequest {
    @NotNull
    private Long affiliateId;
    
    @DecimalMin("1.00")
    private BigDecimal amount;
    
    @Min(1)
    private Integer term;
}
