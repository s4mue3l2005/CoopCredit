package com.coopcredit.core;

import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.coopcredit.core.infrastructure.adapter.out.persistence.repository.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("Auth Integration Tests")
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUserSuccessfully() throws Exception {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest();
        request.username = "testuser";
        request.password = "password123";
        request.role = "ANALYST";

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ANALYST"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.password").doesNotExist()); // Password should not be in response

        // Verify database
        Optional<UserEntity> savedUser = userRepository.findByUsername("testuser");
        assertTrue(savedUser.isPresent(), "User should be saved in database");
        assertEquals("ANALYST", savedUser.get().getRole());
        assertTrue(passwordEncoder.matches("password123", savedUser.get().getPassword()),
                "Password should be encrypted");
    }

    @Test
    @DisplayName("Should return 400 when username already exists")
    void shouldReturn400WhenUsernameExists() throws Exception {
        // Arrange - Create user first
        UserEntity existingUser = UserEntity.builder()
                .username("existinguser")
                .password(passwordEncoder.encode("password123"))
                .role("ADMIN")
                .active(true)
                .build();
        userRepository.save(existingUser);

        RegisterUserRequest request = new RegisterUserRequest();
        request.username = "existinguser";
        request.password = "password123";
        request.role = "ANALYST";

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when role is invalid")
    void shouldReturn400WhenRoleIsInvalid() throws Exception {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest();
        request.username = "newuser";
        request.password = "password123";
        request.role = "INVALID_ROLE";

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when password is too short")
    void shouldReturn400WhenPasswordTooShort() throws Exception {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest();
        request.username = "newuser";
        request.password = "12345"; // Less than 6 characters
        request.role = "ANALYST";

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept ADMIN role")
    void shouldAcceptAdminRole() throws Exception {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest();
        request.username = "adminuser";
        request.password = "password123";
        request.role = "ADMIN";

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("Should accept AFFILIATE role")
    void shouldAcceptAffiliateRole() throws Exception {
        // Arrange
        RegisterUserRequest request = new RegisterUserRequest();
        request.username = "affiliateuser";
        request.password = "password123";
        request.role = "AFFILIATE";

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("AFFILIATE"));
    }

    static class RegisterUserRequest {
        public String username;
        public String password;
        public String role;
    }
}

