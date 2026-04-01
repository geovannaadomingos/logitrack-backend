package com.logitrack.entity;

import com.logitrack.entity.enums.TipoVeiculo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um veículo da frota.
 */
@Entity
@Table(name = "veiculos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"viagens", "manutencoes"})
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVeiculo tipo;

    @Column(nullable = false)
    private Integer ano;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Viagem> viagens = new ArrayList<>();

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Manutencao> manutencoes = new ArrayList<>();
}
