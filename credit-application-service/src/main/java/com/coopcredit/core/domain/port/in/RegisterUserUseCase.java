package com.coopcredit.core.domain.port.in;

import com.coopcredit.core.domain.model.User;

public interface RegisterUserUseCase {
    User register(String username, String password, String role);
}

