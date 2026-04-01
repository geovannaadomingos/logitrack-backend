package com.logitrack.repository.projection;

import java.math.BigDecimal;

/**
 * Projeção Spring Data para cada entrada do ranking de veículos por KM percorrido.
 *
 * <p>Mapeamento de aliases SQL → getters:</p>
 * <ul>
 *   <li>{@code veiculo_id}    → {@link #getVeiculoId()}</li>
 *   <li>{@code placa}         → {@link #getPlaca()}</li>
 *   <li>{@code modelo}        → {@link #getModelo()}</li>
 *   <li>{@code tipo}          → {@link #getTipo()}</li>
 *   <li>{@code total_km}      → {@link #getTotalKm()}</li>
 *   <li>{@code total_viagens} → {@link #getTotalViagens()}</li>
 * </ul>
 */
public interface RankingVeiculoProjection {

    /** ID do veículo. */
    Long getVeiculoId();

    /** Placa do veículo. */
    String getPlaca();

    /** Modelo do veículo. */
    String getModelo();

    /** Tipo do veículo ({@code "LEVE"} ou {@code "PESADO"}). */
    String getTipo();

    /**
     * Total de km percorrido pelo veículo.
     * Nunca nulo — a query usa {@code COALESCE(SUM(...), 0)}.
     * Veículos sem viagens retornam {@code 0}.
     */
    BigDecimal getTotalKm();

    /** Total de viagens realizadas pelo veículo. */
    Long getTotalViagens();
}
