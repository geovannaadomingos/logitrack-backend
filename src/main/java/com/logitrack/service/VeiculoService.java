package com.logitrack.service;

import com.logitrack.dto.VeiculoResponseDTO;
import com.logitrack.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public List<VeiculoResponseDTO> listarTodos() {
        log.debug("Listando todos os veículos");
        return veiculoRepository.findAll().stream()
                .map(v -> VeiculoResponseDTO.builder()
                        .id(v.getId())
                        .placa(v.getPlaca())
                        .modelo(v.getModelo())
                        .build())
                .collect(Collectors.toList());
    }
}
