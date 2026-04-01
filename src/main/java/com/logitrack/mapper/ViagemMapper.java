package com.logitrack.mapper;

import com.logitrack.dto.ViagemRequestDTO;
import com.logitrack.dto.ViagemResponseDTO;
import com.logitrack.entity.Viagem;
import org.springframework.stereotype.Component;

/**
 * Mapper responsável pela conversão entre a entidade Viagem e seus DTOs.
 */
@Component
public class ViagemMapper {

    /**
     * Converte uma entidade Viagem para o DTO de resposta.
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
     * Atualiza uma entidade existente com os dados do DTO.
     */
    public void updateEntityFromDTO(ViagemRequestDTO dto, Viagem destino) {
        destino.setDataSaida(dto.getDataSaida());
        destino.setDataChegada(dto.getDataChegada());
        destino.setOrigem(dto.getOrigem());
        destino.setDestino(dto.getDestino());
        destino.setKmPercorrida(dto.getKmPercorrida());
    }
}
