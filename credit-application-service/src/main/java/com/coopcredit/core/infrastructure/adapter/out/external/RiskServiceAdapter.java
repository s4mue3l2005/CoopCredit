package com.coopcredit.core.infrastructure.adapter.out.external;

import com.coopcredit.core.domain.port.out.RiskEvaluationResult;
import com.coopcredit.core.domain.port.out.RiskServicePort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskServiceAdapter implements RiskServicePort {

    private final RestTemplate restTemplate;

    @Value("${risk.service.url:http://localhost:8081/api/risk/evaluate}")
    private String riskServiceUrl;

    @Override
    public RiskEvaluationResult evaluateRisk(String documentId, BigDecimal amount, Integer term) {
        log.info("Calling Risk Service for document: {} with amount: {} and term: {} months",
                documentId, amount, term);
        log.debug("Risk Service URL: {}", riskServiceUrl);

        RiskRequest request = new RiskRequest(documentId, amount, term);

        try {
            RiskResponse response = restTemplate.postForObject(riskServiceUrl, request, RiskResponse.class);

            if (response == null) {
                log.error("Risk service returned null response for document: {}", documentId);
                throw new RiskServiceException("Risk service returned null response");
            }

            log.info("Risk evaluation completed for document: {} - Score: {}, Level: {}",
                    documentId, response.getScore(), response.getRiskLevel());

            return RiskEvaluationResult.builder()
                    .score(response.getScore())
                    .riskLevel(response.getRiskLevel())
                    .rationale(response.getRationale())
                    .build();

        } catch (HttpClientErrorException e) {
            log.error("Client error calling Risk Service (4xx): {} - {}", e.getStatusCode(), e.getMessage());
            throw new RiskServiceException(
                    String.format("Risk service client error: %s", e.getStatusCode()), e);

        } catch (HttpServerErrorException e) {
            log.error("Server error from Risk Service (5xx): {} - {}", e.getStatusCode(), e.getMessage());
            throw new RiskServiceException(
                    String.format("Risk service server error: %s", e.getStatusCode()), e);

        } catch (ResourceAccessException e) {
            log.error("Cannot connect to Risk Service at {}: {}", riskServiceUrl, e.getMessage());
            throw new RiskServiceException(
                    String.format("Cannot connect to Risk Service at %s. Service may be down.", riskServiceUrl), e);

        } catch (Exception e) {
            log.error("Unexpected error calling Risk Service: {}", e.getMessage(), e);
            throw new RiskServiceException("Unexpected error calling Risk Service", e);
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskResponse {
        private Integer score;
        private String riskLevel;
        private String rationale;
    }
}
