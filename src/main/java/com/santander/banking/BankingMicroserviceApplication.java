package com.santander.banking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Clase principal del microservicio bancario.
 * Configura y arranca la aplicación Spring Boot con todas sus características:
 * - API REST completa para cuentas bancarias
 * - Base de datos H2 en memoria
 * - Documentación Swagger
 * - Observabilidad con Actuator y métricas
 * - Endpoint de auto-consulta
 */
@SpringBootApplication
public class BankingMicroserviceApplication {

    private static final Logger logger = LoggerFactory.getLogger(BankingMicroserviceApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BankingMicroserviceApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        logApplicationStartup(env);
    }

    /**
     * Registra información de inicio de la aplicación en los logs.
     */
    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (contextPath == null || contextPath.trim().isEmpty()) {
            contextPath = "/";
        }
        
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("No se pudo determinar la dirección IP del host", e);
        }

        logger.info("\n----------------------------------------------------------\n\t" +
                "Aplicación '{}' se ha iniciado correctamente!\n\t" +
                "Perfil(es) activo(s): \t{}\n\t" +
                "URL Local: \t\t{}://localhost:{}{}\n\t" +
                "URL Externa: \t\t{}://{}:{}{}\n\t" +
                "Swagger UI: \t\t{}://localhost:{}{}/swagger-ui/\n\t" +
                "H2 Console: \t\t{}://localhost:{}{}/h2-console\n\t" +
                "Actuator: \t\t{}://localhost:{}{}/actuator\n" +
                "----------------------------------------------------------",
                env.getProperty("spring.application.name", "Banking Microservice"),
                env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles(),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                protocol,
                serverPort,
                contextPath,
                protocol,
                serverPort,
                contextPath,
                protocol,
                serverPort,
                contextPath
        );

        // Log específico sobre las características del microservicio
        logger.info("\n----------------------------------------------------------\n\t" +
                "Características del Microservicio Bancario:\n\t" +
                "✓ CRUD completo para cuentas bancarias\n\t" +
                "✓ Validaciones de negocio y duplicidad\n\t" +
                "✓ Endpoint de auto-consulta (cumple requerimiento)\n\t" +
                "✓ Base de datos H2 en memoria\n\t" +
                "✓ Documentación Swagger interactiva\n\t" +
                "✓ Observabilidad con Actuator y métricas\n\t" +
                "✓ Manejo centralizado de excepciones\n\t" +
                "✓ Logs estructurados y trazabilidad\n\t" +
                "✓ Tests unitarios e integración\n\t" +
                "✓ Arquitectura SOLID y patrones de diseño\n" +
                "----------------------------------------------------------");
    }
} 