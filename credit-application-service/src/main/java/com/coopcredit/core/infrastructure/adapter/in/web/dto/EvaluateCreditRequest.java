package com.coopcredit.core.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EvaluateCreditRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "(?i)(APPROVED|REJECTED)", message = "Status must be APPROVED or REJECTED")
    private String status;

    @NotBlank(message = "Rationale is required")
    private String rationale;
}
