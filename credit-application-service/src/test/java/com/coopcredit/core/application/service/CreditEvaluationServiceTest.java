package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.domain.port.out.CreditRepositoryPort;
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
@DisplayName("CreditEvaluationService Unit Tests")
class CreditEvaluationServiceTest {

    @Mock
    private CreditRepositoryPort creditRepositoryPort;

    @InjectMocks
    private CreditEvaluationService creditEvaluationService;

    @Test
    @DisplayName("Should evaluate credit as APPROVED successfully")
    void shouldEvaluateCreditAsApproved() {
        // Arrange
        Long creditId = 1L;
        Credit pendingCredit = Credit.builder()
                .id(creditId)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.PENDING)
                .rationale("Initial rationale")
                .build();

        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.of(pendingCredit));
        when(creditRepositoryPort.save(any(Credit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Credit result = creditEvaluationService.evaluate(creditId, "APPROVED", "Credit approved after review");

        // Assert
        assertNotNull(result);
        assertEquals(CreditStatus.APPROVED, result.getStatus());
        assertEquals("Credit approved after review", result.getRationale());

        verify(creditRepositoryPort).findById(creditId);
        verify(creditRepositoryPort).save(any(Credit.class));
    }

    @Test
    @DisplayName("Should evaluate credit as REJECTED successfully")
    void shouldEvaluateCreditAsRejected() {
        // Arrange
        Long creditId = 1L;
        Credit pendingCredit = Credit.builder()
                .id(creditId)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.PENDING)
                .rationale("Initial rationale")
                .build();

        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.of(pendingCredit));
        when(creditRepositoryPort.save(any(Credit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Credit result = creditEvaluationService.evaluate(creditId, "REJECTED", "Credit rejected due to insufficient income");

        // Assert
        assertEquals(CreditStatus.REJECTED, result.getStatus());
        assertEquals("Credit rejected due to insufficient income", result.getRationale());
    }

    @Test
    @DisplayName("Should throw exception when credit not found")
    void shouldThrowExceptionWhenCreditNotFound() {
        // Arrange
        Long creditId = 999L;
        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditEvaluationService.evaluate(creditId, "APPROVED", "Rationale")
        );

        assertEquals("Credit not found with id: " + creditId, exception.getMessage());
        verify(creditRepositoryPort).findById(creditId);
        verify(creditRepositoryPort, never()).save(any(Credit.class));
    }

    @Test
    @DisplayName("Should throw exception when credit is not PENDING")
    void shouldThrowExceptionWhenCreditIsNotPending() {
        // Arrange
        Long creditId = 1L;
        Credit approvedCredit = Credit.builder()
                .id(creditId)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.APPROVED)
                .rationale("Already approved")
                .build();

        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.of(approvedCredit));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> creditEvaluationService.evaluate(creditId, "APPROVED", "Rationale")
        );

        assertTrue(exception.getMessage().contains("Credit must be in PENDING status"));
        assertTrue(exception.getMessage().contains("APPROVED"));
        verify(creditRepositoryPort).findById(creditId);
        verify(creditRepositoryPort, never()).save(any(Credit.class));
    }

    @Test
    @DisplayName("Should throw exception when credit is REJECTED")
    void shouldThrowExceptionWhenCreditIsRejected() {
        // Arrange
        Long creditId = 1L;
        Credit rejectedCredit = Credit.builder()
                .id(creditId)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.REJECTED)
                .rationale("Already rejected")
                .build();

        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.of(rejectedCredit));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> creditEvaluationService.evaluate(creditId, "APPROVED", "Rationale")
        );

        assertTrue(exception.getMessage().contains("PENDING status"));
        verify(creditRepositoryPort, never()).save(any(Credit.class));
    }

    @Test
    @DisplayName("Should throw exception when status is invalid")
    void shouldThrowExceptionWhenStatusIsInvalid() {
        // Arrange
        Long creditId = 1L;
        Credit pendingCredit = Credit.builder()
                .id(creditId)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.PENDING)
                .build();

        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.of(pendingCredit));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditEvaluationService.evaluate(creditId, "INVALID_STATUS", "Rationale")
        );

        assertTrue(exception.getMessage().contains("Invalid status"));
        verify(creditRepositoryPort, never()).save(any(Credit.class));
    }

    @Test
    @DisplayName("Should throw exception when status is PENDING (cannot evaluate to PENDING)")
    void shouldThrowExceptionWhenStatusIsPending() {
        // Arrange
        Long creditId = 1L;
        Credit pendingCredit = Credit.builder()
                .id(creditId)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.PENDING)
                .build();

        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.of(pendingCredit));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditEvaluationService.evaluate(creditId, "PENDING", "Rationale")
        );

        assertTrue(exception.getMessage().contains("Status must be APPROVED or REJECTED"));
    }

    @Test
    @DisplayName("Should normalize status to uppercase")
    void shouldNormalizeStatusToUppercase() {
        // Arrange
        Long creditId = 1L;
        Credit pendingCredit = Credit.builder()
                .id(creditId)
                .amount(new BigDecimal("10000.00"))
                .term(12)
                .status(CreditStatus.PENDING)
                .build();

        when(creditRepositoryPort.findById(creditId)).thenReturn(Optional.of(pendingCredit));
        when(creditRepositoryPort.save(any(Credit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Credit result = creditEvaluationService.evaluate(creditId, "approved", "Rationale");

        // Assert
        assertEquals(CreditStatus.APPROVED, result.getStatus());
    }
}

