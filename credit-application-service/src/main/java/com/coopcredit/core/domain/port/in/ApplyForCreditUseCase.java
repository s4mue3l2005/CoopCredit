package com.coopcredit.core.domain.port.in;

import com.coopcredit.core.domain.model.Credit;
import java.math.BigDecimal;

public interface ApplyForCreditUseCase {
    Credit apply(Long affiliateId, BigDecimal amount, Integer term);
}
