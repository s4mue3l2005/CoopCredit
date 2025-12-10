package com.coopcredit.core.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterUserRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Pattern(regexp = ".{6,}", message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|ANALYST|AFFILIATE", message = "Role must be ADMIN, ANALYST, or AFFILIATE")
    private String role;
}

