package com.tomazbr9.buildprice.dto.project;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProjectRequestDTO(

        @NotBlank(message = "O nome não pode está em branco")
        String nameWork,

        String clientName,
        String description,

        @NotBlank(message = "uf é obrigatório")
        String uf,

        BigDecimal bdi
) {
}
