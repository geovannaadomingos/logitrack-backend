package com.logitrack.repository.projection;

import java.math.BigDecimal;

/**
 * Projeção Spring Data para o resultado da query de projeção de custo de manutenção.
 *
 * <p>Mapeamento de aliases SQL → getters:</p>
 * <ul>
 *   <li>{@code mes}                  → {@link #getMes()}</li>
 *   <li>{@code ano}                  → {@link #getAno()}</li>
 *   <li>{@code custo_estimado_total} → {@link #getCustoEstimadoTotal()}</li>
 *   <li>{@code total_manutencoes}    → {@link #getTotalManutencoes()}</li>
 * </ul>
 *
 * <p><strong>Nota sobre EXTRACT:</strong> a função {@code EXTRACT()} do PostgreSQL
 * retorna {@code DOUBLE PRECISION} por padrão. Para obter {@code Integer} diretamente
 * no getter, a query utiliza {@code CAST(EXTRACT(...) AS INTEGER)} explicitamente,
 * evitando conversão extra no lado Java.</p>
 */
public interface ProjecaoCustoProjection {

    /**
     * Mês de referência (1-12).
     * Obtido via {@code CAST(EXTRACT(MONTH FROM CURRENT_DATE) AS INTEGER)}.
     */
    Integer getMes();

    /**
     * Ano de referência (ex.: 2026).
     * Obtido via {@code CAST(EXTRACT(YEAR FROM CURRENT_DATE) AS INTEGER)}.
     */
    Integer getAno();

    /**
     * Soma dos custos estimados das manutenções do mês.
     * Nunca nulo — a query usa {@code COALESCE(SUM(...), 0)}.
     */
    BigDecimal getCustoEstimadoTotal();

    /**
     * Quantidade de manutenções com status {@code PENDENTE} ou {@code EM_REALIZACAO}
     * no mês de referência.
     */
    Long getTotalManutencoes();
}
