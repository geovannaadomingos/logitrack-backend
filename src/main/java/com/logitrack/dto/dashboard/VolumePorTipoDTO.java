package com.logitrack.dto.dashboard;

import lombok.*;

/**
 * DTO que representa o volume de viagens agrupado por tipo de veículo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolumePorTipoDTO {

    private String tipoVeiculo;
    private Long totalViagens;
}
