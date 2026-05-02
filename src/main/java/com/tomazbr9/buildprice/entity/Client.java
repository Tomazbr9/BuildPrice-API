package com.tomazbr9.buildprice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_clients")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Client implements Serializable {

    private static final long seriaVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = true)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "cpf_cnpj", nullable = false)
    private String cpfCnpj;

    private String phone;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
