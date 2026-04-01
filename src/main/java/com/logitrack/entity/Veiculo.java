package com.logitrack.entity;

import com.logitrack.entity.enums.TipoVeiculo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um veículo da frota.
 *
 * <p>Mapeada para a tabela {@code veiculos}. Os campos {@code created_at} e
 * {@code updated_at} não pertencem ao schema original do banco e foram removidos
 * para garantir compatibilidade total com o DDL existente.</p>
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

    /**
     * Placa do veículo no formato brasileiro (ex.: ABC-1234 ou ABC1D23).
     * Valor único — não pode haver dois veículos com a mesma placa.
     */
    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVeiculo tipo;

    @Column(nullable = false)
    private Integer ano;

    /**
     * Coleção de viagens associadas a este veículo.
     * Carregamento LAZY — não inicializar fora de contexto transacional.
     */
    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Viagem> viagens = new ArrayList<>();

    /**
     * Coleção de manutenções associadas a este veículo.
     * Carregamento LAZY — não inicializar fora de contexto transacional.
     */
    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Manutencao> manutencoes = new ArrayList<>();
}
