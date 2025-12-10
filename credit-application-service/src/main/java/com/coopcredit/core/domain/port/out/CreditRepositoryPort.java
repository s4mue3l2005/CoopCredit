package com.coopcredit.core.domain.port.out;

import com.coopcredit.core.domain.model.Credit;
import java.util.List;
import java.util.Optional;

public interface CreditRepositoryPort {
    Credit save(Credit credit);

    Optional<Credit> findById(Long id);

    List<Credit> findByAffiliateId(Long affiliateId);

    List<Credit> findAll();

    List<Credit> findByStatus(String status);
}
