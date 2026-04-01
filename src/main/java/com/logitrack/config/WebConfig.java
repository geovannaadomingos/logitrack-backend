package com.logitrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração global de CORS (Cross-Origin Resource Sharing) da API.
 *
 * <h3>Por que não usar {@code allowedOrigins("*")}?</h3>
 * <p>O wildcard {@code "*"} permite que qualquer origem acesse a API, o que
 * representa um risco de segurança em produção:</p>
 * <ul>
 *   <li>Expõe endpoints a ataques CSRF via sites maliciosos</li>
 *   <li>Incompatível com {@code allowCredentials(true)} — o browser rejeita a configuração</li>
 *   <li>Viola o princípio de menor privilégio</li>
 * </ul>
 *
 * <h3>Estratégia adotada</h3>
 * <p>As origens permitidas são configuradas via {@code application.properties}
 * (propriedade {@code logitrack.cors.allowed-origins}), permitindo valores diferentes
 * por ambiente sem alterar o código:</p>
 * <ul>
 *   <li><strong>desenvolvimento</strong>: {@code http://localhost:3000,http://localhost:5173}</li>
 *   <li><strong>produção</strong>: {@code https://logitrack.exemplo.com.br}</li>
 * </ul>
 *
 * <h3>Configurações aplicadas</h3>
 * <ul>
 *   <li>{@code allowedMethods} — apenas os verbos HTTP utilizados pela API</li>
 *   <li>{@code allowedHeaders} — qualquer header (necessário para {@code Content-Type}, {@code Authorization})</li>
 *   <li>{@code allowCredentials(true)} — permite cookies e headers de autenticação cross-origin</li>
 *   <li>{@code maxAge(3600)} — preflight cacheado por 1 hora (reduz latência)</li>
 * </ul>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Origens CORS permitidas, configuráveis por ambiente via {@code application.properties}.
     *
     * <p>Exemplo de configuração no {@code application.properties}:</p>
     * <pre>
     * # Desenvolvimento
     * logitrack.cors.allowed-origins=http://localhost:3000,http://localhost:5173
     *
     * # Produção (application-prod.properties)
     * logitrack.cors.allowed-origins=https://logitrack.exemplo.com.br
     * </pre>
     */
    @Value("${logitrack.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
