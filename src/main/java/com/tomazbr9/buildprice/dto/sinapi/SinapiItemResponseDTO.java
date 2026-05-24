package com.tomazbr9.buildprice.dto.sinapi;

import java.math.BigDecimal;
import java.util.UUID;

public record SinapiItemResponseDTO(
        UUID id,
        String codSinapi,
        String description,
        String classification,
        String unit,
        String uf,
        String taxRelief,
        BigDecimal price
) {}