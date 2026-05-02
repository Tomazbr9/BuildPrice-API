package com.tomazbr9.buildprice.dto.client;

import java.util.UUID;

public record ClientResponseDTO(
        UUID id,
        String name,
        String cpfCnpj,
        String phone,
        String email
) {
}
