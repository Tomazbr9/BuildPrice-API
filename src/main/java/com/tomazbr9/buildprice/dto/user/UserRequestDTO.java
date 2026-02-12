package com.tomazbr9.buildprice.dto.user;

import com.tomazbr9.buildprice.enums.RoleName;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(

        @NotBlank(message = "Por favor, digite um email válido")
        String email,

        @NotBlank(message = "Por favor, digite uma senha válida")
        String password
) {}
