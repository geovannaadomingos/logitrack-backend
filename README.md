# LogiTrack Pro — Backend API

Sistemas logísticos perdem eficiência quando não há visibilidade sobre a frota.
**LogiTrack Pro** resolve isso com uma API REST que centraliza o registro de viagens, o gerenciamento básico da frota e expõe métricas operacionais em tempo real — quilometragem, custo de manutenção, desempenho por veículo — prontas para alimentar dashboards e apoiar decisões de gestão.

---

## Objetivo do Projeto

Este projeto simula o backend de um sistema de gestão de frota para empresas de logística.
O foco está em três eixos: **gerenciamento de frota** (veículos), **operações CRUD** de viagens e **análise de dados** via dashboard.
O dashboard agrega informações como total de KM percorrido, ranking de veículos e projeção de custo de manutenção, permitindo que gestores identifiquem rapidamente veículos de alto uso e custos operacionais elevados.

---

## Regras de Negócio

Para garantir a integridade dos dados e a precisão dos cálculos do dashboard, as seguintes regras são aplicadas:

- **KM percorrido**: Deve ser obrigatoriamente maior que zero (`> 0.00`).
- **Consistência Cronológica**: A data de saída deve ser sempre anterior à data de chegada.
- **Integridade de Referência**: Um veículo válido deve existir no sistema para que uma viagem seja registrada.
- **Campos Mandatórios**: Dados de origem, destino e datas são validados na entrada (DTO).

---

## Arquitetura

O projeto segue arquitetura em camadas:

**Controller → Service → Repository → Database**

- **Controller**: expõe endpoints REST e gerencia as requisições.
- **Service**: contém as regras de negócio e orquestração.
- **Repository**: abstração de acesso ao banco de dados (Spring Data JPA).
- **DTOs**: garantem o contrato de dados seguro entre backend e frontend.

---

## Fluxo da Requisição

O caminho de um dado desde a chamada da API até o banco segue este fluxo organizado:

1. **Controller**: Recebe a requisição HTTP e valida a estrutura do DTO.
2. **Service**: Aplica as regras de negócio e validações lógicas.
3. **Repository**: Interage com o PostgreSQL via Spring Data JPA.
4. **DTO**: O dado processado é mapeado e retornado ao cliente com segurança.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.4 |
| Spring Data JPA | — |
| PostgreSQL | 15+ |
| Lombok | — |
| Jakarta Validation | — |
| Maven | 3.9+ |

---

## Variáveis de Ambiente

Para flexibilidade em diferentes ambientes (Dev, Staging, Prod), a aplicação utiliza as seguintes variáveis:

| Variável | Descrição |
|----------|-----------|
| `SPRING_DATASOURCE_URL` | URL de conexão do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `PORT` | Porta de execução (padrão 8080) |

> O projeto suporta fallback automático para valores locais via `application.properties`.

---

## Como Rodar

### Pré-requisitos

- [Java 17+](https://adoptium.net/) — verificar: `java -version`
- [Maven 3.9+](https://maven.apache.org/download.cgi) — verificar: `mvn -version`
- [PostgreSQL 15+](https://www.postgresql.org/download/) — verificar: `pg_isready`

### 1. Criar o banco de dados

```sql
CREATE DATABASE logitrack;
```

### 2. Executar o schema (tabelas + dados de exemplo)

Para garantir que caracteres acentuados (como "Manutenção") sejam importados corretamente, forçamos o encoding do cliente:

```bash
# No Windows PowerShell
$env:PGCLIENTENCODING='UTF8'; psql -U postgres -d logitrack -f src/main/resources/db/schema.sql
```

> O script cria as tabelas `veiculos`, `viagens` e `manutencoes`, além de inserir dados de exemplo para teste imediato dos endpoints.

### 3. Configurar credenciais

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/logitrack?charSet=UTF8
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 4. Subir a aplicação

No Windows, é **obrigatório** forçar o encoding da JVM para UTF-8 para evitar caracteres corrompidos na API:

```bash
# No Windows PowerShell
$env:MAVEN_OPTS="-Dfile.encoding=UTF-8"; mvn spring-boot:run
```

API disponível em: **`http://localhost:8080`**

---

## Endpoints

### Veículos — `/api/v1/veiculos`

| Método | Rota | Descrição | Status de sucesso |
|---|---|---|---|
| `GET` | `/api/v1/veiculos` | Lista todos os veículos da frota (id, placa, modelo) | `200 OK` |

#### `GET /api/v1/veiculos`
```json
[
  {
    "id": 1,
    "placa": "ABC-1234",
    "modelo": "Fiat Strada"
  }
]
```

### Viagens — `/api/v1/viagens`

| Método | Rota | Descrição | Status de sucesso |
|---|---|---|---|
| `GET` | `/api/v1/viagens` | Lista viagens com paginação | `200 OK` |
| `GET` | `/api/v1/viagens/{id}` | Busca viagem por ID | `200 OK` |
| `POST` | `/api/v1/viagens` | Cria nova viagem | `201 Created` |
| `PUT` | `/api/v1/viagens/{id}` | Atualiza viagem existente | `200 OK` |
| `DELETE` | `/api/v1/viagens/{id}` | Remove viagem | `204 No Content` |

#### Paginação e filtros (`GET /api/v1/viagens`)

| Parâmetro | Tipo | Padrão | Descrição |
|---|---|---|---|
| `page` | `int` | `0` | Número da página (zero-indexed) |
| `size` | `int` | `20` | Itens por página |
| `sort` | `string` | `dataSaida,desc` | Campo e direção (ex: `kmPercorrida,asc`) |
| `veiculoId` | `Long` | — | Filtro opcional por veículo |

#### Criar viagem — `POST /api/v1/viagens`

**Request:**
```json
{
  "veiculoId": 1,
  "dataSaida": "2026-04-01T08:00:00",
  "dataChegada": "2026-04-01T18:00:00",
  "origem": "São Paulo, SP",
  "destino": "Campinas, SP",
  "kmPercorrida": 95.50
}
```

---

### Dashboard — `/api/v1/dashboard`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/v1/dashboard/total-km` | KM total percorrido pela frota |
| `GET` | `/api/v1/dashboard/volume-por-tipo` | Viagens agrupadas por tipo de veículo |
| `GET` | `/api/v1/dashboard/ranking-veiculos` | Veículos ordenados por KM percorrido |
| `GET` | `/api/v1/dashboard/proximas-manutencoes` | Próximas 5 manutenções pendentes |
| `GET` | `/api/v1/dashboard/projecao-custo` | Projeção total de custo de manutenção acumulado |

#### `GET /api/v1/dashboard/total-km`
```json
{
  "totalKm": 6183.3,
  "totalViagens": 8
}
```

#### `GET /api/v1/dashboard/volume-por-tipo`
```json
[
  { "tipo": "PESADO", "volume": 4 },
  { "tipo": "LEVE",   "volume": 4 }
]
```

#### `GET /api/v1/dashboard/ranking-veiculos`
```json
[
  { "placa": "JKL-3456", "modelo": "Volvo FH",        "tipo": "PESADO", "totalKm": 3821.50 },
  { "placa": "GHI-9012", "modelo": "Mercedes Actros", "tipo": "PESADO", "totalKm": 1125.00 }
]
```

#### `GET /api/v1/dashboard/proximas-manutencoes`
```json
[
  {
    "placa": "ABC-1234",
    "modelo": "Fiat Strada",
    "data": "01/04/2026",
    "servico": "Troca de óleo"
  }
]
```

#### `GET /api/v1/dashboard/projecao-custo`
```json
{
  "total": 4050.00
}
```

---

## Integração com Frontend

A API foi desenhada para ser consumida de forma transparente pelo frontend do LogiTrack (React).

- **Base URL padrão**: `http://localhost:8080/api/v1`
- **CORS**: Configurado para permitir comunicações seguras entre os módulos.

---

## Tratamento de Erros

Todos os erros retornam um envelope padronizado:

```json
{
  "timestamp": "2026-04-01T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Há campos inválidos na requisição",
  "path": "/api/v1/viagens",
  "fieldErrors": {
    "kmPercorrida": "O km percorrido deve ser maior que zero"
  }
}
```

---

## Possíveis Problemas e Soluções

#### Problema: Caracteres quebrados (acentuação)
- **Causa**: Encoding padrão do Windows/JVM diferente de UTF-8.
- **Solução**: Certifique-se de rodar com a flag `-Dfile.encoding=UTF-8`.

#### Problema: O banco de dados não conecta
- **Checklist**:
  - Verifique se o PostgreSQL está rodando (`pg_isready`).
  - Valide as credenciais em `SPRING_DATASOURCE_USERNAME` e `PASSWORD`.
  - Verifique se o banco `logitrack` foi criado.

---

## Decisões Técnicas

### Arquitetura em camadas
O projeto segue a separação `Controller → Service → Repository`, isolando a lógica de apresentação da persistência.

### Suporte total a UTF-8
Para evitar corrupção de caracteres especiais (acentuação) em ambientes Windows, o projeto força UTF-8 em três níveis:
1. **Maven**: `project.build.sourceEncoding` no pom.xml.
2. **JVM**: `-Dfile.encoding=UTF-8` na inicialização do serviço.
3. **JDBC**: parâmetro `charSet=UTF8` na string de conexão com o PostgreSQL.

### JPQL Constructor Expressions no Dashboard
O dashboard utiliza `SELECT new com.logitrack.dto.dashboard...` para garantir que o DTO seja instanciado com os tipos exatos vindos da camada de persistência, otimizando o mapeamento.

### Prevenção de N+1
Consultas de listagem utilizam `JOIN FETCH` explícito para carregar o Veículo associado à Viagem em uma única instrução SQL.

---


## Deploy

Para rodar o backend em ambiente de produção ou simular a execução final:

### Execução via JAR

1. Gere o artefato:
```bash
mvn clean package -DskipTests
```

2. Execute o JAR forçando o encoding:
```bash
java -Dfile.encoding=UTF-8 -jar target/*.jar
```

### Variáveis obrigatórias em produção

Ao realizar o deploy em ambientes cloud (Railway, Render, AWS), configure as seguintes variáveis:

| Variável | Exemplo |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://db-host:5432/logitrack` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `suasenha123` |
| `PORT` | `8080` |

---

## Docker (Opcional)

O projeto está preparado para ser containerizado, facilitando a orquestração e garantindo paridade total entre os ambientes de desenvolvimento e produção.

---

## Testes

A aplicação utiliza as melhores práticas de testes do ecossistema Spring:

- **Spring Boot Test**: Para testes de integração e contexto.
- **JUnit 5 / Mockito**: Para testes unitários de regras de negócio.

Para executar os testes:
```bash
mvn test
```
---
