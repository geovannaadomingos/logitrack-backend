package com.logitrack.entity;

import com.logitrack.entity.enums.StatusManutencao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa uma manutenção de um veículo da frota.
 *
 * <p>Os campos {@code data_inicio} e {@code data_finalizacao} são mapeados
 * como {@code LocalDate} pois o banco de dados define essas colunas com o tipo
 * SQL {@code DATE} (sem componente de hora).</p>
 *
 * <p>O campo {@code status} reflete o ciclo de vida da manutenção:
 * PENDENTE → EM_REALIZACAO → CONCLUIDA</p>
 */
@Entity
@Table(name = "manutencoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "veiculo")
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Veículo que será ou foi submetido à manutenção.
     * LAZY — carregar via JOIN FETCH quando necessário projetar dados do veículo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    /**
     * Data de início da manutenção (coluna DATE no banco — sem hora).
     */
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    /**
     * Data de finalização da manutenção (coluna DATE no banco — sem hora).
     * Nulo enquanto a manutenção não for concluída.
     */
    @Column(name = "data_finalizacao")
    private LocalDate dataFinalizacao;

    @Column(name = "tipo_servico", nullable = false, length = 200)
    private String tipoServico;

    /**
     * Custo estimado da manutenção em reais.
     * Precisão de 10 dígitos com 2 casas decimais.
     */
    @Column(name = "custo_estimado", precision = 10, scale = 2)
    private BigDecimal custoEstimado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusManutencao status = StatusManutencao.PENDENTE;
}
