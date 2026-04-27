package com.tomazbr9.buildprice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_projects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Project  implements Serializable {

    private static final long seriaVersionUID = 1L;

    @Id
    @Column(nullable = false, updatable = true)
    @GeneratedValue
    private UUID id;

    @Column(name = "name_work", nullable = false)
    private String nameWork;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "client_name")
    private String clientName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String uf;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal bdi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist(){
        if(bdi == null){
            bdi = BigDecimal.ZERO;
        }
    }
}
