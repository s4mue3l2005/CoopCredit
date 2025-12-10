package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.User;
import com.coopcredit.core.domain.port.in.RegisterUserUseCase;
import com.coopcredit.core.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(String username, String password, String role) {
        // Validate username uniqueness
        if (userRepositoryPort.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        // Validate role
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role. Must be ADMIN, ANALYST, or AFFILIATE");
        }

        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(password);

        // Create and save user
        User user = User.builder()
                .username(username)
                .password(encryptedPassword)
                .role(role.toUpperCase())
                .active(true)
                .build();

        return userRepositoryPort.save(user);
    }

    private boolean isValidRole(String role) {
        return "ADMIN".equalsIgnoreCase(role) 
            || "ANALYST".equalsIgnoreCase(role) 
            || "AFFILIATE".equalsIgnoreCase(role);
    }
}

