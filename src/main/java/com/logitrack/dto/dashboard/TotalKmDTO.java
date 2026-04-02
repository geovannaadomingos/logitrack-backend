package com.logitrack.dto.dashboard;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalKmDTO {

    @Builder.Default
    private Double totalKm = 0.0;

    @Builder.Default
    private Long totalViagens = 0L;
}
