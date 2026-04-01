package com.logitrack.repository.projection;

/**
 * Projeção Spring Data para o resultado da query de volume de viagens por tipo de veículo.
 *
 * <p>Mapeamento de aliases SQL → getters:</p>
 * <ul>
 *   <li>{@code tipo_veiculo}  → {@link #getTipoVeiculo()}</li>
 *   <li>{@code total_viagens} → {@link #getTotalViagens()}</li>
 * </ul>
 */
public interface VolumePorTipoProjection {

    /**
     * Tipo do veículo ({@code "LEVE"} ou {@code "PESADO"}).
     */
    String getTipoVeiculo();

    /**
     * Quantidade de viagens realizadas por veículos deste tipo.
     */
    Long getTotalViagens();
}
