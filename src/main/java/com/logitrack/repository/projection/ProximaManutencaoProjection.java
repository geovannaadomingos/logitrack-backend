package com.logitrack.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Projeção Spring Data para cada entrada da lista de próximas manutenções.
 *
 * <p>Mapeamento de aliases SQL → getters:</p>
 * <ul>
 *   <li>{@code manutencao_id}  → {@link #getManutencaoId()}</li>
 *   <li>{@code veiculo_id}     → {@link #getVeiculoId()}</li>
 *   <li>{@code placa}          → {@link #getPlaca()}</li>
 *   <li>{@code modelo}         → {@link #getModelo()}</li>
 *   <li>{@code data_inicio}    → {@link #getDataInicio()}</li>
 *   <li>{@code tipo_servico}   → {@link #getTipoServico()}</li>
 *   <li>{@code custo_estimado} → {@link #getCustoEstimado()}</li>
 *   <li>{@code status}         → {@link #getStatus()}</li>
 * </ul>
 *
 * <p><strong>Nota sobre tipos de data:</strong> a coluna {@code data_inicio} é
 * {@code DATE} no PostgreSQL. O driver JDBC a retorna como {@code java.sql.Date},
 * mas o Spring Data converte automaticamente para {@link LocalDate} quando o tipo
 * declarado no getter é {@code LocalDate}.</p>
 */
public interface ProximaManutencaoProjection {

    /** ID da manutenção. */
    Long getManutencaoId();

    /** ID do veículo associado. */
    Long getVeiculoId();

    /** Placa do veículo. */
    String getPlaca();

    /** Modelo do veículo. */
    String getModelo();

    /**
     * Data de início da manutenção.
     * Mapeado de coluna {@code DATE} — sem componente de hora.
     */
    LocalDate getDataInicio();

    /** Descrição do serviço a ser realizado. */
    String getTipoServico();

    /** Custo estimado da manutenção em reais. Pode ser nulo. */
    BigDecimal getCustoEstimado();

    /** Status da manutenção ({@code "PENDENTE"} ou {@code "EM_REALIZACAO"}). */
    String getStatus();
}
