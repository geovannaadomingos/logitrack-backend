-- ============================================================
-- LogiTrack Pro — Script de Criação e Seed do Banco de Dados
-- ============================================================
-- Compatível com: PostgreSQL 15+
-- Encoding:       UTF-8
-- Observações:
--   • data_inicio / data_finalizacao são do tipo DATE (sem hora)
--   • status da manutenção: PENDENTE, EM_REALIZACAO, CONCLUIDA
--   • Entidades sem colunas de auditoria (sem created_at/updated_at)
-- ============================================================

-- ------------------------------------------------------------
-- Tabela: veiculos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS veiculos (
    id     BIGSERIAL    PRIMARY KEY,
    placa  VARCHAR(10)  NOT NULL UNIQUE,
    modelo VARCHAR(100) NOT NULL,
    tipo   VARCHAR(20)  NOT NULL CHECK (tipo IN ('LEVE', 'PESADO')),
    ano    INTEGER      NOT NULL CHECK (ano BETWEEN 1900 AND 2100)
);

-- ------------------------------------------------------------
-- Tabela: viagens
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS viagens (
    id            BIGSERIAL      PRIMARY KEY,
    veiculo_id    BIGINT         NOT NULL,
    data_saida    TIMESTAMP      NOT NULL,
    data_chegada  TIMESTAMP,
    origem        VARCHAR(200)   NOT NULL,
    destino       VARCHAR(200)   NOT NULL,
    km_percorrida NUMERIC(10, 2) CHECK (km_percorrida > 0),
    CONSTRAINT fk_viagem_veiculo
        FOREIGN KEY (veiculo_id) REFERENCES veiculos (id)
        ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- Tabela: manutencoes
-- ------------------------------------------------------------
-- Atenção: data_inicio e data_finalizacao são DATE (sem hora)
-- Status:  PENDENTE | EM_REALIZACAO | CONCLUIDA
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS manutencoes (
    id               BIGSERIAL      PRIMARY KEY,
    veiculo_id       BIGINT         NOT NULL,
    data_inicio      DATE           NOT NULL,
    data_finalizacao DATE,
    tipo_servico     VARCHAR(200)   NOT NULL,
    custo_estimado   NUMERIC(10, 2) CHECK (custo_estimado >= 0),
    status           VARCHAR(20)    NOT NULL DEFAULT 'PENDENTE'
                         CHECK (status IN ('PENDENTE', 'EM_REALIZACAO', 'CONCLUIDA')),
    CONSTRAINT fk_manutencao_veiculo
        FOREIGN KEY (veiculo_id) REFERENCES veiculos (id)
        ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- Índices para performance
-- ------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_viagens_veiculo_id ON viagens (veiculo_id);
CREATE INDEX IF NOT EXISTS idx_viagens_data_saida ON viagens (data_saida);
CREATE INDEX IF NOT EXISTS idx_manutencoes_veiculo_id ON manutencoes (veiculo_id);
CREATE INDEX IF NOT EXISTS idx_manutencoes_status ON manutencoes (status);
CREATE INDEX IF NOT EXISTS idx_manutencoes_data_inicio ON manutencoes (data_inicio);

-- ============================================================
-- Dados de Exemplo (Seed)
-- ============================================================

INSERT INTO veiculos (placa, modelo, tipo, ano) VALUES
    ('ABC-1234', 'Fiat Strada',        'LEVE',   2022),
    ('DEF-5678', 'Volkswagen Gol',     'LEVE',   2021),
    ('GHI-9012', 'Mercedes Actros',    'PESADO', 2020),
    ('JKL-3456', 'Volvo FH',           'PESADO', 2023),
    ('MNO-7890', 'Renault Kangoo',     'LEVE',   2022)
ON CONFLICT (placa) DO NOTHING;

INSERT INTO viagens (veiculo_id, data_saida, data_chegada, origem, destino, km_percorrida) VALUES
    (1, '2026-03-01 08:00', '2026-03-01 14:00', 'São Paulo, SP',    'Campinas, SP',       95.50),
    (1, '2026-03-05 09:00', '2026-03-05 18:00', 'Campinas, SP',     'Ribeirão Preto, SP', 310.20),
    (2, '2026-03-02 07:00', '2026-03-02 10:00', 'São Paulo, SP',    'Santos, SP',          72.80),
    (3, '2026-03-03 06:00', '2026-03-04 20:00', 'São Paulo, SP',    'Porto Alegre, RS',  1125.00),
    (3, '2026-03-10 06:00', '2026-03-11 22:00', 'Porto Alegre, RS', 'Curitiba, PR',       558.30),
    (4, '2026-03-07 05:00', '2026-03-09 19:00', 'Curitiba, PR',     'Manaus, AM',        3821.50),
    (5, '2026-03-15 08:30', '2026-03-15 12:00', 'São Paulo, SP',    'Sorocaba, SP',       100.00),
    (1, '2026-03-20 07:00', '2026-03-20 15:00', 'Sorocaba, SP',     'São Paulo, SP',      100.00);

-- status: PENDENTE, EM_REALIZACAO, CONCLUIDA
-- data_inicio e data_finalizacao: tipo DATE
INSERT INTO manutencoes (veiculo_id, data_inicio, data_finalizacao, tipo_servico, custo_estimado, status) VALUES
    -- Futuras (aparecerão no dashboard de próximas manutenções)
    (1, '2026-04-01', NULL,         'Troca de óleo',               250.00, 'PENDENTE'),
    (2, '2026-04-02', NULL,         'Revisão geral',               800.00, 'PENDENTE'),
    (3, '2026-04-05', NULL,         'Troca de pneus',             2500.00, 'PENDENTE'),
    (4, '2026-04-10', NULL,         'Alinhamento e balanceamento', 350.00, 'PENDENTE'),
    (5, '2026-04-15', NULL,         'Revisão de freios',           600.00, 'PENDENTE'),
    -- Em realização (mês corrente — aparecerão na projeção de custo)
    (2, '2026-03-25', NULL,         'Manutenção elétrica',         750.00, 'EM_REALIZACAO'),
    -- Concluídas (históricas)
    (1, '2026-03-01', '2026-03-01', 'Troca de correia',            450.00, 'CONCLUIDA'),
    (3, '2026-03-10', '2026-03-12', 'Revisão completa',           3200.00, 'CONCLUIDA');
