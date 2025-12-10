package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.User;
import com.coopcredit.core.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final String ENCRYPTED_PASSWORD = "$2a$10$encryptedPasswordHash";

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String role = "ANALYST";

        when(userRepositoryPort.existsByUsername(username)).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register(username, password, role);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(ENCRYPTED_PASSWORD, result.getPassword());
        assertEquals("ANALYST", result.getRole());
        assertTrue(result.isActive());

        verify(passwordEncoder).encode(password);
        verify(userRepositoryPort).existsByUsername(username);
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Arrange
        String username = "existinguser";
        String password = "password123";
        String role = "ADMIN";

        when(userRepositoryPort.existsByUsername(username)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(username, password, role)
        );

        assertEquals("Username already exists: " + username, exception.getMessage());
        verify(userRepositoryPort).existsByUsername(username);
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when role is invalid")
    void shouldThrowExceptionWhenRoleIsInvalid() {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String invalidRole = "INVALID_ROLE";

        when(userRepositoryPort.existsByUsername(username)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(username, password, invalidRole)
        );

        assertEquals("Invalid role. Must be ADMIN, ANALYST, or AFFILIATE", exception.getMessage());
        verify(userRepositoryPort).existsByUsername(username);
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should accept ADMIN role")
    void shouldAcceptAdminRole() {
        // Arrange
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register("admin", "pass123", "ADMIN");

        // Assert
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    @DisplayName("Should accept ANALYST role")
    void shouldAcceptAnalystRole() {
        // Arrange
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register("analyst", "pass123", "ANALYST");

        // Assert
        assertEquals("ANALYST", result.getRole());
    }

    @Test
    @DisplayName("Should accept AFFILIATE role")
    void shouldAcceptAffiliateRole() {
        // Arrange
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register("affiliate", "pass123", "AFFILIATE");

        // Assert
        assertEquals("AFFILIATE", result.getRole());
    }

    @Test
    @DisplayName("Should normalize role to uppercase")
    void shouldNormalizeRoleToUppercase() {
        // Arrange
        when(userRepositoryPort.existsByUsername(anyString())).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register("user", "pass123", "analyst");

        // Assert
        assertEquals("ANALYST", result.getRole());
    }
}

