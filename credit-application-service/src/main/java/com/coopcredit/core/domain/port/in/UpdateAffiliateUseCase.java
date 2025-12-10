package com.coopcredit.core.domain.port.in;

import com.coopcredit.core.domain.model.Affiliate;

public interface UpdateAffiliateUseCase {
    Affiliate update(Long affiliateId, String name, String email, String address, String phone, java.math.BigDecimal salary);
}

