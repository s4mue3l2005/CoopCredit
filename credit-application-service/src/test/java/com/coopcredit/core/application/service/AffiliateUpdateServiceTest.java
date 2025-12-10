package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AffiliateUpdateService Unit Tests")
class AffiliateUpdateServiceTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @InjectMocks
    private AffiliateUpdateService affiliateUpdateService;

    @Test
    @DisplayName("Should update affiliate successfully with all fields")
    void shouldUpdateAffiliateWithAllFields() {
        // Arrange
        Long affiliateId = 1L;
        Affiliate existingAffiliate = Affiliate.builder()
                .id(affiliateId)
                .name("Old Name")
                .email("old@example.com")
                .salary(new BigDecimal("5000.00"))
                .document("DOC123")
                .active(true)
                .build();

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(existingAffiliate));
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Affiliate result = affiliateUpdateService.update(
                affiliateId,
                "New Name",
                "new@example.com",
                "New Address",
                "123456789",
                new BigDecimal("6000.00")
        );

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(new BigDecimal("6000.00"), result.getSalary());

        verify(affiliateRepositoryPort).findById(affiliateId);
        verify(affiliateRepositoryPort).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should perform partial update with only name")
    void shouldPerformPartialUpdateWithOnlyName() {
        // Arrange
        Long affiliateId = 1L;
        Affiliate existingAffiliate = Affiliate.builder()
                .id(affiliateId)
                .name("Old Name")
                .email("old@example.com")
                .salary(new BigDecimal("5000.00"))
                .document("DOC123")
                .active(true)
                .build();

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(existingAffiliate));
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Affiliate result = affiliateUpdateService.update(
                affiliateId,
                "New Name",
                null,
                null,
                null,
                null
        );

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals("old@example.com", result.getEmail()); // Unchanged
        assertEquals(new BigDecimal("5000.00"), result.getSalary()); // Unchanged
    }

    @Test
    @DisplayName("Should perform partial update with only salary")
    void shouldPerformPartialUpdateWithOnlySalary() {
        // Arrange
        Long affiliateId = 1L;
        Affiliate existingAffiliate = Affiliate.builder()
                .id(affiliateId)
                .name("Old Name")
                .email("old@example.com")
                .salary(new BigDecimal("5000.00"))
                .document("DOC123")
                .active(true)
                .build();

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(existingAffiliate));
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Affiliate result = affiliateUpdateService.update(
                affiliateId,
                null,
                null,
                null,
                null,
                new BigDecimal("7000.00")
        );

        // Assert
        assertEquals("Old Name", result.getName()); // Unchanged
        assertEquals(new BigDecimal("7000.00"), result.getSalary());
    }

    @Test
    @DisplayName("Should throw exception when affiliate not found")
    void shouldThrowExceptionWhenAffiliateNotFound() {
        // Arrange
        Long affiliateId = 999L;
        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> affiliateUpdateService.update(affiliateId, "Name", null, null, null, null)
        );

        assertEquals("Affiliate not found with id: " + affiliateId, exception.getMessage());
        verify(affiliateRepositoryPort).findById(affiliateId);
        verify(affiliateRepositoryPort, never()).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should throw exception when salary is zero")
    void shouldThrowExceptionWhenSalaryIsZero() {
        // Arrange
        Long affiliateId = 1L;
        Affiliate existingAffiliate = Affiliate.builder()
                .id(affiliateId)
                .name("Name")
                .email("email@example.com")
                .salary(new BigDecimal("5000.00"))
                .document("DOC123")
                .active(true)
                .build();

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(existingAffiliate));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> affiliateUpdateService.update(affiliateId, null, null, null, null, BigDecimal.ZERO)
        );

        assertEquals("Salary must be greater than 0", exception.getMessage());
        verify(affiliateRepositoryPort).findById(affiliateId);
        verify(affiliateRepositoryPort, never()).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should throw exception when salary is negative")
    void shouldThrowExceptionWhenSalaryIsNegative() {
        // Arrange
        Long affiliateId = 1L;
        Affiliate existingAffiliate = Affiliate.builder()
                .id(affiliateId)
                .name("Name")
                .email("email@example.com")
                .salary(new BigDecimal("5000.00"))
                .document("DOC123")
                .active(true)
                .build();

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(existingAffiliate));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> affiliateUpdateService.update(affiliateId, null, null, null, null, new BigDecimal("-1000.00"))
        );

        assertEquals("Salary must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should ignore blank strings")
    void shouldIgnoreBlankStrings() {
        // Arrange
        Long affiliateId = 1L;
        Affiliate existingAffiliate = Affiliate.builder()
                .id(affiliateId)
                .name("Old Name")
                .email("old@example.com")
                .salary(new BigDecimal("5000.00"))
                .document("DOC123")
                .active(true)
                .build();

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(existingAffiliate));
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Affiliate result = affiliateUpdateService.update(
                affiliateId,
                "   ", // Blank string
                "", // Empty string
                null,
                null,
                null
        );

        // Assert
        assertEquals("Old Name", result.getName()); // Unchanged
        assertEquals("old@example.com", result.getEmail()); // Unchanged
    }
}

