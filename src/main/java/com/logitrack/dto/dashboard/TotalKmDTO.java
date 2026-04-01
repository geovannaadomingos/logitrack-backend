package com.logitrack.dto.dashboard;

import lombok.*;

/**
 * DTO de resposta para o endpoint {@code GET /api/v1/dashboard/total-km}.
 *
 * <h3>Por que Double e não BigDecimal?</h3>
 * <p>Para métricas de dashboard exibidas em UI, {@code Double} é suficiente em
 * precisão e produz serialização JSON mais limpa (ex.: {@code 6183.3} em vez de
 * {@code 6183.30}). Para cálculos financeiros críticos, {@code BigDecimal} seria
 * obrigatório — mas para KM percorrido, {@code Double} é adequado.</p>
 *
 * <h3>Serialização garantida</h3>
 * <p>Todos os campos são primitivos com valor padrão {@code 0}. Mesmo com
 * {@code spring.jackson.default-property-inclusion=non_null}, primitivos nunca
 * são nulos no Java, então a resposta JSON <strong>sempre</strong> conterá os
 * campos — sem risco de retornar {@code {}}.</p>
 *
 * <p>Exemplo de resposta:</p>
 * <pre>
 * {
 *   "totalKm": 6183.3,
 *   "totalViagens": 8
 * }
 * </pre>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalKmDTO {

    /**
     * Soma total de km percorrida por toda a frota.
     * Valor padrão: {@code 0.0} (frota sem viagens registradas).
     */
    @Builder.Default
    private Double totalKm = 0.0;

    /**
     * Quantidade total de viagens registradas.
     * Valor padrão: {@code 0L}.
     */
    @Builder.Default
    private Long totalViagens = 0L;
}
