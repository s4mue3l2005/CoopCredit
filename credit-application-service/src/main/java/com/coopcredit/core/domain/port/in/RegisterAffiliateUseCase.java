package com.coopcredit.core.domain.port.in;

import com.coopcredit.core.domain.model.Affiliate;

public interface RegisterAffiliateUseCase {
    Affiliate register(Affiliate affiliate);
}
