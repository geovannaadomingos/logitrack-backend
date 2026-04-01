package com.logitrack.mapper;

import com.logitrack.dto.ViagemRequestDTO;
import com.logitrack.dto.ViagemResponseDTO;
import com.logitrack.entity.Viagem;
import org.springframework.stereotype.Component;

/**
 * Mapper responsável pela conversão entre a entidade {@link Viagem} e seus DTOs.
 *
 * <h3>Por que um Mapper separado?</h3>
 * <p>Colocar a lógica de mapeamento dentro do próprio DTO (ex.: método estático
 * {@code from()}) acopla o DTO à entidade, mistura responsabilidades e dificulta
 * testes isolados. Um {@code @Component} Spring separado:</p>
 * <ul>
 *   <li>Pode ser injetado por DI e substituído ou decorado em testes</li>
 *   <li>Mantém DTOs como <em>POJOs puros</em> (sem referências à camada de persistência)</li>
 *   <li>Centraliza toda a lógica de conversão em um único lugar</li>
 *   <li>Facilita a futura adoção de MapStruct sem alterar callers</li>
 * </ul>
 *
 * <h3>Pré-requisito de carregamento</h3>
 * <p>O método {@link #toResponseDTO(Viagem)} acessa {@code viagem.getVeiculo()},
 * que é um relacionamento {@code LAZY}. Este mapper <strong>só deve ser invocado
 * dentro de uma transação ativa</strong> ou quando o veículo já foi carregado
 * via {@code JOIN FETCH} (como garantem as queries do {@code ViagemRepository}).</p>
 */
@Component
public class ViagemMapper {

    /**
     * Converte uma entidade {@link Viagem} para o DTO de resposta da API.
     *
     * <p>Os dados do veículo (id, placa, modelo, tipo) são achatados no DTO
     * para evitar estruturas aninhadas — o consumidor da API não precisa fazer
     * uma segunda requisição para obter os dados do veículo.</p>
     *
     * @param viagem entidade com o veículo obrigatoriamente inicializado
     * @return DTO pronto para serialização JSON
     * @throws org.hibernate.LazyInitializationException se o veículo não estiver carregado
     */
    public ViagemResponseDTO toResponseDTO(Viagem viagem) {
        return ViagemResponseDTO.builder()
                .id(viagem.getId())
                .veiculoId(viagem.getVeiculo().getId())
                .veiculoPlaca(viagem.getVeiculo().getPlaca())
                .veiculoModelo(viagem.getVeiculo().getModelo())
                .veiculoTipo(viagem.getVeiculo().getTipo().name())
                .dataSaida(viagem.getDataSaida())
                .dataChegada(viagem.getDataChegada())
                .origem(viagem.getOrigem())
                .destino(viagem.getDestino())
                .kmPercorrida(viagem.getKmPercorrida())
                .build();
    }

    /**
     * Aplica os dados de um {@link ViagemRequestDTO} sobre uma entidade existente.
     *
     * <p>Útil no fluxo de atualização ({@code PUT}) para evitar criar uma nova
     * entidade e re-setar todos os campos manualmente no service.</p>
     *
     * <p><strong>Nota:</strong> o campo {@code veiculo} <em>não</em> é atualizado
     * aqui pois requer uma busca ao banco — essa responsabilidade fica no service.</p>
     *
     * @param dto     dados atualizados recebidos do cliente
     * @param destino entidade existente a ser mutada
     */
    public void updateEntityFromDTO(ViagemRequestDTO dto, Viagem destino) {
        destino.setDataSaida(dto.getDataSaida());
        destino.setDataChegada(dto.getDataChegada());
        destino.setOrigem(dto.getOrigem());
        destino.setDestino(dto.getDestino());
        destino.setKmPercorrida(dto.getKmPercorrida());
    }
}
