package com.tomazbr9.buildprice.dto.client;

public record ClientRequestDTO(
        String name,
        String cpfCnpj,
        String phone,
        String email
) {
}
