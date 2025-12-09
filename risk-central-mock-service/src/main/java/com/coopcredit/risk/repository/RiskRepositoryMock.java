package com.coopcredit.risk.repository;

import com.coopcredit.risk.model.RiskAssessmentResponse;
import com.coopcredit.risk.model.RiskLevel;
import org.springframework.stereotype.Repository;

/**
 * Mock Repository to simulate data access/calculation.
 * Follows the Layered Architecture requirement: Controller -> Service ->
 * RepositoryMock.
 */
@Repository
public class RiskRepositoryMock {

    public RiskAssessmentResponse findRiskByDocument(String documentId) {
        // Simulation of database lookup/calculation
        int seed = Math.abs(documentId.hashCode());
        int score = 300 + (seed % 651); // 300 - 950

        RiskLevel level;
        String rationale;

        if (score < 550) {
            level = RiskLevel.HIGH;
            rationale = "Candidate has a low credit score: " + score;
        } else if (score < 750) {
            level = RiskLevel.MEDIUM;
            rationale = "Candidate has a moderate credit score: " + score;
        } else {
            level = RiskLevel.LOW;
            rationale = "Candidate has a good credit score: " + score;
        }

        return RiskAssessmentResponse.builder()
                .score(score)
                .riskLevel(level)
                .rationale(rationale)
                .build();
    }
}
