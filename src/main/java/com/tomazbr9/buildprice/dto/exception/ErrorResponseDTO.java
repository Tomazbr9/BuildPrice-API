package com.tomazbr9.buildprice.dto.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        int status,
        String path,
        LocalDateTime timestamp

) {
}
