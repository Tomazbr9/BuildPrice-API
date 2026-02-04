package com.tomazbr9.buildprice.dto.sinapi;

import java.math.BigDecimal;
import java.util.Map;

public record SinapiItemDTO(

        String classification,

        String codSinapi,

        String description,

        String unit,

        String taxRelief,

        Map<String, BigDecimal> pricesForUf

) {

}
