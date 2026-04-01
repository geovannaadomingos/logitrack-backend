package com.logitrack.repository;

import com.logitrack.dto.dashboard.VolumePorTipoDTO;
import com.logitrack.entity.Viagem;
import com.logitrack.repository.projection.RankingVeiculoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Viagem.
 */
@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    @Query(
        value = """
                SELECT v FROM Viagem v
                JOIN FETCH v.veiculo ve
                WHERE (:veiculoId IS NULL OR ve.id = :veiculoId)
                """,
        countQuery = """
                SELECT COUNT(v) FROM Viagem v
                JOIN v.veiculo ve
                WHERE (:veiculoId IS NULL OR ve.id = :veiculoId)
                """
    )
    Page<Viagem> findAllWithVeiculo(@Param("veiculoId") Long veiculoId, Pageable pageable);

    @Query("SELECT v FROM Viagem v JOIN FETCH v.veiculo WHERE v.id = :id")
    Optional<Viagem> findByIdWithVeiculo(@Param("id") Long id);

    @Query("SELECT COALESCE(SUM(v.kmPercorrida), 0.0) FROM Viagem v")
    Optional<Double> findSomaTotalKm();

    @Query("SELECT COUNT(v) FROM Viagem v")
    Long findTotalViagens();

    @Query("""
            SELECT new com.logitrack.dto.dashboard.VolumePorTipoDTO(
                str(ve.tipo),
                COUNT(vi)
            )
            FROM Viagem vi
            JOIN vi.veiculo ve
            GROUP BY ve.tipo
            ORDER BY COUNT(vi) DESC
            """)
    List<VolumePorTipoDTO> getVolumePorTipo();

    @Query(value = """
            SELECT
                ve.id                              AS veiculo_id,
                ve.placa                           AS placa,
                ve.modelo                          AS modelo,
                ve.tipo                            AS tipo,
                COALESCE(SUM(vi.km_percorrida), 0) AS total_km,
                COUNT(vi.id)                       AS total_viagens
            FROM veiculos ve
            LEFT JOIN viagens vi ON vi.veiculo_id = ve.id
            GROUP BY ve.id, ve.placa, ve.modelo, ve.tipo
            ORDER BY total_km DESC
            """, nativeQuery = true)
    List<RankingVeiculoProjection> findRankingVeiculosPorKm();
}
