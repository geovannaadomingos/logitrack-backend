package com.logitrack.repository;

import com.logitrack.entity.Viagem;
import com.logitrack.repository.projection.RankingVeiculoProjection;
import com.logitrack.repository.projection.VolumePorTipoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade {@link Viagem}.
 *
 * <h3>Estratégia de carregamento</h3>
 * <p>O relacionamento {@code Viagem → Veiculo} é {@code LAZY}. Para evitar
 * {@code LazyInitializationException} ao mapear para
 * {@link com.logitrack.dto.ViagemResponseDTO}, todos os métodos que retornam
 * entidades completas utilizam {@code JOIN FETCH} embutido na JPQL.
 * A {@code countQuery} separada evita o problema de paginação in-memory
 * (HHH90003004) que ocorreria se o Hibernate tentasse aplicar {@code LIMIT/OFFSET}
 * sobre uma query com {@code FETCH JOIN}.</p>
 *
 * <h3>Paginação com filtro opcional</h3>
 * <p>O método {@link #findAllWithVeiculo(Long, Pageable)} aceita {@code veiculoId}
 * nulo para retornar todas as viagens, ou um ID específico para filtrar por veículo.
 * O parâmetro é passado como {@code Long} — JPQL avalia {@code :veiculoId IS NULL}
 * corretamente em ambos os casos.</p>
 *
 * <h3>Queries de Dashboard — Projections</h3>
 * <p>Todas as queries nativas do dashboard retornam interfaces de projeção Spring Data
 * em vez de {@code Object[]}. A convenção de mapeamento é automaticamente aplicada
 * pelo framework: alias SQL {@code snake_case} → getter {@code camelCase}.</p>
 */
@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    // ===================================================================
    // JPQL com JOIN FETCH — evitam LazyInitializationException
    // ===================================================================

    /**
     * Retorna uma página de viagens com suporte a filtro opcional por veículo.
     *
     * <p>A {@code countQuery} separada é obrigatória quando a query principal
     * usa {@code JOIN FETCH}, pois o Hibernate não consegue derivar automaticamente
     * a contagem de uma query com fetch join.</p>
     *
     * @param veiculoId filtra viagens de um veículo específico; {@code null} retorna tudo
     * @param pageable  parâmetros de paginação e ordenação (page, size, sort)
     * @return página de viagens com o veículo já carregado
     */
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

    /**
     * Busca uma viagem pelo ID com o veículo já carregado (JOIN FETCH).
     * Usado em {@code buscarPorId} e {@code atualizar} para evitar N+1.
     *
     * @param id identificador da viagem
     * @return Optional com a viagem e seu veículo, ou vazio se não encontrado
     */
    @Query("SELECT v FROM Viagem v JOIN FETCH v.veiculo WHERE v.id = :id")
    Optional<Viagem> findByIdWithVeiculo(@Param("id") Long id);

    // ===================================================================
    // Queries JPQL — Dashboard (valores escalares diretos)
    // ===================================================================

    /**
     * Retorna a soma total de km percorrida por toda a frota.
     *
     * <p>Usa JPQL (não nativa) para retornar um {@code Double} escalar diretamente,
     * evitando a camada de proxy de projeção que pode retornar {@code null}
     * silenciosamente em queries de agregação com resultado de única linha.</p>
     *
     * <p>{@code COALESCE} garante {@code 0.0} quando a tabela está vazia.
     * O {@code Optional} permite tratamento defensivo no service.</p>
     *
     * @return soma total de km, ou {@code Optional.empty()} se a query falhar
     */
    @Query("SELECT COALESCE(SUM(v.kmPercorrida), 0.0) FROM Viagem v")
    Optional<Double> findSomaTotalKm();

    /**
     * Retorna a contagem total de viagens registradas.
     *
     * @return número total de viagens
     */
    @Query("SELECT COUNT(v) FROM Viagem v")
    Long findTotalViagens();

    /**
     * Retorna o volume de viagens agrupado pelo tipo do veículo.
     *
     * <p>Alias SQL {@code tipo_veiculo} → getter {@code getTipoVeiculo()}.</p>
     *
     * @return lista de projeções ordenada por volume decrescente
     */
    @Query(value = """
            SELECT
                ve.tipo      AS tipo_veiculo,
                COUNT(vi.id) AS total_viagens
            FROM viagens vi
            INNER JOIN veiculos ve ON ve.id = vi.veiculo_id
            GROUP BY ve.tipo
            ORDER BY total_viagens DESC
            """, nativeQuery = true)
    List<VolumePorTipoProjection> findVolumePorTipoVeiculo();

    /**
     * Retorna o ranking de veículos ordenado pelo total de KM percorrido (decrescente).
     * Inclui veículos sem nenhuma viagem ({@code LEFT JOIN}) com {@code total_km = 0}.
     *
     * <p>Alias SQL {@code veiculo_id} → getter {@code getVeiculoId()}.</p>
     *
     * @return lista de projeções do ranking completo
     */
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
