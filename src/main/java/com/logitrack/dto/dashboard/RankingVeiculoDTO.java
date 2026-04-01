package com.logitrack.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO que representa a posição de um veículo no ranking por KM percorrido.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingVeiculoDTO {

    private Long veiculoId;
    private String placa;
    private String modelo;
    private String tipo;
    private BigDecimal totalKm;
    private Long totalViagens;
}
