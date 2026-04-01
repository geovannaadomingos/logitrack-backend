package com.logitrack.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de saída para representação de uma Viagem nas respostas da API.
 *
 * <h3>Design</h3>
 * <p>Este DTO é um <em>POJO puro</em> — não possui referências à camada de
 * persistência, não importa entidades e não contém lógica de conversão.
 * Toda a transformação de {@link com.logitrack.entity.Viagem} para este DTO
 * é responsabilidade do {@link com.logitrack.mapper.ViagemMapper}.</p>
 *
 * <h3>Estrutura achatada</h3>
 * <p>Os dados do veículo ({@code veiculoId}, {@code veiculoPlaca},
 * {@code veiculoModelo}, {@code veiculoTipo}) são incorporados diretamente
 * neste DTO, evitando estruturas aninhadas e eliminando a necessidade de uma
 * segunda requisição para obter os dados do veículo associado.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViagemResponseDTO {

    private Long id;

    // Dados do veículo achatados (flat) para evitar estrutura aninhada
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
