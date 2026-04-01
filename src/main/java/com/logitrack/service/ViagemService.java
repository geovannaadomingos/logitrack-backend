package com.logitrack.service;

import com.logitrack.dto.ViagemRequestDTO;
import com.logitrack.dto.ViagemResponseDTO;
import com.logitrack.entity.Veiculo;
import com.logitrack.entity.Viagem;
import com.logitrack.exception.ResourceNotFoundException;
import com.logitrack.mapper.ViagemMapper;
import com.logitrack.repository.VeiculoRepository;
import com.logitrack.repository.ViagemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelas regras de negócio de Viagens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final VeiculoRepository veiculoRepository;
    private final ViagemMapper viagemMapper;

    @Transactional(readOnly = true)
    public Page<ViagemResponseDTO> listarTodas(Long veiculoId, Pageable pageable) {
        log.debug("Listando viagens — veiculoId={}", veiculoId);
        return viagemRepository
                .findAllWithVeiculo(veiculoId, pageable)
                .map(viagemMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ViagemResponseDTO buscarPorId(Long id) {
        log.debug("Buscando viagem ID: {}", id);
        return viagemRepository.findByIdWithVeiculo(id)
                .map(viagemMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem", id));
    }

    @Transactional
    public ViagemResponseDTO criar(ViagemRequestDTO dto) {
        log.debug("Criando viagem para veículo ID: {}", dto.getVeiculoId());

        Veiculo veiculo = findVeiculoOrThrow(dto.getVeiculoId());
        validarDatas(dto);

        Viagem viagem = Viagem.builder()
                .veiculo(veiculo)
                .dataSaida(dto.getDataSaida())
                .dataChegada(dto.getDataChegada())
                .origem(dto.getOrigem())
                .destino(dto.getDestino())
                .kmPercorrida(dto.getKmPercorrida())
                .build();

        Viagem salva = viagemRepository.save(viagem);
        log.info("Viagem criada. ID: {}", salva.getId());

        return viagemMapper.toResponseDTO(salva);
    }

    @Transactional
    public ViagemResponseDTO atualizar(Long id, ViagemRequestDTO dto) {
        log.debug("Atualizando viagem ID: {}", id);

        Viagem viagem = viagemRepository.findByIdWithVeiculo(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem", id));

        Veiculo veiculo = findVeiculoOrThrow(dto.getVeiculoId());
        validarDatas(dto);

        viagem.setVeiculo(veiculo);
        viagemMapper.updateEntityFromDTO(dto, viagem);

        Viagem atualizada = viagemRepository.save(viagem);
        log.info("Viagem ID: {} atualizada", id);

        return viagemMapper.toResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.debug("Deletando viagem ID: {}", id);

        if (!viagemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Viagem", id);
        }

        viagemRepository.deleteById(id);
        log.info("Viagem ID: {} deletada", id);
    }

    private Veiculo findVeiculoOrThrow(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo", id));
    }

    private void validarDatas(ViagemRequestDTO dto) {
        if (dto.getDataChegada() != null && dto.getDataChegada().isBefore(dto.getDataSaida())) {
            throw new IllegalArgumentException("A data de chegada não pode ser anterior à data de saída");
        }
    }
}
