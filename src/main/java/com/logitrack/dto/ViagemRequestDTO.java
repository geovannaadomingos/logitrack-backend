package com.logitrack.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de entrada para criação e atualização de uma Viagem.
 * Contém apenas os campos que o cliente deve fornecer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViagemRequestDTO {

    @NotNull(message = "O ID do veículo é obrigatório")
    private Long veiculoId;

    @NotNull(message = "A data de saída é obrigatória")
    private LocalDateTime dataSaida;

    private LocalDateTime dataChegada;

    @NotBlank(message = "A origem é obrigatória")
    @Size(max = 200, message = "A origem deve ter no máximo 200 caracteres")
    private String origem;

    @NotBlank(message = "O destino é obrigatório")
    @Size(max = 200, message = "O destino deve ter no máximo 200 caracteres")
    private String destino;

    @NotNull(message = "O km percorrido é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "O km percorrido deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido para km percorrida")
    private BigDecimal kmPercorrida;
}
