package com.logitrack.repository.projection;

import java.math.BigDecimal;

public interface RankingVeiculoProjection {

    Long getVeiculoId();

    String getPlaca();

    String getModelo();

    String getTipo();

    BigDecimal getTotalKm();

    Long getTotalViagens();
}
