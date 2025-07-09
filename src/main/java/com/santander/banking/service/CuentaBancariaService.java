package com.santander.banking.service;

import com.santander.banking.dto.CuentaBancariaRequestDTO;
import com.santander.banking.dto.CuentaBancariaResponseDTO;
import com.santander.banking.entity.TipoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Interfaz del servicio de cuentas bancarias.
 * Define las operaciones de negocio disponibles para la gestión de cuentas.
 * Sigue el principio de Inversión de Dependencias (DIP) de SOLID.
 */
public interface CuentaBancariaService {

    /**
     * Crea una nueva cuenta bancaria.
     * Valida que no exista duplicidad en el número de cuenta.
     * 
     * @param requestDTO Datos de la cuenta a crear
     * @return DTO con los datos de la cuenta creada
     * @throws CuentaDuplicadaException si ya existe una cuenta con el mismo número
     */
    CuentaBancariaResponseDTO crearCuenta(CuentaBancariaRequestDTO requestDTO);

    /**
     * Obtiene una cuenta bancaria por su ID.
     * 
     * @param id Identificador único de la cuenta
     * @return DTO con los datos de la cuenta
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     */
    CuentaBancariaResponseDTO obtenerCuentaPorId(Long id);

    /**
     * Obtiene una cuenta bancaria por su número de cuenta.
     * 
     * @param numeroCuenta Número único de la cuenta
     * @return DTO con los datos de la cuenta
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     */
    CuentaBancariaResponseDTO obtenerCuentaPorNumero(String numeroCuenta);

    /**
     * Obtiene todas las cuentas bancarias con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página con las cuentas encontradas
     */
    Page<CuentaBancariaResponseDTO> obtenerTodasLasCuentas(Pageable pageable);

    /**
     * Busca cuentas por titular.
     * 
     * @param titular Nombre del titular (búsqueda parcial)
     * @return Lista de cuentas del titular
     */
    List<CuentaBancariaResponseDTO> buscarCuentasPorTitular(String titular);

    /**
     * Busca cuentas por tipo de cuenta.
     * 
     * @param tipoCuenta Tipo de cuenta a buscar
     * @return Lista de cuentas del tipo especificado
     */
    List<CuentaBancariaResponseDTO> buscarCuentasPorTipo(TipoCuenta tipoCuenta);

    /**
     * Obtiene solo las cuentas activas.
     * 
     * @return Lista de cuentas activas
     */
    List<CuentaBancariaResponseDTO> obtenerCuentasActivas();

    /**
     * Busca cuentas con criterios múltiples.
     * 
     * @param titular Nombre del titular (opcional)
     * @param tipoCuenta Tipo de cuenta (opcional)
     * @param saldoMinimo Saldo mínimo (opcional)
     * @param activa Estado de la cuenta (opcional)
     * @return Lista de cuentas que cumplen los criterios
     */
    List<CuentaBancariaResponseDTO> buscarConCriterios(String titular, TipoCuenta tipoCuenta, 
                                                       BigDecimal saldoMinimo, Boolean activa);

    /**
     * Actualiza los datos de una cuenta bancaria.
     * 
     * @param id Identificador de la cuenta a actualizar
     * @param requestDTO Nuevos datos de la cuenta
     * @return DTO con los datos actualizados
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     */
    CuentaBancariaResponseDTO actualizarCuenta(Long id, CuentaBancariaRequestDTO requestDTO);

    /**
     * Actualiza el saldo de una cuenta.
     * 
     * @param id Identificador de la cuenta
     * @param nuevoSaldo Nuevo saldo a establecer
     * @return DTO con los datos actualizados
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     * @throws IllegalArgumentException si el saldo es negativo
     */
    CuentaBancariaResponseDTO actualizarSaldo(Long id, BigDecimal nuevoSaldo);

    /**
     * Realiza un débito en una cuenta.
     * 
     * @param id Identificador de la cuenta
     * @param monto Monto a debitar
     * @return DTO con los datos actualizados
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     * @throws SaldoInsuficienteException si no hay saldo suficiente
     */
    CuentaBancariaResponseDTO debitarCuenta(Long id, BigDecimal monto);

    /**
     * Realiza un crédito en una cuenta.
     * 
     * @param id Identificador de la cuenta
     * @param monto Monto a acreditar
     * @return DTO con los datos actualizados
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     */
    CuentaBancariaResponseDTO acreditarCuenta(Long id, BigDecimal monto);

    /**
     * Activa una cuenta bancaria.
     * 
     * @param id Identificador de la cuenta
     * @return DTO con los datos actualizados
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     */
    CuentaBancariaResponseDTO activarCuenta(Long id);

    /**
     * Desactiva una cuenta bancaria.
     * 
     * @param id Identificador de la cuenta
     * @return DTO con los datos actualizados
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     */
    CuentaBancariaResponseDTO desactivarCuenta(Long id);

    /**
     * Elimina una cuenta bancaria (borrado lógico).
     * 
     * @param id Identificador de la cuenta a eliminar
     * @throws CuentaBancariaNotFoundException si no se encuentra la cuenta
     */
    void eliminarCuenta(Long id);

    /**
     * Obtiene estadísticas generales del sistema.
     * 
     * @return Mapa con estadísticas (total cuentas, saldo total, etc.)
     */
    Map<String, Object> obtenerEstadisticas();

    /**
     * Obtiene estadísticas por tipo de cuenta.
     * 
     * @return Mapa con estadísticas agrupadas por tipo de cuenta
     */
    Map<TipoCuenta, Map<String, Object>> obtenerEstadisticasPorTipo();

    /**
     * Verifica si un número de cuenta ya existe en el sistema.
     * 
     * @param numeroCuenta Número de cuenta a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeNumeroCuenta(String numeroCuenta);

    /**
     * Obtiene los titulares que tienen cuentas duplicadas.
     * 
     * @return Lista de nombres de titulares con múltiples cuentas
     */
    List<String> obtenerTitularesConCuentasDuplicadas();
} 