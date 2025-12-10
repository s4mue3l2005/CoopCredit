package com.coopcredit.core.infrastructure.adapter.in.web;

import com.coopcredit.core.domain.model.Credit;
import com.coopcredit.core.domain.port.in.ApplyForCreditUseCase;
import com.coopcredit.core.domain.port.in.EvaluateCreditUseCase;
import com.coopcredit.core.domain.port.in.GetCreditApplicationsUseCase;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.ApplyForCreditRequest;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.EvaluateCreditRequest;
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
    private final EvaluateCreditUseCase evaluateCreditUseCase;

    @PostMapping
    public ResponseEntity<Credit> apply(@RequestBody @Valid ApplyForCreditRequest request) {
        Credit credit = applyForCreditUseCase.apply(request.getAffiliateId(), request.getAmount(), request.getTerm());
        return ResponseEntity.created(URI.create("/api/credits/" + credit.getId())).body(credit);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<List<Credit>> getAll(org.springframework.security.core.Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ANALYST"))) {
            return ResponseEntity.ok(getCreditApplicationsUseCase.getPending());
        }
        return ResponseEntity.ok(getCreditApplicationsUseCase.getAll());
    }

    @GetMapping("/affiliate/{affiliateId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('AFFILIATE') and #affiliateId == principal.id)")
    // Note: principal.id check requires custom Principal, for now strict roles or
    // simplified check
    public ResponseEntity<List<Credit>> getByAffiliate(@PathVariable Long affiliateId) {
        return ResponseEntity.ok(getCreditApplicationsUseCase.getByAffiliate(affiliateId));
    }

    @PatchMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<Credit> evaluate(
            @PathVariable Long id,
            @RequestBody @Valid EvaluateCreditRequest request) {
        Credit evaluated = evaluateCreditUseCase.evaluate(id, request.getStatus(), request.getRationale());
        return ResponseEntity.ok(evaluated);
    }
}
