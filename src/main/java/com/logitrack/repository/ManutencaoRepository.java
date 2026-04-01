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
 * Repositório JPA para a entidade Manutencao.
 */
@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

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
