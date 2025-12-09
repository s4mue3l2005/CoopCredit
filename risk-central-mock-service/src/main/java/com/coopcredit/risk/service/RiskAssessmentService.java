package com.coopcredit.risk.service;

import com.coopcredit.risk.model.RiskAssessmentRequest;
import com.coopcredit.risk.model.RiskAssessmentResponse;
import com.coopcredit.risk.repository.RiskRepositoryMock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final RiskRepositoryMock riskRepositoryMock;

    public RiskAssessmentResponse evaluateRisk(RiskAssessmentRequest request) {
        return riskRepositoryMock.findRiskByDocument(request.getDocumentId());
    }
}
