package com.coopcredit.core.infrastructure.adapter.in.web;

import com.coopcredit.core.domain.model.User;
import com.coopcredit.core.domain.port.in.RegisterUserUseCase;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.AuthRequest;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.AuthResponse;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.RegisterUserRequest;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.UserResponse;
import com.coopcredit.core.infrastructure.config.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(user);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwtToken)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        User user = registerUserUseCase.register(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.isActive())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/auth/users/" + user.getId()))
                .body(response);
    }
}
