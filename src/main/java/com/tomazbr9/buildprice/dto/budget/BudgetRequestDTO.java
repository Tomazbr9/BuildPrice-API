package com.tomazbr9.buildprice.dto.budget;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BudgetRequestDTO(
        String name,
        BigDecimal bdi,
        UUID projectId
) {
}
