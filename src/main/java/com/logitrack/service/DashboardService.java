package com.logitrack.service;

import com.logitrack.dto.dashboard.*;
import com.logitrack.repository.ManutencaoRepository;
import com.logitrack.repository.ViagemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço responsável por agregar e retornar as métricas do Dashboard.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ViagemRepository viagemRepository;
    private final ManutencaoRepository manutencaoRepository;

    @Transactional(readOnly = true)
    public TotalKmDTO getTotalKm() {
        log.debug("Calculando total de KM percorrido pela frota");

        Double totalKm = viagemRepository.findSomaTotalKm().orElse(0.0);
        Long totalViagens = viagemRepository.findTotalViagens();

        return TotalKmDTO.builder()
                .totalKm(totalKm)
                .totalViagens(totalViagens != null ? totalViagens : 0L)
                .build();
    }

    @Transactional(readOnly = true)
    public List<VolumePorTipoDTO> getVolumePorTipo() {
        log.debug("Calculando volume de viagens por tipo de veículo");
        return viagemRepository.getVolumePorTipo();
    }

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

    @Transactional(readOnly = true)
    public List<ProximaManutencaoDTO> getProximasManutencoes() {
        log.debug("Buscando próximas 5 manutenções pendentes");

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

    @Transactional(readOnly = true)
    public ProjecaoCustoDTO getProjecaoCustoMesAtual() {
        log.debug("Calculando projeção de custo de manutenção");

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
