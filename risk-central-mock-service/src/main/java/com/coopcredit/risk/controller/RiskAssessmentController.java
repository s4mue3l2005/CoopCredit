package com.coopcredit.risk.controller;

import com.coopcredit.risk.model.RiskAssessmentRequest;
import com.coopcredit.risk.model.RiskAssessmentResponse;
import com.coopcredit.risk.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    @PostMapping("/evaluate")
    public ResponseEntity<RiskAssessmentResponse> evaluate(@RequestBody RiskAssessmentRequest request) {
        RiskAssessmentResponse response = riskAssessmentService.evaluateRisk(request);
        return ResponseEntity.ok(response);
    }
}
