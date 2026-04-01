package com.logitrack.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO que representa a projeção de custo de manutenção do mês atual.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjecaoCustoDTO {

    private Integer mes;
    private Integer ano;
    private BigDecimal custoEstimadoTotal;
    private Long totalManutencoes;
}
