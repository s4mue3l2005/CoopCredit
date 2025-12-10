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
public class Affiliate {
    private Long id;
    private String name;
    private String email;
    private String document;
    private BigDecimal salary;
    private java.time.LocalDate enrollmentDate;
    private boolean active;
}
