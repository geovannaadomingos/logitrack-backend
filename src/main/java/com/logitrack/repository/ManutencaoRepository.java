package com.logitrack.repository;

import com.logitrack.dto.dashboard.ProximaManutencaoDTO;
import com.logitrack.entity.Manutencao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

    @Query("""
            SELECT new com.logitrack.dto.dashboard.ProximaManutencaoDTO(
                ve.placa,
                ve.modelo,
                m.dataInicio,
                m.tipoServico
            )
            FROM Manutencao m
            JOIN m.veiculo ve
            WHERE m.status IN ('PENDENTE', 'EM_REALIZACAO')
            ORDER BY m.dataInicio ASC
            """)
    List<ProximaManutencaoDTO> findProximasManutencoes(Pageable pageable);

    @Query("SELECT COALESCE(SUM(m.custoEstimado), 0.0) FROM Manutencao m WHERE m.status IN ('PENDENTE', 'EM_REALIZACAO')")
    Optional<BigDecimal> sumTotalCustoEstimado();
}
