package com.coopcredit.core.application.service;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.model.CreditStatus;
import com.coopcredit.core.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.core.domain.port.out.CreditRepositoryPort;
import com.coopcredit.core.domain.port.out.RiskEvaluationResult;
import com.coopcredit.core.domain.port.out.RiskServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    void applyForCredit_WhenLowRisk_ShouldApprove() {
        Long affiliateId = 1L;
        Affiliate affiliate = new Affiliate();
        affiliate.setId(affiliateId);
        affiliate.setActive(true);
        affiliate.setSalary(new BigDecimal("5000"));
        affiliate.setDocument("DOC1");

        when(affiliateRepositoryPort.findById(affiliateId)).thenReturn(Optional.of(affiliate));
        
        RiskEvaluationResult lowRisk = RiskEvaluationResult.builder()
                .riskLevel("LOW")
                .score(800)
                .build();
        when(riskServicePort.evaluateRisk(any(), any(), any())).thenReturn(lowRisk);

        when(creditRepositoryPort.save(any(Credit.class))).thenAnswer(i -> {
            Credit c = i.getArgument(0);
            c.setId(100L);
            return c;
        });

        Credit result = creditService.apply(affiliateId, new BigDecimal("1000"), 12);

        assertNotNull(result);
        assertEquals(CreditStatus.APPROVED, result.getStatus());
        verify(creditRepositoryPort).save(any());
    }

    @Test
    void applyForCredit_WhenHighRisk_ShouldReject() {
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

        assertEquals(CreditStatus.REJECTED, result.getStatus());
        assertEquals("Rejected due to High Risk. Too risky", result.getRationale());
    }
}
