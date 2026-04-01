package com.logitrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma viagem realizada por um veículo.
 */
@Entity
@Table(name = "viagens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "veiculo")
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(name = "data_saida", nullable = false)
    private LocalDateTime dataSaida;

    @Column(name = "data_chegada")
    private LocalDateTime dataChegada;

    @Column(nullable = false, length = 200)
    private String origem;

    @Column(nullable = false, length = 200)
    private String destino;

    @Column(name = "km_percorrida", precision = 10, scale = 2)
    private BigDecimal kmPercorrida;
}
