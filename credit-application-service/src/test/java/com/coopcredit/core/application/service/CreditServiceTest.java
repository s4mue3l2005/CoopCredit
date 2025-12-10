package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.core.domain.port.out.CreditRepositoryPort;
import com.coopcredit.core.domain.port.out.RiskEvaluationResult;
import com.coopcredit.core.domain.port.out.RiskServicePort;
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
@DisplayName("CreditService Unit Tests")
class CreditServiceTest {

    @Mock
    private CreditRepositoryPort creditRepositoryPort;
    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;
    @Mock
    private RiskServicePort riskServicePort;

    @InjectMocks
    private CreditService creditService;

    @Test
    @DisplayName("Should create credit with PENDING status initially")
    void applyForCredit_ShouldCreateWithPendingStatus() {
        Long affiliateId = 1L;
        Affiliate affiliate = new Affiliate();
        affiliate.setId(affiliateId);
        affiliate.setActive(true);
        affiliate.setSalary(new BigDecimal("5000"));
        affiliate.setEnrollmentDate(java.time.LocalDate.now().minusMonths(12));
        affiliate.setDocument("DOC1");

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        RiskEvaluationResult lowRisk = RiskEvaluationResult.builder()
                .riskLevel("LOW")
                .score(800)
                .rationale("Good credit score")
                .build();
        when(riskServicePort.evaluateRisk(any(), any(), any())).thenReturn(lowRisk);

        when(creditRepositoryPort.save(any(Credit.class))).thenAnswer(i -> {
            Credit c = i.getArgument(0);
            c.setId(100L);
            return c;
        });

        Credit result = creditService.apply(affiliateId, new BigDecimal("1000"), 12);

        assertNotNull(result);
        assertEquals(CreditStatus.PENDING, result.getStatus()); // Changed: Now PENDING initially
        assertTrue(result.getRationale().contains("Awaiting analyst evaluation"));
        verify(creditRepositoryPort).save(any());
    }

    @Test
    @DisplayName("Should create credit with PENDING status even when high risk (evaluation deferred)")
    void applyForCredit_WhenHighRisk_ShouldStillBePending() {
        Long affiliateId = 1L;
        Affiliate affiliate = new Affiliate();
        affiliate.setId(affiliateId);
        affiliate.setActive(true);
        affiliate.setSalary(new BigDecimal("5000"));
        affiliate.setDocument("DOC_HIGH");

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        RiskEvaluationResult highRisk = RiskEvaluationResult.builder()
                .riskLevel("HIGH")
                .score(400)
                .rationale("Too risky")
                .build();
        when(riskServicePort.evaluateRisk(any(), any(), any())).thenReturn(highRisk);

        when(creditRepositoryPort.save(any(Credit.class))).thenAnswer(i -> i.getArgument(0));

        Credit result = creditService.apply(affiliateId, new BigDecimal("1000"), 12);

        // Changed: All credits start as PENDING, even high risk ones
        assertEquals(CreditStatus.PENDING, result.getStatus());
        assertTrue(result.getRationale().contains("Awaiting analyst evaluation"));
    }

    @Test
    @DisplayName("Should throw exception when affiliate not found")
    void applyForCredit_WhenAffiliateNotFound_ShouldThrowException() {
        Long affiliateId = 999L;
        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> creditService.apply(affiliateId, new BigDecimal("1000"), 12));

        assertEquals("Affiliate not found", exception.getMessage());
        verify(creditRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when affiliate is inactive")
    void applyForCredit_WhenAffiliateInactive_ShouldThrowException() {
        Long affiliateId = 1L;
        Affiliate inactiveAffiliate = new Affiliate();
        inactiveAffiliate.setId(affiliateId);
        inactiveAffiliate.setActive(false);
        inactiveAffiliate.setSalary(new BigDecimal("5000"));
        inactiveAffiliate.setDocument("DOC1");

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(inactiveAffiliate));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> creditService.apply(affiliateId, new BigDecimal("1000"), 12));

        assertEquals("Affiliate is not active", exception.getMessage());
        verify(creditRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when affiliate seniority is less than 6 months")
    void applyForCredit_WhenSeniorityTooLow_ShouldThrowException() {
        Long affiliateId = 1L;
        Affiliate affiliate = new Affiliate();
        affiliate.setId(affiliateId);
        affiliate.setActive(true);
        affiliate.setSalary(new BigDecimal("5000"));
        affiliate.setEnrollmentDate(java.time.LocalDate.now().minusMonths(1)); // Only 1 month
        affiliate.setDocument("DOC_NEW");

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> creditService.apply(affiliateId, new BigDecimal("1000"), 12));

        assertEquals("Affiliate seniority must be at least 6 months", exception.getMessage());
        verify(creditRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when amount exceeds 10x salary")
    void applyForCredit_WhenAmountTooHigh_ShouldThrowException() {
        Long affiliateId = 1L;
        Affiliate affiliate = new Affiliate();
        affiliate.setId(affiliateId);
        affiliate.setActive(true);
        affiliate.setSalary(new BigDecimal("5000"));
        affiliate.setEnrollmentDate(java.time.LocalDate.now().minusMonths(12));
        affiliate.setDocument("DOC1");

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        // Max amount should be 50,000. Requesting 50,001.
        BigDecimal amount = new BigDecimal("50001");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> creditService.apply(affiliateId, amount, 12));

        assertTrue(exception.getMessage().contains("Amount exceeds limit"));
        verify(creditRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when installment exceeds 50% of salary")
    void applyForCredit_WhenInstallmentTooHigh_ShouldThrowException() {
        Long affiliateId = 1L;
        Affiliate affiliate = new Affiliate();
        affiliate.setId(affiliateId);
        affiliate.setActive(true);
        affiliate.setSalary(new BigDecimal("1000")); // Salary 1000
        affiliate.setEnrollmentDate(java.time.LocalDate.now().minusMonths(12));
        affiliate.setDocument("DOC1");

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(affiliate));

        // Amount 6000, Term 10 => Installment 600.
        // 50% of Salary = 500.
        // 600 > 500 => Fail.
        // Check Max Amount first: 6000 <= 10*1000 (10000). OK.
        
        BigDecimal amount = new BigDecimal("6000");
        Integer term = 10;

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> creditService.apply(affiliateId, amount, term)
        );

        assertTrue(exception.getMessage().contains("Installment exceeds 50% of salary"));
        verify(creditRepositoryPort, never()).save(any());
    }
}
