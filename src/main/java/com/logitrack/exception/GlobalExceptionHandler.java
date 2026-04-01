package com.logitrack.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler global de exceções que padroniza todas as respostas de erro da API.
 *
 * <p>Centraliza o tratamento de erros, garantindo que todos os endpoints
 * retornem o mesmo contrato de resposta em caso de falha, sem duplicar
 * lógica nos controllers.</p>
 *
 * <h3>Mapeamentos</h3>
 * <ul>
 *   <li>{@link ResourceNotFoundException}         → {@code 404 Not Found}</li>
 *   <li>{@link MethodArgumentNotValidException}   → {@code 400 Bad Request} (com fieldErrors)</li>
 *   <li>{@link IllegalArgumentException}          → {@code 400 Bad Request}</li>
 *   <li>{@link Exception}                         → {@code 500 Internal Server Error}</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===================================================================
    // 404 Not Found
    // ===================================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Recurso não encontrado: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ===================================================================
    // 400 Bad Request — Validation (@Valid)
    // ===================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }

        log.warn("Falha de validação nos campos: {}", fieldErrors);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Há campos inválidos na requisição")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ===================================================================
    // 400 Bad Request — JSON malformado ou tipo inválido
    // ===================================================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("JSON malformado ou tipo inválido em [{}]: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("O corpo da requisição está malformado ou contém tipos inválidos")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ===================================================================
    // 400 Bad Request — Regras de negócio
    // ===================================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Argumento inválido: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ===================================================================
    // 500 Internal Server Error — catch-all
    // ===================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        log.error("Erro interno não esperado em [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocorreu um erro interno. Tente novamente ou entre em contato com o suporte.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ===================================================================
    // Contrato de resposta de erro
    // ===================================================================

    /**
     * Estrutura padronizada de resposta para todos os erros da API.
     *
     * <p>{@code fieldErrors} só é preenchido em falhas de validação do Bean Validation
     * e será {@code null} nos demais casos (o Jackson omite campos nulos por padrão
     * se configurado com {@code Include.NON_NULL}).</p>
     */
    @Getter
    @Builder
    public static class ApiErrorResponse {

        /** Momento exato em que o erro ocorreu no servidor. */
        private final LocalDateTime timestamp;

        /** Código HTTP numérico (ex.: 404, 400, 500). */
        private final int status;

        /** Descrição curta do tipo de erro (ex.: "Not Found"). */
        private final String error;

        /** Mensagem legível por humanos descrevendo o problema. */
        private final String message;

        /** Path da requisição que gerou o erro. */
        private final String path;

        /**
         * Mapa de campo → mensagem de erro para falhas de validação.
         * {@code null} quando não aplicável.
         */
        private final Map<String, String> fieldErrors;
    }
}
