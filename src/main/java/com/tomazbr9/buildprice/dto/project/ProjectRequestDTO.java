package com.tomazbr9.buildprice.dto.project;

import java.math.BigDecimal;

public record ProjectRequestDTO(
        String nameWork,
        String clientName,
        String description,
        String uf,
        BigDecimal bdi
) {
}
