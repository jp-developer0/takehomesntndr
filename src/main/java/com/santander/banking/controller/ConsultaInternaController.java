package com.santander.banking.controller;

import com.santander.banking.dto.CuentaBancariaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Controlador que implementa endpoints que consumen sus propios endpoints de consulta.
 * Este controlador cumple con el requerimiento del enunciado de crear un endpoint
 * que realice una llamada a sí mismo para consumir el endpoint de consulta.
 */
@RestController
@RequestMapping("/consulta-interna")
@Tag(name = "Consulta Interna", description = "Endpoints que consumen sus propios endpoints de consulta")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConsultaInternaController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaInternaController.class);

    private final RestTemplate restTemplate;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Autowired
    public ConsultaInternaController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Endpoint que consume el endpoint de consulta por ID realizando una llamada HTTP a sí mismo.
     * Este endpoint cumple con el requerimiento específico del enunciado.
     */
    @GetMapping("/cuenta/{id}")
    @Operation(summary = "Consulta interna de cuenta por ID", 
              description = "Endpoint que consume su propio endpoint de consulta por ID realizando una llamada HTTP interna")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada a través de consulta interna"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor o error en la llamada interna")
    })
    public ResponseEntity<Map<String, Object>> consultarCuentaPorIdInterna(
            @Parameter(description = "ID único de la cuenta", required = true, example = "1")
            @PathVariable @NotNull @Min(1) Long id) {
        
        logger.info("Iniciando consulta interna para cuenta con ID: {}", id);

        try {
            // Construir la URL del endpoint propio
            String baseUrl = "http://localhost:" + serverPort + contextPath;
            String url = baseUrl + "/cuentas/" + id;
            
            logger.debug("Realizando llamada HTTP interna a: {}", url);

            // Realizar la llamada HTTP al propio endpoint
            ResponseEntity<CuentaBancariaResponseDTO> response = restTemplate.getForEntity(
                    url, CuentaBancariaResponseDTO.class);

            // Preparar respuesta con metadatos adicionales
            Map<String, Object> respuestaCompleta = Map.of(
                    "origen", "consulta-interna",
                    "endpointConsumido", url,
                    "timestampConsulta", System.currentTimeMillis(),
                    "statusRespuesta", response.getStatusCode().value(),
                    "cuenta", response.getBody()
            );

            logger.info("Consulta interna completada exitosamente para cuenta ID: {}", id);

            return ResponseEntity.ok(respuestaCompleta);

        } catch (Exception e) {
            logger.error("Error en consulta interna para cuenta ID: {}", id, e);
            
            Map<String, Object> errorResponse = Map.of(
                    "origen", "consulta-interna",
                    "error", true,
                    "mensaje", "Error al realizar consulta interna: " + e.getMessage(),
                    "timestampError", System.currentTimeMillis()
            );
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint que consume el endpoint de consulta de cuentas activas realizando una llamada HTTP a sí mismo.
     */
    @GetMapping("/cuentas-activas")
    @Operation(summary = "Consulta interna de cuentas activas", 
              description = "Endpoint que consume su propio endpoint de cuentas activas realizando una llamada HTTP interna")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuentas activas obtenidas a través de consulta interna"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor o error en la llamada interna")
    })
    public ResponseEntity<Map<String, Object>> consultarCuentasActivasInterna() {
        
        logger.info("Iniciando consulta interna para cuentas activas");

        try {
            // Construir la URL del endpoint propio
            String baseUrl = "http://localhost:" + serverPort + contextPath;
            String url = baseUrl + "/cuentas/activas";
            
            logger.debug("Realizando llamada HTTP interna a: {}", url);

            // Realizar la llamada HTTP al propio endpoint
            ResponseEntity<List<CuentaBancariaResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CuentaBancariaResponseDTO>>() {}
            );

            // Preparar respuesta con metadatos adicionales
            Map<String, Object> respuestaCompleta = Map.of(
                    "origen", "consulta-interna",
                    "endpointConsumido", url,
                    "timestampConsulta", System.currentTimeMillis(),
                    "statusRespuesta", response.getStatusCode().value(),
                    "totalCuentasActivas", response.getBody() != null ? response.getBody().size() : 0,
                    "cuentasActivas", response.getBody()
            );

            logger.info("Consulta interna de cuentas activas completada exitosamente. Total: {}", 
                       response.getBody() != null ? response.getBody().size() : 0);

            return ResponseEntity.ok(respuestaCompleta);

        } catch (Exception e) {
            logger.error("Error en consulta interna de cuentas activas", e);
            
            Map<String, Object> errorResponse = Map.of(
                    "origen", "consulta-interna",
                    "error", true,
                    "mensaje", "Error al realizar consulta interna: " + e.getMessage(),
                    "timestampError", System.currentTimeMillis()
            );
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint que consume el endpoint de estadísticas realizando una llamada HTTP a sí mismo.
     */
    @GetMapping("/estadisticas")
    @Operation(summary = "Consulta interna de estadísticas", 
              description = "Endpoint que consume su propio endpoint de estadísticas realizando una llamada HTTP interna")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas a través de consulta interna"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor o error en la llamada interna")
    })
    public ResponseEntity<Map<String, Object>> consultarEstadisticasInterna() {
        
        logger.info("Iniciando consulta interna para estadísticas");

        try {
            // Construir la URL del endpoint propio
            String baseUrl = "http://localhost:" + serverPort + contextPath;
            String url = baseUrl + "/cuentas/estadisticas";
            
            logger.debug("Realizando llamada HTTP interna a: {}", url);

            // Realizar la llamada HTTP al propio endpoint
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Preparar respuesta con metadatos adicionales
            Map<String, Object> respuestaCompleta = Map.of(
                    "origen", "consulta-interna",
                    "endpointConsumido", url,
                    "timestampConsulta", System.currentTimeMillis(),
                    "statusRespuesta", response.getStatusCode().value(),
                    "estadisticas", response.getBody()
            );

            logger.info("Consulta interna de estadísticas completada exitosamente");

            return ResponseEntity.ok(respuestaCompleta);

        } catch (Exception e) {
            logger.error("Error en consulta interna de estadísticas", e);
            
            Map<String, Object> errorResponse = Map.of(
                    "origen", "consulta-interna",
                    "error", true,
                    "mensaje", "Error al realizar consulta interna: " + e.getMessage(),
                    "timestampError", System.currentTimeMillis()
            );
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint que genera un resumen completo realizando múltiples consultas internas.
     */
    @GetMapping("/resumen-completo")
    @Operation(summary = "Resumen completo mediante consultas internas", 
              description = "Endpoint que realiza múltiples consultas internas para generar un resumen completo del sistema")
    public ResponseEntity<Map<String, Object>> obtenerResumenCompletoInterno() {
        
        logger.info("Iniciando generación de resumen completo mediante consultas internas");

        try {
            String baseUrl = "http://localhost:" + serverPort + contextPath;

            // Consulta de estadísticas generales
            String urlEstadisticas = baseUrl + "/cuentas/estadisticas";
            ResponseEntity<Map<String, Object>> estadisticas = restTemplate.exchange(
                    urlEstadisticas,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Consulta de cuentas activas
            String urlCuentasActivas = baseUrl + "/cuentas/activas";
            ResponseEntity<List<CuentaBancariaResponseDTO>> cuentasActivas = restTemplate.exchange(
                    urlCuentasActivas,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CuentaBancariaResponseDTO>>() {}
            );

            // Consulta de estadísticas por tipo
            String urlEstadisticasTipo = baseUrl + "/cuentas/estadisticas/tipo";
            ResponseEntity<Map<String, Object>> estadisticasTipo = restTemplate.exchange(
                    urlEstadisticasTipo,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Preparar resumen completo
            Map<String, Object> resumenCompleto = Map.of(
                    "origen", "consulta-interna-multiple",
                    "timestampGeneracion", System.currentTimeMillis(),
                    "endpointsConsumidos", List.of(urlEstadisticas, urlCuentasActivas, urlEstadisticasTipo),
                    "estadisticasGenerales", estadisticas.getBody(),
                    "cuentasActivas", cuentasActivas.getBody(),
                    "estadisticasPorTipo", estadisticasTipo.getBody(),
                    "resumen", Map.of(
                            "totalCuentasActivas", cuentasActivas.getBody() != null ? cuentasActivas.getBody().size() : 0,
                            "consultasRealizadas", 3,
                            "statusGeneracion", "exitoso"
                    )
            );

            logger.info("Resumen completo generado exitosamente mediante {} consultas internas", 3);

            return ResponseEntity.ok(resumenCompleto);

        } catch (Exception e) {
            logger.error("Error en generación de resumen completo", e);
            
            Map<String, Object> errorResponse = Map.of(
                    "origen", "consulta-interna-multiple",
                    "error", true,
                    "mensaje", "Error al generar resumen completo: " + e.getMessage(),
                    "timestampError", System.currentTimeMillis()
            );
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
} 