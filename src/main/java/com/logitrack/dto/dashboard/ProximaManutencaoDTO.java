package com.logitrack.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa uma manutenção próxima agendada ou em andamento.
 *
 * <p>O campo {@code dataInicio} é do tipo {@link LocalDate} pois a coluna
 * {@code data_inicio} no banco é {@code DATE} (sem componente de hora).</p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProximaManutencaoDTO {

    private Long manutencaoId;
    private Long veiculoId;
    private String placa;
    private String modelo;
    private LocalDate dataInicio;
    private String tipoServico;
    private BigDecimal custoEstimado;
    private String status;
}
