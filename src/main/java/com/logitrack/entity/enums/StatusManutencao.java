package com.logitrack.entity.enums;

/**
 * Status do ciclo de vida de uma manutenção de veículo.
 *
 * <ul>
 *   <li>PENDENTE       – manutenção agendada, ainda não iniciada</li>
 *   <li>EM_REALIZACAO  – manutenção em execução no momento</li>
 *   <li>CONCLUIDA      – manutenção finalizada com sucesso</li>
 * </ul>
 */
public enum StatusManutencao {
    PENDENTE,
    EM_REALIZACAO,
    CONCLUIDA
}
