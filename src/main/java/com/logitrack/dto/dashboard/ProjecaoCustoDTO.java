package com.logitrack.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO que representa a soma total estimada de custos de manutenção.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjecaoCustoDTO {

    private BigDecimal total;
}
