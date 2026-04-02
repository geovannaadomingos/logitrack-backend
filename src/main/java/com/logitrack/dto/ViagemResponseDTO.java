package com.logitrack.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViagemResponseDTO {

    private Long id;
    private Long veiculoId;
    private String veiculoPlaca;
    private String veiculoModelo;
    private String veiculoTipo;
    private LocalDateTime dataSaida;
    private LocalDateTime dataChegada;
    private String origem;
    private String destino;
    private BigDecimal kmPercorrida;
}
