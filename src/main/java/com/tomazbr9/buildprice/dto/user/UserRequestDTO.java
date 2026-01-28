package com.tomazbr9.buildprice.dto.user;

import com.tomazbr9.buildprice.enums.RoleName;

public record UserRequestDTO(
        String email,
        String password
) {}
