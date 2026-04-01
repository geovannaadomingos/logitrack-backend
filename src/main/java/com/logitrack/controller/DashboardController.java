package com.logitrack.controller;

import com.logitrack.dto.dashboard.*;
import com.logitrack.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/total-km")
    public ResponseEntity<TotalKmDTO> getTotalKm() {
        return ResponseEntity.ok(dashboardService.getTotalKm());
    }

    @GetMapping("/volume-por-tipo")
    public ResponseEntity<List<VolumePorTipoDTO>> getVolumePorTipo() {
        return ResponseEntity.ok(dashboardService.getVolumePorTipo());
    }

    @GetMapping("/ranking-veiculos")
    public ResponseEntity<List<RankingVeiculoDTO>> getRankingVeiculos() {
        return ResponseEntity.ok(dashboardService.getRankingVeiculos());
    }

    @GetMapping("/proximas-manutencoes")
    public ResponseEntity<List<ProximaManutencaoDTO>> getProximasManutencoes() {
        return ResponseEntity.ok(dashboardService.getProximasManutencoes());
    }

    @GetMapping("/projecao-custo")
    public ResponseEntity<ProjecaoCustoDTO> getProjecaoCusto() {
        return ResponseEntity.ok(dashboardService.getProjecaoCustoMesAtual());
    }
}
