package com.santander.banking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de RestTemplate para las llamadas HTTP internas.
 * Permite que el microservicio pueda realizar llamadas a sus propios endpoints.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${banking.api.self-call-timeout:5000}")
    private int timeout;

    /**
     * Bean de RestTemplate configurado para llamadas internas.
     * Incluye configuración de timeouts apropiados para llamadas locales.
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }

    /**
     * Factory para la configuración de las peticiones HTTP.
     * Configura timeouts y otras propiedades de las conexiones.
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Timeout para establecer la conexión
        factory.setConnectTimeout(timeout);
        
        // Timeout para leer la respuesta
        factory.setReadTimeout(timeout);
        
        return factory;
    }
} 