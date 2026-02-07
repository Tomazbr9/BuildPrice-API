package com.tomazbr9.buildprice.dto.project;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectResponseDTO(
        UUID id,
        String nameWork,
        String clientName,
        String description,
        String uf,
        BigDecimal bdi,
        Instant createdAt
) {
}
