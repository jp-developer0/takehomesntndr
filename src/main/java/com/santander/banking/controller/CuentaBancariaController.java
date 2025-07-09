package com.santander.banking.controller;

import com.santander.banking.dto.CuentaBancariaRequestDTO;
import com.santander.banking.dto.CuentaBancariaResponseDTO;
import com.santander.banking.entity.TipoCuenta;
import com.santander.banking.service.CuentaBancariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de cuentas bancarias.
 * Proporciona endpoints para todas las operaciones CRUD y operaciones bancarias.
 */
@RestController
@RequestMapping("/cuentas")
@Validated
@Tag(name = "Cuentas Bancarias", description = "API para la gestión de cuentas bancarias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CuentaBancariaController {

    private static final Logger logger = LoggerFactory.getLogger(CuentaBancariaController.class);

    private final CuentaBancariaService cuentaBancariaService;

    @Autowired
    public CuentaBancariaController(CuentaBancariaService cuentaBancariaService) {
        this.cuentaBancariaService = cuentaBancariaService;
    }

    /**
     * Crea una nueva cuenta bancaria.
     */
    @PostMapping
    @Operation(summary = "Crear cuenta bancaria", 
              description = "Crea una nueva cuenta bancaria verificando que no exista duplicidad en el número de cuenta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Número de cuenta ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CuentaBancariaResponseDTO> crearCuenta(
            @Parameter(description = "Datos de la cuenta a crear", required = true)
            @Valid @RequestBody CuentaBancariaRequestDTO requestDTO) {
        
        logger.info("Recibida petición para crear cuenta bancaria: {}", requestDTO.getNumeroCuenta());
        
        CuentaBancariaResponseDTO cuentaCreada = cuentaBancariaService.crearCuenta(requestDTO);
        
        logger.info("Cuenta bancaria creada exitosamente con ID: {}", cuentaCreada.getId());
        
        return new ResponseEntity<>(cuentaCreada, HttpStatus.CREATED);
    }

    /**
     * Obtiene una cuenta bancaria por su ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID", 
              description = "Recupera los datos completos de una cuenta bancaria por su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CuentaBancariaResponseDTO> obtenerCuentaPorId(
            @Parameter(description = "ID único de la cuenta", required = true, example = "1")
            @PathVariable @NotNull @Min(1) Long id) {
        
        logger.debug("Recibida petición para obtener cuenta con ID: {}", id);
        
        CuentaBancariaResponseDTO cuenta = cuentaBancariaService.obtenerCuentaPorId(id);
        
        return ResponseEntity.ok(cuenta);
    }

    /**
     * Obtiene una cuenta bancaria por su número de cuenta.
     */
    @GetMapping("/numero/{numeroCuenta}")
    @Operation(summary = "Obtener cuenta por número", 
              description = "Recupera los datos de una cuenta bancaria por su número único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CuentaBancariaResponseDTO> obtenerCuentaPorNumero(
            @Parameter(description = "Número único de la cuenta", required = true, example = "1234567890")
            @PathVariable @NotBlank String numeroCuenta) {
        
        logger.debug("Recibida petición para obtener cuenta con número: {}", numeroCuenta);
        
        CuentaBancariaResponseDTO cuenta = cuentaBancariaService.obtenerCuentaPorNumero(numeroCuenta);
        
        return ResponseEntity.ok(cuenta);
    }

    /**
     * Obtiene todas las cuentas bancarias con paginación.
     */
    @GetMapping
    @Operation(summary = "Listar cuentas bancarias", 
              description = "Obtiene una lista paginada de todas las cuentas bancarias del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cuentas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<CuentaBancariaResponseDTO>> obtenerTodasLasCuentas(
            @Parameter(description = "Número de página (empezando desde 0)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size,
            
            @Parameter(description = "Campo para ordenar", example = "fechaCreacion")
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            
            @Parameter(description = "Dirección del ordenamiento", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("Recibida petición para listar cuentas - página: {}, tamaño: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CuentaBancariaResponseDTO> cuentas = cuentaBancariaService.obtenerTodasLasCuentas(pageable);
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Busca cuentas por titular.
     */
    @GetMapping("/buscar/titular")
    @Operation(summary = "Buscar cuentas por titular", 
              description = "Busca cuentas bancarias por nombre del titular (búsqueda parcial)")
    public ResponseEntity<List<CuentaBancariaResponseDTO>> buscarCuentasPorTitular(
            @Parameter(description = "Nombre del titular (búsqueda parcial)", required = true)
            @RequestParam @NotBlank String titular) {
        
        logger.debug("Recibida petición para buscar cuentas del titular: {}", titular);
        
        List<CuentaBancariaResponseDTO> cuentas = cuentaBancariaService.buscarCuentasPorTitular(titular);
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Busca cuentas por tipo de cuenta.
     */
    @GetMapping("/buscar/tipo")
    @Operation(summary = "Buscar cuentas por tipo", 
              description = "Busca cuentas bancarias por tipo de cuenta")
    public ResponseEntity<List<CuentaBancariaResponseDTO>> buscarCuentasPorTipo(
            @Parameter(description = "Tipo de cuenta", required = true)
            @RequestParam @NotNull TipoCuenta tipoCuenta) {
        
        logger.debug("Recibida petición para buscar cuentas de tipo: {}", tipoCuenta);
        
        List<CuentaBancariaResponseDTO> cuentas = cuentaBancariaService.buscarCuentasPorTipo(tipoCuenta);
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Obtiene solo las cuentas activas.
     */
    @GetMapping("/activas")
    @Operation(summary = "Obtener cuentas activas", 
              description = "Recupera todas las cuentas bancarias que están activas")
    public ResponseEntity<List<CuentaBancariaResponseDTO>> obtenerCuentasActivas() {
        
        logger.debug("Recibida petición para obtener cuentas activas");
        
        List<CuentaBancariaResponseDTO> cuentasActivas = cuentaBancariaService.obtenerCuentasActivas();
        
        return ResponseEntity.ok(cuentasActivas);
    }

    /**
     * Busca cuentas con criterios múltiples.
     */
    @GetMapping("/buscar")
    @Operation(summary = "Búsqueda avanzada de cuentas", 
              description = "Busca cuentas bancarias con múltiples criterios de filtrado")
    public ResponseEntity<List<CuentaBancariaResponseDTO>> buscarConCriterios(
            @Parameter(description = "Nombre del titular (opcional)")
            @RequestParam(required = false) String titular,
            
            @Parameter(description = "Tipo de cuenta (opcional)")
            @RequestParam(required = false) TipoCuenta tipoCuenta,
            
            @Parameter(description = "Saldo mínimo (opcional)")
            @RequestParam(required = false) @DecimalMin("0.0") BigDecimal saldoMinimo,
            
            @Parameter(description = "Estado de la cuenta (opcional)")
            @RequestParam(required = false) Boolean activa) {
        
        logger.debug("Recibida petición de búsqueda con criterios múltiples");
        
        List<CuentaBancariaResponseDTO> cuentas = cuentaBancariaService.buscarConCriterios(
                titular, tipoCuenta, saldoMinimo, activa);
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Actualiza una cuenta bancaria.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuenta bancaria", 
              description = "Actualiza los datos de una cuenta bancaria existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CuentaBancariaResponseDTO> actualizarCuenta(
            @Parameter(description = "ID de la cuenta a actualizar", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            
            @Parameter(description = "Nuevos datos de la cuenta", required = true)
            @Valid @RequestBody CuentaBancariaRequestDTO requestDTO) {
        
        logger.info("Recibida petición para actualizar cuenta con ID: {}", id);
        
        CuentaBancariaResponseDTO cuentaActualizada = cuentaBancariaService.actualizarCuenta(id, requestDTO);
        
        logger.info("Cuenta bancaria actualizada exitosamente con ID: {}", id);
        
        return ResponseEntity.ok(cuentaActualizada);
    }

    /**
     * Actualiza el saldo de una cuenta.
     */
    @PatchMapping("/{id}/saldo")
    @Operation(summary = "Actualizar saldo de cuenta", 
              description = "Actualiza únicamente el saldo de una cuenta bancaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saldo actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Saldo inválido"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CuentaBancariaResponseDTO> actualizarSaldo(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            
            @Parameter(description = "Nuevo saldo", required = true)
            @RequestParam @NotNull @DecimalMin("0.0") BigDecimal nuevoSaldo) {
        
        logger.info("Recibida petición para actualizar saldo de cuenta ID: {} a {}", id, nuevoSaldo);
        
        CuentaBancariaResponseDTO cuentaActualizada = cuentaBancariaService.actualizarSaldo(id, nuevoSaldo);
        
        return ResponseEntity.ok(cuentaActualizada);
    }

    /**
     * Realiza un débito en una cuenta.
     */
    @PostMapping("/{id}/debitar")
    @Operation(summary = "Debitar cuenta", 
              description = "Realiza un débito (retiro) en una cuenta bancaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Débito realizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Monto inválido o saldo insuficiente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CuentaBancariaResponseDTO> debitarCuenta(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            
            @Parameter(description = "Monto a debitar", required = true)
            @RequestParam @NotNull @DecimalMin("0.01") BigDecimal monto) {
        
        logger.info("Recibida petición para debitar {} de la cuenta ID: {}", monto, id);
        
        CuentaBancariaResponseDTO cuentaActualizada = cuentaBancariaService.debitarCuenta(id, monto);
        
        return ResponseEntity.ok(cuentaActualizada);
    }

    /**
     * Realiza un crédito en una cuenta.
     */
    @PostMapping("/{id}/acreditar")
    @Operation(summary = "Acreditar cuenta", 
              description = "Realiza un crédito (depósito) en una cuenta bancaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Crédito realizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Monto inválido"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CuentaBancariaResponseDTO> acreditarCuenta(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            
            @Parameter(description = "Monto a acreditar", required = true)
            @RequestParam @NotNull @DecimalMin("0.01") BigDecimal monto) {
        
        logger.info("Recibida petición para acreditar {} a la cuenta ID: {}", monto, id);
        
        CuentaBancariaResponseDTO cuentaActualizada = cuentaBancariaService.acreditarCuenta(id, monto);
        
        return ResponseEntity.ok(cuentaActualizada);
    }

    /**
     * Activa una cuenta bancaria.
     */
    @PatchMapping("/{id}/activar")
    @Operation(summary = "Activar cuenta", 
              description = "Activa una cuenta bancaria previamente desactivada")
    public ResponseEntity<CuentaBancariaResponseDTO> activarCuenta(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        
        logger.info("Recibida petición para activar cuenta ID: {}", id);
        
        CuentaBancariaResponseDTO cuentaActivada = cuentaBancariaService.activarCuenta(id);
        
        return ResponseEntity.ok(cuentaActivada);
    }

    /**
     * Desactiva una cuenta bancaria.
     */
    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar cuenta", 
              description = "Desactiva una cuenta bancaria activa")
    public ResponseEntity<CuentaBancariaResponseDTO> desactivarCuenta(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        
        logger.info("Recibida petición para desactivar cuenta ID: {}", id);
        
        CuentaBancariaResponseDTO cuentaDesactivada = cuentaBancariaService.desactivarCuenta(id);
        
        return ResponseEntity.ok(cuentaDesactivada);
    }

    /**
     * Elimina una cuenta bancaria.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cuenta bancaria", 
              description = "Elimina una cuenta bancaria del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarCuenta(
            @Parameter(description = "ID de la cuenta a eliminar", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        
        logger.info("Recibida petición para eliminar cuenta ID: {}", id);
        
        cuentaBancariaService.eliminarCuenta(id);
        
        logger.info("Cuenta eliminada exitosamente con ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene estadísticas generales del sistema.
     */
    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas generales", 
              description = "Recupera estadísticas generales del sistema bancario")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        
        logger.debug("Recibida petición para obtener estadísticas generales");
        
        Map<String, Object> estadisticas = cuentaBancariaService.obtenerEstadisticas();
        
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene estadísticas por tipo de cuenta.
     */
    @GetMapping("/estadisticas/tipo")
    @Operation(summary = "Obtener estadísticas por tipo", 
              description = "Recupera estadísticas agrupadas por tipo de cuenta")
    public ResponseEntity<Map<TipoCuenta, Map<String, Object>>> obtenerEstadisticasPorTipo() {
        
        logger.debug("Recibida petición para obtener estadísticas por tipo");
        
        Map<TipoCuenta, Map<String, Object>> estadisticas = cuentaBancariaService.obtenerEstadisticasPorTipo();
        
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Verifica si existe un número de cuenta.
     */
    @GetMapping("/existe/{numeroCuenta}")
    @Operation(summary = "Verificar existencia de número de cuenta", 
              description = "Verifica si un número de cuenta ya existe en el sistema")
    public ResponseEntity<Boolean> existeNumeroCuenta(
            @Parameter(description = "Número de cuenta a verificar", required = true)
            @PathVariable @NotBlank String numeroCuenta) {
        
        logger.debug("Recibida petición para verificar existencia de número: {}", numeroCuenta);
        
        boolean existe = cuentaBancariaService.existeNumeroCuenta(numeroCuenta);
        
        return ResponseEntity.ok(existe);
    }

    /**
     * Obtiene titulares con cuentas duplicadas.
     */
    @GetMapping("/duplicados")
    @Operation(summary = "Obtener titulares con cuentas duplicadas", 
              description = "Recupera la lista de titulares que tienen múltiples cuentas")
    public ResponseEntity<List<String>> obtenerTitularesConCuentasDuplicadas() {
        
        logger.debug("Recibida petición para obtener titulares con cuentas duplicadas");
        
        List<String> titulares = cuentaBancariaService.obtenerTitularesConCuentasDuplicadas();
        
        return ResponseEntity.ok(titulares);
    }
} 