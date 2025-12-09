package com.coopcredit.core.infrastructure.adapter.in.web;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.port.in.ApplyForCreditUseCase;
import com.coopcredit.core.domain.port.in.GetCreditApplicationsUseCase;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.ApplyForCreditRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {

    private final ApplyForCreditUseCase applyForCreditUseCase;
    private final GetCreditApplicationsUseCase getCreditApplicationsUseCase;

    @PostMapping
    public ResponseEntity<Credit> apply(@RequestBody @Valid ApplyForCreditRequest request) {
        Credit credit = applyForCreditUseCase.apply(request.getAffiliateId(), request.getAmount(), request.getTerm());
        return ResponseEntity.created(URI.create("/api/credits/" + credit.getId())).body(credit);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<List<Credit>> getAll() {
        return ResponseEntity.ok(getCreditApplicationsUseCase.getAll());
    }

    @GetMapping("/affiliate/{affiliateId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AFFILIATE') and #affiliateId == principal.id)") 
    // Note: principal.id check requires custom Principal, for now strict roles or simplified check
    public ResponseEntity<List<Credit>> getByAffiliate(@PathVariable Long affiliateId) {
        return ResponseEntity.ok(getCreditApplicationsUseCase.getByAffiliate(affiliateId));
    }
}
