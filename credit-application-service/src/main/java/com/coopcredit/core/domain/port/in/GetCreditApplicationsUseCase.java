package com.coopcredit.core.domain.port.in;

import com.coopcredit.core.domain.model.Credit;
import java.util.List;

public interface GetCreditApplicationsUseCase {
    List<Credit> getByAffiliate(Long affiliateId);
    List<Credit> getAll();
}
