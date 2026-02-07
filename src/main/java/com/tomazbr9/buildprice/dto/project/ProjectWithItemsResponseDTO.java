package com.tomazbr9.buildprice.dto.project;

import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProjectWithItemsResponseDTO(
        UUID id,
        String nameWork,
        String clientName,
        String description,
        String state,
        BigDecimal bdi,
        List<ItemResponseDTO> items,
        BigDecimal totalWithOutBDI,
        BigDecimal totalWithBDI,
        BigDecimal grossMargin,
        Instant createdAt
) {}
