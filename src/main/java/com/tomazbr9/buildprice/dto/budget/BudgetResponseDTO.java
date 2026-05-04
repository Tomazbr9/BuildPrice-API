package com.tomazbr9.buildprice.dto.budget;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponseDTO(
        UUID id,
        String name,
        BigDecimal bdi
) {
}
