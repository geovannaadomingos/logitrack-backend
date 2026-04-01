# LogiTrack Pro — Backend API

Sistemas logísticos perdem eficiência quando não há visibilidade sobre a frota.
**LogiTrack Pro** resolve isso com uma API REST que centraliza o registro de viagens e expoe métricas operacionais em tempo real — quilometragem, custo de manutenção, desempenho por veículo — prontas para alimentar dashboards e apoiar decisões de gestão.

---

## Objetivo do Projeto

Este projeto simula o backend de um sistema de gestão de frota para empresas de logística.
O foco está em dois eixos: **operações CRUD** de viagens e **análise de dados** via dashboard.
O dashboard agrega informações como total de KM percorrido, ranking de veículos e projeção de custo de manutenção do mês, permitindo que gestores identifiquem rapidamente veículos de alto uso e custos operacionais elevados.

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

```bash
psql -U postgres -d logitrack -f src/main/resources/db/schema.sql
```

> O script cria as tabelas `veiculos`, `viagens` e `manutencoes`, além de inserir dados de exemplo para teste imediato dos endpoints.

### 3. Configurar credenciais

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/logitrack
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 4. Subir a aplicação

```bash
# Clonar o repositório
git clone <url-do-repositorio>
cd logitrack-backend

# Rodar com Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Ou com Maven instalado localmente
mvn spring-boot:run
```

API disponível em: **`http://localhost:8080`**

---

## Endpoints

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
  "dataChegada": "2026-04-01T14:00:00",
  "origem": "São Paulo, SP",
  "destino": "Campinas, SP",
  "kmPercorrida": 95.50
}
```

**Response — `201 Created`:**
```json
{
  "id": 9,
  "veiculoId": 1,
  "veiculoPlaca": "ABC-1234",
  "veiculoModelo": "Fiat Strada",
  "veiculoTipo": "LEVE",
  "dataSaida": "2026-04-01T08:00:00",
  "dataChegada": "2026-04-01T14:00:00",
  "origem": "São Paulo, SP",
  "destino": "Campinas, SP",
  "kmPercorrida": 95.50
}
```

#### Listagem paginada — `GET /api/v1/viagens`

**Response — `200 OK`:**
```json
{
  "content": [
    {
      "id": 1,
      "veiculoId": 3,
      "veiculoPlaca": "GHI-9012",
      "veiculoModelo": "Mercedes Actros",
      "veiculoTipo": "PESADO",
      "dataSaida": "2026-03-03T06:00:00",
      "dataChegada": "2026-03-04T20:00:00",
      "origem": "São Paulo, SP",
      "destino": "Porto Alegre, RS",
      "kmPercorrida": 1125.00
    }
  ],
  "totalElements": 8,
  "totalPages": 1,
  "number": 0,
  "size": 20,
  "first": true,
  "last": true
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
| `GET` | `/api/v1/dashboard/projecao-custo` | Projeção de custo de manutenção do mês |

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
  { "tipoVeiculo": "PESADO", "totalViagens": 4 },
  { "tipoVeiculo": "LEVE",   "totalViagens": 4 }
]
```

#### `GET /api/v1/dashboard/ranking-veiculos`
```json
[
  { "veiculoId": 4, "placa": "JKL-3456", "modelo": "Volvo FH",        "tipo": "PESADO", "totalKm": 3821.50, "totalViagens": 1 },
  { "veiculoId": 3, "placa": "GHI-9012", "modelo": "Mercedes Actros", "tipo": "PESADO", "totalKm": 1683.30, "totalViagens": 2 },
  { "veiculoId": 1, "placa": "ABC-1234", "modelo": "Fiat Strada",     "tipo": "LEVE",   "totalKm": 505.70,  "totalViagens": 3 }
]
```

#### `GET /api/v1/dashboard/proximas-manutencoes`
```json
[
  {
    "manutencaoId": 1,
    "veiculoId": 1,
    "placa": "ABC-1234",
    "modelo": "Fiat Strada",
    "dataInicio": "2026-04-01",
    "tipoServico": "Troca de óleo",
    "custoEstimado": 250.00,
    "status": "PENDENTE"
  }
]
```
> Status possíveis: `PENDENTE` | `EM_REALIZACAO` | `CONCLUIDA`

#### `GET /api/v1/dashboard/projecao-custo`
```json
{
  "mes": 3,
  "ano": 2026,
  "custoEstimadoTotal": 750.00,
  "totalManutencoes": 1
}
```

---

## Tratamento de Erros

Todos os erros retornam um envelope padronizado:

```json
{
  "timestamp": "2026-04-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Recurso 'Viagem' não encontrado com ID: 99",
  "path": "/api/v1/viagens/99"
}
```

| Cenário | Status |
|---|---|
| Recurso não encontrado | `404 Not Found` |
| Campos inválidos (`@Valid`) | `400 Bad Request` + `fieldErrors` |
| JSON malformado ou tipo inválido | `400 Bad Request` |
| Regra de negócio violada | `400 Bad Request` |
| Erro interno inesperado | `500 Internal Server Error` |

**Exemplo de erro de validação (`400`):**
```json
{
  "timestamp": "2026-04-01T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Há campos inválidos na requisição",
  "path": "/api/v1/viagens",
  "fieldErrors": {
    "kmPercorrida": "O km percorrido é obrigatório",
    "destino": "O destino é obrigatório"
  }
}
```

---

## Decisões Técnicas

### Arquitetura em camadas
O projeto segue a separação `Controller → Service → Repository`, com uma camada de `Mapper` dedicada à conversão entre entidades e DTOs. A conversão não fica no service nem no próprio DTO — fica no mapper, que é um `@Component` Spring injetável e testável. Os DTOs são POJOs puros sem dependência da camada de persistência.

### Prevenção de N+1 com JOIN FETCH
O vínculo `Viagem → Veiculo` é `LAZY` por padrão. Para não gerar uma query extra por viagem retornada, todas as buscas usam `JOIN FETCH` explícito. Na paginação, uma `countQuery` separada é obrigatória — sem ela, o Hibernate aplica `LIMIT/OFFSET` em memória, o que compromete performance em volumes maiores.

### Spring Data Projections no Dashboard
As queries nativas do dashboard retornam **interfaces de projeção tipadas** em vez de `Object[]`. O Spring Data mapeia os aliases SQL (`snake_case`) automaticamente para os getters da interface (`camelCase`), eliminando parsing por índice e erros de cast em tempo de execução.

> **Exceção pontual:** `total-km` usa JPQL escalar (`Optional<Double>`) porque queries agregadas de linha única com projeção nativa podem retornar `null` silenciosamente — gerando resposta `{}` com Jackson `non_null` ativo.

### Validações com Bean Validation
Campos obrigatórios do `ViagemRequestDTO` são validados antes de chegar ao service, via `@Valid`. O `GlobalExceptionHandler` trata `MethodArgumentNotValidException` com mapa de erros por campo e `HttpMessageNotReadableException` (JSON malformado) com `400` em vez de `500` genérico.

### CORS configurável por ambiente
Origens permitidas são definidas via `application.properties` (`logitrack.cors.allowed-origins`), sem alteração de código entre ambientes. Em produção, basta sobrescrever via variável de ambiente ou profile Spring.

---

## Estrutura do Projeto

```
src/main/java/com/logitrack/
├── config/          # CORS (WebConfig)
├── controller/      # ViagemController, DashboardController
├── service/         # ViagemService, DashboardService
├── mapper/          # ViagemMapper (entidade → DTO)
├── repository/      # Repositórios JPA + queries nativas
│   └── projection/  # Interfaces de projeção do dashboard
├── entity/          # Veiculo, Viagem, Manutencao + enums
├── dto/             # DTOs de entrada/saída + dashboard
└── exception/       # ResourceNotFoundException, GlobalExceptionHandler
```

---

## Build para Produção

```bash
mvn clean package -DskipTests
java -jar target/logitrack-backend-1.0.0.jar
```

Para sobrescrever origens CORS em produção:
```bash
java -jar target/logitrack-backend-1.0.0.jar \
  --logitrack.cors.allowed-origins=https://meusite.com.br
```
