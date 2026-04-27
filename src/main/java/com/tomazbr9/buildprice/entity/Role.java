package com.tomazbr9.buildprice.entity;

import com.tomazbr9.buildprice.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Role implements Serializable {
    private static final long seriaVersionUID = 1L;

    @Id
    @Column(nullable = false, updatable = true)
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

}
