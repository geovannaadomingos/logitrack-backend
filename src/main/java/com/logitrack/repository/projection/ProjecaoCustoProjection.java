package com.logitrack.repository.projection;

import java.math.BigDecimal;

public interface ProjecaoCustoProjection {

    Integer getMes();

    Integer getAno();

    BigDecimal getCustoEstimadoTotal();

    Long getTotalManutencoes();
}
