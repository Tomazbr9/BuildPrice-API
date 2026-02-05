package com.tomazbr9.buildprice.dto.project_item;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponseDTO(
        UUID id,
        Integer quantity,
        BigDecimal price
) {
}
