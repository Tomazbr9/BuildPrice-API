package com.tomazbr9.buildprice.dto.project_item;

import java.util.UUID;

public record ItemRequestDTO(
        UUID projectId,
        Integer quantity

) {
}
