package com.coopcredit.core.infrastructure.adapter.in.web;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.port.in.RegisterAffiliateUseCase;
import com.coopcredit.core.domain.port.in.UpdateAffiliateUseCase;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.RegisterAffiliateRequest;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.UpdateAffiliateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/affiliates")
@RequiredArgsConstructor
public class AffiliateController {

    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final UpdateAffiliateUseCase updateAffiliateUseCase;

    @PostMapping
    public ResponseEntity<Affiliate> register(@RequestBody @Valid RegisterAffiliateRequest request) {
        Affiliate affiliate = Affiliate.builder()
                .name(request.getName())
                .email(request.getEmail())
                .document(request.getDocument())
                .salary(request.getSalary())
                .enrollmentDate(request.getEnrollmentDate())
                .build();

        Affiliate created = registerAffiliateUseCase.register(affiliate);
        return ResponseEntity.created(URI.create("/api/affiliates/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Affiliate> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAffiliateRequest request) {
        Affiliate updated = updateAffiliateUseCase.update(
                id,
                request.getName(),
                request.getEmail(),
                request.getAddress(),
                request.getPhone(),
                request.getSalary());
        return ResponseEntity.ok(updated);
    }
}
