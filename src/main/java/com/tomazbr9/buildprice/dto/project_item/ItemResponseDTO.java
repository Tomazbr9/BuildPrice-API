package com.tomazbr9.buildprice.dto.project_item;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponseDTO(
        UUID id,
        String codSinapi,
        String description,
        String classification,
        String unit,
        String uf,
        Integer quantity,
        BigDecimal price,
        BigDecimal subTotal
) {
}
