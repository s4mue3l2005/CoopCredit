package com.coopcredit.core.domain.port.in;

import com.coopcredit.core.domain.model.Credit;

public interface EvaluateCreditUseCase {
    Credit evaluate(Long creditId, String status, String rationale);
}

