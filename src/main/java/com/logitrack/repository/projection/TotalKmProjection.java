package com.logitrack.repository.projection;

import java.math.BigDecimal;

/**
 * Projeção Spring Data para o resultado da query de total de KM percorrido.
 *
 * <p>A convenção de mapeamento do Spring Data para queries nativas é:
 * alias SQL em {@code snake_case} → getter em {@code camelCase}.
 * Ex.: coluna {@code total_km} → método {@code getTotalKm()}.</p>
 */
public interface TotalKmProjection {

    /**
     * Soma total de {@code km_percorrida} de todas as viagens.
     * Nunca nulo — a query usa {@code COALESCE(..., 0)}.
     */
    BigDecimal getTotalKm();

    /**
     * Contagem total de viagens registradas.
     */
    Long getTotalViagens();
}
