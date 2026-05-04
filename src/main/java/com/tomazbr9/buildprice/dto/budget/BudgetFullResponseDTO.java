package com.tomazbr9.buildprice.dto.budget;

import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;
import com.tomazbr9.buildprice.entity.ProjectItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BudgetFullResponseDTO(
        UUID id,
        String name,
        Instant createdAt,
        BigDecimal bdi,
        List<ItemResponseDTO> items,
        BigDecimal totalWithOutBDI,
        BigDecimal totalWithBDI,
        BigDecimal grossMargin
) {
}
