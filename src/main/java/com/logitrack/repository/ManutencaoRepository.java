package com.logitrack.repository;

import com.logitrack.entity.Manutencao;
import com.logitrack.repository.projection.ProjecaoCustoProjection;
import com.logitrack.repository.projection.ProximaManutencaoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade {@link Manutencao}.
 *
 * <h3>Projections tipadas</h3>
 * <p>Todas as queries nativas retornam interfaces de projeção Spring Data em vez de
 * {@code Object[]}. O framework mapeia automaticamente os aliases SQL em
 * {@code snake_case} para os getters {@code camelCase} da interface.</p>
 *
 * <h3>CAST explícito em EXTRACT()</h3>
 * <p>O PostgreSQL retorna {@code DOUBLE PRECISION} para {@code EXTRACT()}. Para obter
 * {@code Integer} diretamente no getter da projeção ({@link ProjecaoCustoProjection#getMes()}
 * e {@link ProjecaoCustoProjection#getAno()}), a query aplica
 * {@code CAST(EXTRACT(...) AS INTEGER)} explicitamente, eliminando qualquer conversão
 * ou perda de precisão no lado Java.</p>
 *
 * <h3>Tipos de data</h3>
 * <p>A coluna {@code data_inicio} é {@code DATE} no PostgreSQL. O Spring Data converte
 * automaticamente {@code java.sql.Date} para {@link java.time.LocalDate} quando o tipo
 * declarado no getter da projeção é {@code LocalDate}.</p>
 */
@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

    /**
     * Retorna as próximas 5 manutenções com status {@code PENDENTE} ou
     * {@code EM_REALIZACAO}, ordenadas pela data de início mais próxima.
     *
     * <p>Alias SQL → getter da projeção:</p>
     * <ul>
     *   <li>{@code manutencao_id}  → {@code getManutencaoId()}</li>
     *   <li>{@code veiculo_id}     → {@code getVeiculoId()}</li>
     *   <li>{@code data_inicio}    → {@code getDataInicio()} — {@code LocalDate}</li>
     *   <li>{@code tipo_servico}   → {@code getTipoServico()}</li>
     *   <li>{@code custo_estimado} → {@code getCustoEstimado()}</li>
     * </ul>
     *
     * @return lista de até 5 projeções de manutenções futuras
     */
    @Query(value = """
            SELECT
                m.id             AS manutencao_id,
                ve.id            AS veiculo_id,
                ve.placa         AS placa,
                ve.modelo        AS modelo,
                m.data_inicio    AS data_inicio,
                m.tipo_servico   AS tipo_servico,
                m.custo_estimado AS custo_estimado,
                m.status         AS status
            FROM manutencoes m
            INNER JOIN veiculos ve ON ve.id = m.veiculo_id
            WHERE m.status IN ('PENDENTE', 'EM_REALIZACAO')
            ORDER BY m.data_inicio ASC
            LIMIT 5
            """, nativeQuery = true)
    List<ProximaManutencaoProjection> findProximasManutencoes();

    /**
     * Retorna a projeção de custo total de manutenção para o mês e ano correntes.
     *
     * <p>Considera apenas manutenções com status {@code PENDENTE} ou {@code EM_REALIZACAO}
     * cuja {@code data_inicio} caia no mês e ano atuais.</p>
     *
     * <p><strong>CAST explícito:</strong> {@code EXTRACT()} retorna {@code DOUBLE PRECISION}
     * no PostgreSQL. O {@code CAST(...AS INTEGER)} garante que o Spring Data mapeie
     * corretamente os valores para {@link Integer} nos getters {@code getMes()} e
     * {@code getAno()} sem conversão extra.</p>
     *
     * <p>Retorna {@link Optional} pois a query sempre produz exatamente uma linha
     * (agregação sem GROUP BY), mas o {@code Optional} comunica claramente a
     * intenção e facilita o uso seguro no service.</p>
     *
     * @return Optional contendo a projeção de custo do mês atual
     */
    @Query(value = """
            SELECT
                CAST(EXTRACT(MONTH FROM CURRENT_DATE) AS INTEGER) AS mes,
                CAST(EXTRACT(YEAR  FROM CURRENT_DATE) AS INTEGER) AS ano,
                COALESCE(SUM(m.custo_estimado), 0)                AS custo_estimado_total,
                COUNT(m.id)                                       AS total_manutencoes
            FROM manutencoes m
            WHERE EXTRACT(MONTH FROM m.data_inicio) = EXTRACT(MONTH FROM CURRENT_DATE)
              AND EXTRACT(YEAR  FROM m.data_inicio) = EXTRACT(YEAR  FROM CURRENT_DATE)
              AND m.status IN ('PENDENTE', 'EM_REALIZACAO')
            """, nativeQuery = true)
    Optional<ProjecaoCustoProjection> findProjecaoCustoMesAtual();
}
