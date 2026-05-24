package com.tomazbr9.buildprice.dto.project;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record ProjectResponseDTO(
        UUID id,
        String nameWork,
        String description,
        String uf,
        Instant createdAt,
        List<UUID> budgetIds
) {
}
