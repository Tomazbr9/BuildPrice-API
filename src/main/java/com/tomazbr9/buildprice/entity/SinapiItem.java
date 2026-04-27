package com.tomazbr9.buildprice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_sinapi_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SinapiItem implements Serializable {

    private static final long seriaVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cod_sinapi", nullable = false)
    private String codSinapi;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String classification;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String uf;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "tax_relief", nullable = false)
    private String taxRelief;

    @Column(name = "reference_month", nullable = false)
    private LocalDate referenceMonth;

}
