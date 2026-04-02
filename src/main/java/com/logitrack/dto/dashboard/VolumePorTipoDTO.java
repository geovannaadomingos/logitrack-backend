package com.logitrack.dto.dashboard;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumePorTipoDTO {

    private String tipo;

    private Long volume;
}
