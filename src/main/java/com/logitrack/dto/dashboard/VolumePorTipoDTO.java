package com.logitrack.dto.dashboard;

import lombok.*;

/**
 * DTO que representa o volume de viagens agrupado por tipo de veículo.
 *
 * <h3>Uso em Construtor JPQL</h3>
 * <p>Este DTO é instanciado diretamente pelo Hibernate via expressão
 * {@code SELECT new ...} no repositório. O construtor deve ser público
 * e os tipos dos parâmetros devem bater exatamente com os retornos da query
 * (String para o tipo e Long para o COUNT).</p>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumePorTipoDTO {

    /**
     * Tipo do veículo (LEVE / PESADO).
     */
    private String tipo;

    /**
     * Quantidade total de viagens realizadas por veículos deste tipo.
     */
    private Long volume;
}
