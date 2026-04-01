package com.logitrack.service;

import com.logitrack.dto.dashboard.*;
import com.logitrack.repository.ManutencaoRepository;
import com.logitrack.repository.ViagemRepository;
import com.logitrack.repository.projection.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço responsável por agregar e retornar as métricas do Dashboard.
 *
 * <h3>Uso de Projections Spring Data</h3>
 * <p>Todas as queries nativas retornam interfaces de projeção tipadas — sem
 * {@code Object[]}, sem parsing manual por índice. O framework mapeia os aliases
 * SQL diretamente para os getters das projeções, garantindo segurança de tipos
 * em tempo de compilação e eliminando a classe inteira de erros de índice errado.</p>
 *
 * <h3>Mapeamento Projection → DTO</h3>
 * <p>As projeções são uma abstração da camada de persistência e não devem vazar
 * para os controllers. Por isso, cada método converte os dados da projeção para
 * o DTO correspondente antes de retornar.</p>
 *
 * <h3>Todas as operações são somente leitura</h3>
 * <p>{@code @Transactional(readOnly = true)} em todos os métodos — sem escrita.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ViagemRepository viagemRepository;
    private final ManutencaoRepository manutencaoRepository;

    /**
     * Retorna o total de KM percorrido e o volume total de viagens da frota.
     *
     * <h3>Por que não usa projeção nativa aqui?</h3>
     * <p>Queries nativas com projeções de linha única ({@code SELECT SUM...}) retornam
     * um proxy Hibernate cujos getters podem produzir {@code null} silenciosamente
     * quando o mapeamento de tipo falha. Com {@code spring.jackson.default-property-inclusion=non_null},
     * campos nulos são omitidos pelo Jackson, causando o retorno de {@code {}}.</p>
     *
     * <p>A solução é usar <strong>JPQL com retorno escalar:</strong>
     * {@code Optional<Double>} e {@code Long} são mapeados diretamente pelo
     * Hibernate sem camada de proxy — garantindo valores válidos ou fallback
     * explícito via {@code orElse(0.0)}.</p>
     *
     * @return DTO com {@code totalKm} e {@code totalViagens} — nunca nulo, nunca {@code {}}
     */
    @Transactional(readOnly = true)
    public TotalKmDTO getTotalKm() {
        log.debug("Calculando total de KM percorrido pela frota");

        Double totalKm = viagemRepository.findSomaTotalKm().orElse(0.0);
        Long totalViagens = viagemRepository.findTotalViagens();

        log.debug("Total KM={}, Total viagens={}", totalKm, totalViagens);

        return TotalKmDTO.builder()
                .totalKm(totalKm)
                .totalViagens(totalViagens != null ? totalViagens : 0L)
                .build();
    }


    /**
     * Retorna o volume de viagens agrupado por tipo de veículo (LEVE / PESADO).
     *
     * @return lista de DTOs com {@code tipoVeiculo} e {@code totalViagens},
     *         ordenada por volume decrescente
     */
    @Transactional(readOnly = true)
    public List<VolumePorTipoDTO> getVolumePorTipo() {
        log.debug("Calculando volume de viagens por tipo de veículo");

        return viagemRepository.findVolumePorTipoVeiculo()
                .stream()
                .map(p -> VolumePorTipoDTO.builder()
                        .tipoVeiculo(p.getTipoVeiculo())
                        .totalViagens(p.getTotalViagens())
                        .build())
                .toList();
    }

    /**
     * Retorna o ranking de veículos ordenado pelo total de KM percorrido (decrescente).
     * Inclui veículos sem viagens com {@code totalKm = 0}.
     *
     * @return lista de DTOs do ranking completo
     */
    @Transactional(readOnly = true)
    public List<RankingVeiculoDTO> getRankingVeiculos() {
        log.debug("Calculando ranking de veículos por KM");

        return viagemRepository.findRankingVeiculosPorKm()
                .stream()
                .map(p -> RankingVeiculoDTO.builder()
                        .veiculoId(p.getVeiculoId())
                        .placa(p.getPlaca())
                        .modelo(p.getModelo())
                        .tipo(p.getTipo())
                        .totalKm(p.getTotalKm())
                        .totalViagens(p.getTotalViagens())
                        .build())
                .toList();
    }

    /**
     * Retorna as próximas 5 manutenções com status {@code PENDENTE} ou
     * {@code EM_REALIZACAO}, ordenadas pela data de início mais próxima.
     *
     * @return lista de até 5 DTOs com informações da manutenção e do veículo
     */
    @Transactional(readOnly = true)
    public List<ProximaManutencaoDTO> getProximasManutencoes() {
        log.debug("Buscando próximas 5 manutenções pendentes ou em realização");

        return manutencaoRepository.findProximasManutencoes()
                .stream()
                .map(p -> ProximaManutencaoDTO.builder()
                        .manutencaoId(p.getManutencaoId())
                        .veiculoId(p.getVeiculoId())
                        .placa(p.getPlaca())
                        .modelo(p.getModelo())
                        .dataInicio(p.getDataInicio())
                        .tipoServico(p.getTipoServico())
                        .custoEstimado(p.getCustoEstimado())
                        .status(p.getStatus())
                        .build())
                .toList();
    }

    /**
     * Retorna a projeção de custo total de manutenção para o mês corrente.
     *
     * <p>Os campos {@code mes} e {@code ano} são retornados como {@link Integer}
     * diretamente da projeção — sem conversão extra — graças ao
     * {@code CAST(EXTRACT(...) AS INTEGER)} na query SQL.</p>
     *
     * @return DTO com mês, ano, custo estimado total e quantidade de manutenções
     */
    @Transactional(readOnly = true)
    public ProjecaoCustoDTO getProjecaoCustoMesAtual() {
        log.debug("Calculando projeção de custo de manutenção do mês atual");

        return manutencaoRepository.findProjecaoCustoMesAtual()
                .map(p -> ProjecaoCustoDTO.builder()
                        .mes(p.getMes())
                        .ano(p.getAno())
                        .custoEstimadoTotal(p.getCustoEstimadoTotal())
                        .totalManutencoes(p.getTotalManutencoes())
                        .build())
                .orElseGet(() -> ProjecaoCustoDTO.builder()
                        .mes(0)
                        .ano(0)
                        .custoEstimadoTotal(BigDecimal.ZERO)
                        .totalManutencoes(0L)
                        .build());
    }
}
