package com.coopcredit.core.infrastructure.adapter.out.external;

import com.coopcredit.core.domain.port.out.RiskEvaluationResult;
import com.coopcredit.core.domain.port.out.RiskServicePort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RiskServiceAdapter implements RiskServicePort {

    private final RestTemplate restTemplate;

    @Value("${risk.service.url:http://localhost:8081/api/risk/evaluate}")
    private String riskServiceUrl;

    @Override
    public RiskEvaluationResult evaluateRisk(String documentId, BigDecimal amount, Integer term) {
        RiskRequest request = new RiskRequest(documentId, amount, term);
        
        try {
            RiskResponse response = restTemplate.postForObject(riskServiceUrl, request, RiskResponse.class);
            if (response == null) {
                // Fallback or error handling
                throw new RuntimeException("Risk service returned null");
            }
            
            return RiskEvaluationResult.builder()
                    .score(response.getScore())
                    .riskLevel(response.getRiskLevel())
                    .rationale(response.getRationale())
                    .build();

        } catch (Exception e) {
            // For resilience: could return a default "MANUAL REVIEW" or rethrow
            throw new RuntimeException("Failed to call Risk Service", e);
        }
    }

    @Data
    @AllArgsConstructor
    public static class RiskRequest {
        private String documentId;
        private BigDecimal amount;
        private Integer term;
    }

    @Data
    public static class RiskResponse {
        private Integer score;
        private String riskLevel;
        private String rationale;
    }
}
