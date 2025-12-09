package com.coopcredit.core;

import com.coopcredit.core.domain.port.out.RiskEvaluationResult;
import com.coopcredit.core.domain.port.out.RiskServicePort;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.ApplyForCreditRequest;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.RegisterAffiliateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CreditIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RiskServicePort riskServicePort;

    @Test
    @WithMockUser  // Bypasses JWT filter logic for simplicity in integration test of business logic
    void testRegisterAffiliateAndApplyForCredit() throws Exception {
        // 1. Register Affiliate
        RegisterAffiliateRequest affiliateRequest = new RegisterAffiliateRequest();
        affiliateRequest.setName("John Doe");
        affiliateRequest.setEmail("john@example.com");
        affiliateRequest.setDocument("DOC12345");
        affiliateRequest.setSalary(new BigDecimal("5000.00"));

        mockMvc.perform(post("/api/affiliates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(affiliateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Assuming ID 1 for the first affiliate (fresh container)
        Long affiliateId = 1L;

        // 2. Mock Risk Service Response
        RiskEvaluationResult lowRisk = RiskEvaluationResult.builder()
                .score(800)
                .riskLevel("LOW")
                .rationale("Good score")
                .build();
        
        when(riskServicePort.evaluateRisk(anyString(), any(BigDecimal.class), anyInt()))
                .thenReturn(lowRisk);

        // 3. Apply for Credit
        ApplyForCreditRequest creditRequest = new ApplyForCreditRequest();
        creditRequest.setAffiliateId(affiliateId);
        creditRequest.setAmount(new BigDecimal("10000.00"));
        creditRequest.setTerm(12);

        mockMvc.perform(post("/api/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creditRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
