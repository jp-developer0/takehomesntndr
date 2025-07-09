package com.santander.banking.repository;

import com.santander.banking.entity.CuentaBancaria;
import com.santander.banking.entity.TipoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para la gestión de cuentas bancarias.
 * Proporciona operaciones CRUD básicas y consultas específicas del dominio bancario.
 */
@Repository
public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {

    /**
     * Busca una cuenta bancaria por su número de cuenta.
     * @param numeroCuenta Número único de la cuenta
     * @return Optional con la cuenta si existe
     */
    Optional<CuentaBancaria> findByNumeroCuenta(String numeroCuenta);

    /**
     * Verifica si existe una cuenta con el número especificado.
     * @param numeroCuenta Número de cuenta a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNumeroCuenta(String numeroCuenta);

    /**
     * Busca todas las cuentas de un titular específico.
     * @param titular Nombre del titular
     * @return Lista de cuentas del titular
     */
    List<CuentaBancaria> findByTitularContainingIgnoreCase(String titular);

    /**
     * Busca cuentas por tipo de cuenta.
     * @param tipoCuenta Tipo de cuenta a buscar
     * @return Lista de cuentas del tipo especificado
     */
    List<CuentaBancaria> findByTipoCuenta(TipoCuenta tipoCuenta);

    /**
     * Busca cuentas activas únicamente.
     * @return Lista de cuentas activas
     */
    List<CuentaBancaria> findByActivaTrue();

    /**
     * Busca cuentas con saldo mayor al especificado.
     * @param saldoMinimo Saldo mínimo a filtrar
     * @return Lista de cuentas con saldo mayor al especificado
     */
    List<CuentaBancaria> findBySaldoGreaterThan(BigDecimal saldoMinimo);

    /**
     * Busca cuentas con saldo entre dos valores.
     * @param saldoMinimo Saldo mínimo
     * @param saldoMaximo Saldo máximo
     * @return Lista de cuentas en el rango de saldo
     */
    List<CuentaBancaria> findBySaldoBetween(BigDecimal saldoMinimo, BigDecimal saldoMaximo);

    /**
     * Busca cuentas creadas después de una fecha específica.
     * @param fecha Fecha de referencia
     * @return Lista de cuentas creadas después de la fecha
     */
    List<CuentaBancaria> findByFechaCreacionAfter(LocalDateTime fecha);

    /**
     * Busca cuentas por titular y tipo, con paginación.
     * @param titular Nombre del titular (parcial)
     * @param tipoCuenta Tipo de cuenta
     * @param pageable Configuración de paginación
     * @return Página de cuentas que coinciden con los criterios
     */
    Page<CuentaBancaria> findByTitularContainingIgnoreCaseAndTipoCuenta(
            String titular, TipoCuenta tipoCuenta, Pageable pageable);

    /**
     * Cuenta el número total de cuentas activas.
     * @return Número de cuentas activas
     */
    @Query("SELECT COUNT(c) FROM CuentaBancaria c WHERE c.activa = true")
    Long countActiveCuentas();

    /**
     * Obtiene el saldo total de todas las cuentas activas.
     * @return Suma de saldos de cuentas activas
     */
    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM CuentaBancaria c WHERE c.activa = true")
    BigDecimal getTotalSaldoActivo();

    /**
     * Obtiene estadísticas por tipo de cuenta.
     * @return Lista de objetos con tipo de cuenta y conteo
     */
    @Query("SELECT c.tipoCuenta, COUNT(c) as cantidad, COALESCE(SUM(c.saldo), 0) as saldoTotal " +
           "FROM CuentaBancaria c WHERE c.activa = true GROUP BY c.tipoCuenta")
    List<Object[]> getEstadisticasPorTipoCuenta();

    /**
     * Busca cuentas por criterios múltiples usando consulta nativa.
     * @param titular Nombre del titular (puede ser null)
     * @param tipoCuenta Tipo de cuenta (puede ser null)
     * @param saldoMinimo Saldo mínimo (puede ser null)
     * @param activa Estado de la cuenta (puede ser null)
     * @return Lista de cuentas que cumplen los criterios
     */
    @Query(value = "SELECT * FROM cuentas_bancarias c WHERE " +
           "(:titular IS NULL OR UPPER(c.titular) LIKE UPPER(CONCAT('%', :titular, '%'))) AND " +
           "(:tipoCuenta IS NULL OR c.tipo_cuenta = :tipoCuenta) AND " +
           "(:saldoMinimo IS NULL OR c.saldo >= :saldoMinimo) AND " +
           "(:activa IS NULL OR c.activa = :activa)",
           nativeQuery = true)
    List<CuentaBancaria> findByCriteriosMultiples(
            @Param("titular") String titular,
            @Param("tipoCuenta") String tipoCuenta,
            @Param("saldoMinimo") BigDecimal saldoMinimo,
            @Param("activa") Boolean activa);

    /**
     * Actualiza el saldo de una cuenta específica.
     * @param id ID de la cuenta
     * @param nuevoSaldo Nuevo saldo
     * @return Número de registros actualizados
     */
    @Modifying
    @Query("UPDATE CuentaBancaria c SET c.saldo = :nuevoSaldo, c.fechaActualizacion = CURRENT_TIMESTAMP " +
           "WHERE c.id = :id")
    int updateSaldo(@Param("id") Long id, @Param("nuevoSaldo") BigDecimal nuevoSaldo);

    /**
     * Desactiva cuentas con saldo cero que no hayan tenido movimientos en un período.
     * @param fechaLimite Fecha límite de última actualización
     * @return Número de cuentas desactivadas
     */
    @Modifying
    @Query("UPDATE CuentaBancaria c SET c.activa = false, c.fechaActualizacion = CURRENT_TIMESTAMP " +
           "WHERE c.saldo = 0 AND c.fechaActualizacion < :fechaLimite AND c.activa = true")
    int desactivarCuentasInactivas(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Busca cuentas duplicadas por titular (mismo nombre exacto).
     * @return Lista de titulares con múltiples cuentas
     */
    @Query("SELECT c.titular FROM CuentaBancaria c GROUP BY c.titular HAVING COUNT(c) > 1")
    List<String> findTitularesConCuentasDuplicadas();
} 