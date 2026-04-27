package com.tomazbr9.buildprice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "budget")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Budget {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = true)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal bdi;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}
