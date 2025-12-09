package com.coopcredit.core.infrastructure.adapter.in.web;

import com.coopcredit.core.domain.model.Affiliate;
import com.coopcredit.core.domain.port.in.RegisterAffiliateUseCase;
import com.coopcredit.core.infrastructure.adapter.in.web.dto.RegisterAffiliateRequest;
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

    @PostMapping
    public ResponseEntity<Affiliate> register(@RequestBody @Valid RegisterAffiliateRequest request) {
        Affiliate affiliate = Affiliate.builder()
                .name(request.getName())
                .email(request.getEmail())
                .document(request.getDocument())
                .salary(request.getSalary())
                .build();

        Affiliate created = registerAffiliateUseCase.register(affiliate);
        return ResponseEntity.created(URI.create("/api/affiliates/" + created.getId())).body(created);
    }
}
