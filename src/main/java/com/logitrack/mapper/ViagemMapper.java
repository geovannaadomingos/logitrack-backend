package com.logitrack.mapper;

import com.logitrack.dto.ViagemRequestDTO;
import com.logitrack.dto.ViagemResponseDTO;
import com.logitrack.entity.Viagem;
import org.springframework.stereotype.Component;

@Component
public class ViagemMapper {

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

    public void updateEntityFromDTO(ViagemRequestDTO dto, Viagem destino) {
        destino.setDataSaida(dto.getDataSaida());
        destino.setDataChegada(dto.getDataChegada());
        destino.setOrigem(dto.getOrigem());
        destino.setDestino(dto.getDestino());
        destino.setKmPercorrida(dto.getKmPercorrida());
    }
}
