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
 * Serviço responsável pelas regras de negócio relacionadas a Viagens.
 *
 * <h3>Mapeamento via ViagemMapper</h3>
 * <p>A conversão de entidade para DTO é delegada ao {@link ViagemMapper},
 * injetado como dependência Spring. O service apenas orquestra regras de
 * negócio e persistência — sem lógica de mapeamento embutida.</p>
 *
 * <h3>Paginação</h3>
 * <p>O método {@link #listarTodas(Long, Pageable)} expõe paginação completa via
 * Spring Data {@link Pageable}, com filtro opcional por veículo.</p>
 *
 * <h3>Estratégia contra LazyInitializationException</h3>
 * <p>Todos os métodos de leitura utilizam queries com {@code JOIN FETCH} no
 * repositório, garantindo que o veículo esteja inicializado antes de o
 * {@link ViagemMapper} acessar {@code viagem.getVeiculo()}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final VeiculoRepository veiculoRepository;
    private final ViagemMapper viagemMapper;

    /**
     * Retorna uma página de viagens com filtro opcional por veículo.
     *
     * @param veiculoId filtro opcional — retorna apenas viagens do veículo informado; {@code null} retorna tudo
     * @param pageable  parâmetros de paginação ({@code page}, {@code size}, {@code sort})
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ViagemResponseDTO> listarTodas(Long veiculoId, Pageable pageable) {
        log.debug("Listando viagens — veiculoId={}, page={}, size={}, sort={}",
                veiculoId, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        return viagemRepository
                .findAllWithVeiculo(veiculoId, pageable)
                .map(viagemMapper::toResponseDTO);
    }

    /**
     * Busca uma viagem pelo ID.
     *
     * @param id identificador da viagem
     * @return DTO com os dados da viagem e do veículo associado
     * @throws ResourceNotFoundException se nenhuma viagem existir com o ID informado
     */
    @Transactional(readOnly = true)
    public ViagemResponseDTO buscarPorId(Long id) {
        log.debug("Buscando viagem com ID: {}", id);

        return viagemRepository.findByIdWithVeiculo(id)
                .map(viagemMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem", id));
    }

    /**
     * Cria uma nova viagem associada ao veículo informado.
     *
     * @param dto dados da nova viagem
     * @return DTO da viagem criada
     * @throws ResourceNotFoundException se o veículo informado não existir
     * @throws IllegalArgumentException  se a data de chegada for anterior à data de saída
     */
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
        log.info("Viagem criada com sucesso. ID: {}", salva.getId());

        // O contexto de persistência mantém o veiculo gerenciado nesta transação
        return viagemMapper.toResponseDTO(salva);
    }

    /**
     * Atualiza completamente uma viagem existente.
     *
     * <p>Utiliza {@link ViagemMapper#updateEntityFromDTO} para aplicar os dados
     * do DTO sobre a entidade existente, evitando repetição dos setters no service.</p>
     *
     * @param id  identificador da viagem a ser atualizada
     * @param dto novos dados da viagem
     * @return DTO da viagem atualizada
     * @throws ResourceNotFoundException se a viagem ou o veículo não existirem
     * @throws IllegalArgumentException  se as datas forem inválidas
     */
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
        log.info("Viagem ID: {} atualizada com sucesso", id);

        return viagemMapper.toResponseDTO(atualizada);
    }

    /**
     * Remove uma viagem pelo ID.
     *
     * @param id identificador da viagem a ser removida
     * @throws ResourceNotFoundException se nenhuma viagem existir com o ID informado
     */
    @Transactional
    public void deletar(Long id) {
        log.debug("Deletando viagem ID: {}", id);

        if (!viagemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Viagem", id);
        }

        viagemRepository.deleteById(id);
        log.info("Viagem ID: {} deletada com sucesso", id);
    }

    // ===================================================================
    // Private helpers
    // ===================================================================

    private Veiculo findVeiculoOrThrow(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo", id));
    }

    private void validarDatas(ViagemRequestDTO dto) {
        if (dto.getDataChegada() != null && dto.getDataChegada().isBefore(dto.getDataSaida())) {
            throw new IllegalArgumentException(
                    "A data de chegada não pode ser anterior à data de saída"
            );
        }
    }
}
