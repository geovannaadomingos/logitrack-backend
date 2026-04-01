package com.logitrack.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO que representa o cronograma de manutenção simplificado conforme requisitos do desafio.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProximaManutencaoDTO {

    private String placa;
    private String modelo;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;

    private String servico;
}
