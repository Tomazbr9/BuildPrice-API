package com.tomazbr9.buildprice.dto.project_item;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemRequestDTO(
        UUID budgetId,

        @NotNull(message = "Quantidade do item é obrigatória")
        Integer quantity

) {
}
