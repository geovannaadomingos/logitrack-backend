package com.logitrack.controller;

import com.logitrack.dto.dashboard.*;
import com.logitrack.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para os endpoints do Dashboard de métricas.
 * Rota base: /api/v1/dashboard
 *
 * Todos os endpoints são somente leitura (GET).
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/v1/dashboard/total-km
     * Retorna o total de KM percorrido e volume de viagens da frota inteira.
     *
     * Exemplo de resposta:
     * {
     *   "totalKm": 6183.30,
     *   "totalViagens": 8
     * }
     */
    @GetMapping("/total-km")
    public ResponseEntity<TotalKmDTO> getTotalKm() {
        return ResponseEntity.ok(dashboardService.getTotalKm());
    }

    /**
     * GET /api/v1/dashboard/volume-por-tipo
     * Retorna o volume de viagens agrupado por tipo de veículo.
     *
     * Exemplo de resposta:
     * [
     *   { "tipoVeiculo": "PESADO", "totalViagens": 4 },
     *   { "tipoVeiculo": "LEVE",   "totalViagens": 4 }
     * ]
     */
    @GetMapping("/volume-por-tipo")
    public ResponseEntity<List<VolumePorTipoDTO>> getVolumePorTipo() {
        return ResponseEntity.ok(dashboardService.getVolumePorTipo());
    }

    /**
     * GET /api/v1/dashboard/ranking-veiculos
     * Retorna os veículos ordenados por total de KM percorrido (desc).
     *
     * Exemplo de resposta:
     * [
     *   { "veiculoId": 4, "placa": "JKL-3456", "modelo": "Volvo FH", "tipo": "PESADO", "totalKm": 3821.50, "totalViagens": 1 },
     *   ...
     * ]
     */
    @GetMapping("/ranking-veiculos")
    public ResponseEntity<List<RankingVeiculoDTO>> getRankingVeiculos() {
        return ResponseEntity.ok(dashboardService.getRankingVeiculos());
    }

    /**
     * GET /api/v1/dashboard/proximas-manutencoes
     * Retorna as próximas 5 manutenções agendadas ou em andamento.
     *
     * Exemplo de resposta:
     * [
     *   { "manutencaoId": 1, "placa": "ABC-1234", "tipoServico": "Troca de óleo", ... },
     *   ...
     * ]
     */
    @GetMapping("/proximas-manutencoes")
    public ResponseEntity<List<ProximaManutencaoDTO>> getProximasManutencoes() {
        return ResponseEntity.ok(dashboardService.getProximasManutencoes());
    }

    /**
     * GET /api/v1/dashboard/projecao-custo
     * Retorna a projeção de custo total de manutenção do mês atual.
     *
     * Exemplo de resposta:
     * {
     *   "mes": 3,
     *   "ano": 2026,
     *   "custoEstimadoTotal": 750.00,
     *   "totalManutencoes": 1
     * }
     */
    @GetMapping("/projecao-custo")
    public ResponseEntity<ProjecaoCustoDTO> getProjecaoCusto() {
        return ResponseEntity.ok(dashboardService.getProjecaoCustoMesAtual());
    }
}
