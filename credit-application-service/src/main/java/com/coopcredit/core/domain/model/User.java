package com.coopcredit.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password; // Encrypted
    private String role; // ADMIN, ANALYST, AFFILIATE
    private boolean active;
}

