package com.logitrack.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingVeiculoDTO {

    private String placa;
    private String modelo;
    private String tipo;
    private BigDecimal totalKm;
}
