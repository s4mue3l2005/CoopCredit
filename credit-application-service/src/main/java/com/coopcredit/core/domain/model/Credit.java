package com.coopcredit.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    private Long id;
    private Affiliate affiliate;
    private BigDecimal amount;
    private Integer term;
    private CreditStatus status;
    private String rationale;
}
