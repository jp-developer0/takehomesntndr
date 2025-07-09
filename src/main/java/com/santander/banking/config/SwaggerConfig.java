package com.santander.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de OpenAPI 3 (Swagger) para la documentación de la API del microservicio bancario.
 * Proporciona una interfaz web interactiva para explorar y probar los endpoints.
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configuración principal de OpenAPI 3.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(Arrays.asList(
                    new Server()
                        .url("http://localhost:" + serverPort + contextPath)
                        .description("Servidor de desarrollo local")
                ))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes("JWT", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token de autorización")
                    )
                )
                .tags(Arrays.asList(
                    new Tag()
                        .name("Cuentas Bancarias")
                        .description("Operaciones CRUD y gestión de cuentas bancarias"),
                    new Tag()
                        .name("Consulta Interna")
                        .description("Endpoints que consumen sus propios endpoints de consulta"),
                    new Tag()
                        .name("Actuator")
                        .description("Endpoints de monitoreo y métricas del sistema")
                ));
    }

    /**
     * Información general de la API.
     */
    private Info apiInfo() {
        return new Info()
                .title("Banking Microservice API")
                .description("Microservicio bancario con CRUD completo para cuentas bancarias. " +
                           "Incluye operaciones básicas, consultas avanzadas, estadísticas y endpoints de auto-consulta.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Santander Banking Team")
                    .url("https://www.santander.com")
                    .email("banking-support@santander.com")
                )
                .license(new License()
                    .name("Apache License Version 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")
                )
                .termsOfService("https://www.santander.com/terms");
    }
} 