package com.logitrack.controller;

import com.logitrack.dto.ViagemRequestDTO;
import com.logitrack.dto.ViagemResponseDTO;
import com.logitrack.service.ViagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de Viagens.
 * Rota base: /api/v1/viagens
 */
@RestController
@RequestMapping("/api/v1/viagens")
@RequiredArgsConstructor
public class ViagemController {

    private final ViagemService viagemService;

    @GetMapping
    public ResponseEntity<Page<ViagemResponseDTO>> listarTodas(
            @RequestParam(required = false) Long veiculoId,
            @PageableDefault(size = 20, sort = "dataSaida", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(viagemService.listarTodas(veiculoId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(viagemService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ViagemResponseDTO> criar(@Valid @RequestBody ViagemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viagemService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ViagemRequestDTO dto) {

        return ResponseEntity.ok(viagemService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        viagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
