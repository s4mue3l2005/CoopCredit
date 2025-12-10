package com.coopcredit.core.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateAffiliateRequest {
    
    private String name;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String address;
    
    private String phone;
    
    @DecimalMin(value = "0.01", message = "Salary must be greater than 0")
    private BigDecimal salary;
}

