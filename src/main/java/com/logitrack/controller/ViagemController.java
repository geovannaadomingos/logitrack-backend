package com.logitrack.controller;

import com.logitrack.dto.ViagemRequestDTO;
import com.logitrack.dto.ViagemResponseDTO;
import com.logitrack.service.ViagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de CRUD sobre Viagens.
 *
 * <p>Rota base: {@code /api/v1/viagens}</p>
 *
 * <h3>Paginação</h3>
 * <p>O endpoint de listagem ({@code GET /api/v1/viagens}) aceita os parâmetros
 * padrão do Spring Data Web:</p>
 * <ul>
 *   <li>{@code page}  — número da página, zero-indexed (padrão: {@code 0})</li>
 *   <li>{@code size}  — quantidade de itens por página (padrão: {@code 20})</li>
 *   <li>{@code sort}  — campo e direção de ordenação (padrão: {@code dataSaida,desc})</li>
 * </ul>
 *
 * <p>Exemplos de uso:</p>
 * <pre>
 * GET /api/v1/viagens
 * GET /api/v1/viagens?page=1&size=10
 * GET /api/v1/viagens?sort=kmPercorrida,desc
 * GET /api/v1/viagens?veiculoId=3&size=5&sort=dataSaida,asc
 * </pre>
 *
 * <h3>Pré-requisito para paginação</h3>
 * <p>O Spring Boot habilita automaticamente a resolução de {@link Pageable} via
 * {@code @EnableSpringDataWebSupport} quando {@code spring-boot-starter-web} está
 * no classpath — nenhuma configuração adicional é necessária.</p>
 */
@RestController
@RequestMapping("/api/v1/viagens")
@RequiredArgsConstructor
public class ViagemController {

    private final ViagemService viagemService;

    /**
     * {@code GET /api/v1/viagens}
     *
     * <p>Lista viagens com paginação e filtro opcional por veículo.</p>
     *
     * @param veiculoId filtro opcional — retorna apenas viagens do veículo informado
     * @param pageable  parâmetros de paginação ({@code page}, {@code size}, {@code sort})
     *                  resolvidos automaticamente pelo Spring MVC a partir da query string
     * @return página de viagens com metadados de paginação ({@code totalElements},
     *         {@code totalPages}, {@code number}, {@code size}, etc.)
     */
    @GetMapping
    public ResponseEntity<Page<ViagemResponseDTO>> listarTodas(
            @RequestParam(required = false) Long veiculoId,
            @PageableDefault(size = 20, sort = "dataSaida", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(viagemService.listarTodas(veiculoId, pageable));
    }

    /**
     * {@code GET /api/v1/viagens/{id}}
     *
     * <p>Busca uma viagem pelo ID.</p>
     *
     * @param id identificador da viagem
     * @return dados completos da viagem, incluindo informações do veículo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(viagemService.buscarPorId(id));
    }

    /**
     * {@code POST /api/v1/viagens}
     *
     * <p>Cria uma nova viagem. Retorna {@code 201 Created} com o recurso criado.</p>
     *
     * @param dto dados da viagem a ser criada (validado com Bean Validation)
     * @return DTO da viagem criada com HTTP 201
     */
    @PostMapping
    public ResponseEntity<ViagemResponseDTO> criar(@Valid @RequestBody ViagemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viagemService.criar(dto));
    }

    /**
     * {@code PUT /api/v1/viagens/{id}}
     *
     * <p>Atualiza completamente uma viagem existente.</p>
     *
     * @param id  identificador da viagem a ser atualizada
     * @param dto novos dados da viagem (validado com Bean Validation)
     * @return DTO da viagem atualizada com HTTP 200
     */
    @PutMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ViagemRequestDTO dto) {

        return ResponseEntity.ok(viagemService.atualizar(id, dto));
    }

    /**
     * {@code DELETE /api/v1/viagens/{id}}
     *
     * <p>Remove uma viagem pelo ID. Retorna {@code 204 No Content} em caso de sucesso.</p>
     *
     * @param id identificador da viagem a ser removida
     * @return HTTP 204 sem corpo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        viagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
