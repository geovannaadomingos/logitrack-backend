package com.logitrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma viagem realizada por um veículo da frota.
 *
 * <p>Cada viagem pertence a exatamente um veículo — relacionamento ManyToOne.
 * O campo {@code veiculo} usa {@code FetchType.LAZY} por padrão de performance;
 * os métodos de busca no repositório utilizam {@code JOIN FETCH} quando necessário
 * para evitar {@code LazyInitializationException} fora de sessão JPA.</p>
 *
 * <p>Os campos {@code created_at} e {@code updated_at} não pertencem ao schema
 * original do banco e foram removidos para garantir compatibilidade total.</p>
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

    /**
     * Veículo que realizou a viagem.
     * LAZY — sempre use queries com JOIN FETCH ao precisar deste dado fora de transação.
     */
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

    /**
     * Quilometragem total percorrida na viagem.
     * Precisão de 10 dígitos com 2 casas decimais (ex.: 12345678.99 km).
     */
    @Column(name = "km_percorrida", precision = 10, scale = 2)
    private BigDecimal kmPercorrida;
}
