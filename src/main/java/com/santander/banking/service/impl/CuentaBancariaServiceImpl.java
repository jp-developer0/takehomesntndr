package com.santander.banking.service.impl;

import com.santander.banking.dto.CuentaBancariaRequestDTO;
import com.santander.banking.dto.CuentaBancariaResponseDTO;
import com.santander.banking.entity.CuentaBancaria;
import com.santander.banking.entity.TipoCuenta;
import com.santander.banking.exception.CuentaBancariaNotFoundException;
import com.santander.banking.exception.CuentaDuplicadaException;
import com.santander.banking.exception.SaldoInsuficienteException;
import com.santander.banking.repository.CuentaBancariaRepository;
import com.santander.banking.service.CuentaBancariaService;
import com.santander.banking.util.CuentaBancariaMapper;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de cuentas bancarias.
 * Contiene toda la lógica de negocio y aplica principios SOLID.
 * Incluye métricas y logging para observabilidad.
 */
@Service
@Transactional
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    private static final Logger logger = LoggerFactory.getLogger(CuentaBancariaServiceImpl.class);

    private final CuentaBancariaRepository repository;
    private final CuentaBancariaMapper mapper;

    @Autowired
    public CuentaBancariaServiceImpl(CuentaBancariaRepository repository, CuentaBancariaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Timed(value = "cuenta.crear.tiempo", description = "Tiempo para crear una cuenta")
    @Counted(value = "cuenta.crear.intentos", description = "Intentos de creación de cuentas")
    public CuentaBancariaResponseDTO crearCuenta(CuentaBancariaRequestDTO requestDTO) {
        logger.info("Iniciando creación de cuenta bancaria para titular: {}", requestDTO.getTitular());

        // Validar DTO de entrada
        if (!mapper.isValidRequestDTO(requestDTO)) {
            logger.warn("Datos inválidos para crear cuenta: {}", requestDTO);
            throw new IllegalArgumentException("Los datos proporcionados no son válidos");
        }

        // Verificar duplicidad
        if (repository.existsByNumeroCuenta(requestDTO.getNumeroCuenta())) {
            logger.warn("Intento de crear cuenta duplicada con número: {}", requestDTO.getNumeroCuenta());
            throw new CuentaDuplicadaException(requestDTO.getNumeroCuenta());
        }

        try {
            // Convertir DTO a entidad y guardar
            CuentaBancaria nuevaCuenta = mapper.toEntity(requestDTO);
            CuentaBancaria cuentaGuardada = repository.save(nuevaCuenta);

            logger.info("Cuenta bancaria creada exitosamente con ID: {} para titular: {}", 
                       cuentaGuardada.getId(), cuentaGuardada.getTitular());

            return mapper.toResponseDTO(cuentaGuardada);

        } catch (Exception e) {
            logger.error("Error al crear cuenta bancaria para titular: {}", requestDTO.getTitular(), e);
            throw new RuntimeException("Error interno al crear la cuenta bancaria", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "cuenta.buscar.id.tiempo", description = "Tiempo para buscar cuenta por ID")
    public CuentaBancariaResponseDTO obtenerCuentaPorId(Long id) {
        logger.debug("Buscando cuenta bancaria con ID: {}", id);

        CuentaBancaria cuenta = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada con ID: {}", id);
                    return new CuentaBancariaNotFoundException(id);
                });

        logger.debug("Cuenta bancaria encontrada: {}", cuenta.getNumeroCuenta());
        return mapper.toResponseDTO(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "cuenta.buscar.numero.tiempo", description = "Tiempo para buscar cuenta por número")
    public CuentaBancariaResponseDTO obtenerCuentaPorNumero(String numeroCuenta) {
        logger.debug("Buscando cuenta bancaria con número: {}", numeroCuenta);

        CuentaBancaria cuenta = repository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada con número: {}", numeroCuenta);
                    return new CuentaBancariaNotFoundException(numeroCuenta, true);
                });

        logger.debug("Cuenta bancaria encontrada para titular: {}", cuenta.getTitular());
        return mapper.toResponseDTO(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CuentaBancariaResponseDTO> obtenerTodasLasCuentas(Pageable pageable) {
        logger.debug("Obteniendo todas las cuentas bancarias con paginación: {}", pageable);

        Page<CuentaBancaria> cuentasPage = repository.findAll(pageable);
        
        logger.debug("Encontradas {} cuentas en página {} de {}", 
                    cuentasPage.getNumberOfElements(), 
                    cuentasPage.getNumber() + 1, 
                    cuentasPage.getTotalPages());

        return cuentasPage.map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancariaResponseDTO> buscarCuentasPorTitular(String titular) {
        logger.debug("Buscando cuentas bancarias para titular: {}", titular);

        List<CuentaBancaria> cuentas = repository.findByTitularContainingIgnoreCase(titular);
        
        logger.debug("Encontradas {} cuentas para titular: {}", cuentas.size(), titular);
        
        return mapper.toResponseDTOList(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancariaResponseDTO> buscarCuentasPorTipo(TipoCuenta tipoCuenta) {
        logger.debug("Buscando cuentas bancarias de tipo: {}", tipoCuenta);

        List<CuentaBancaria> cuentas = repository.findByTipoCuenta(tipoCuenta);
        
        logger.debug("Encontradas {} cuentas de tipo: {}", cuentas.size(), tipoCuenta);
        
        return mapper.toResponseDTOList(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancariaResponseDTO> obtenerCuentasActivas() {
        logger.debug("Obteniendo todas las cuentas activas");

        List<CuentaBancaria> cuentasActivas = repository.findByActivaTrue();
        
        logger.debug("Encontradas {} cuentas activas", cuentasActivas.size());
        
        return mapper.toResponseDTOList(cuentasActivas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancariaResponseDTO> buscarConCriterios(String titular, TipoCuenta tipoCuenta, 
                                                            BigDecimal saldoMinimo, Boolean activa) {
        logger.debug("Buscando cuentas con criterios múltiples - Titular: {}, Tipo: {}, SaldoMin: {}, Activa: {}", 
                    titular, tipoCuenta, saldoMinimo, activa);

        String tipoStr = tipoCuenta != null ? tipoCuenta.name() : null;
        List<CuentaBancaria> cuentas = repository.findByCriteriosMultiples(titular, tipoStr, saldoMinimo, activa);
        
        logger.debug("Encontradas {} cuentas que cumplen los criterios", cuentas.size());
        
        return mapper.toResponseDTOList(cuentas);
    }

    @Override
    @Timed(value = "cuenta.actualizar.tiempo", description = "Tiempo para actualizar una cuenta")
    public CuentaBancariaResponseDTO actualizarCuenta(Long id, CuentaBancariaRequestDTO requestDTO) {
        logger.info("Actualizando cuenta bancaria con ID: {}", id);

        CuentaBancaria cuentaExistente = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada para actualizar con ID: {}", id);
                    return new CuentaBancariaNotFoundException(id);
                });

        try {
            // Actualizar solo los campos permitidos
            mapper.updateEntityFromDTO(cuentaExistente, requestDTO);
            CuentaBancaria cuentaActualizada = repository.save(cuentaExistente);

            logger.info("Cuenta bancaria actualizada exitosamente con ID: {}", id);

            return mapper.toResponseDTO(cuentaActualizada);

        } catch (Exception e) {
            logger.error("Error al actualizar cuenta bancaria con ID: {}", id, e);
            throw new RuntimeException("Error interno al actualizar la cuenta bancaria", e);
        }
    }

    @Override
    @Timed(value = "cuenta.actualizar.saldo.tiempo", description = "Tiempo para actualizar saldo")
    public CuentaBancariaResponseDTO actualizarSaldo(Long id, BigDecimal nuevoSaldo) {
        logger.info("Actualizando saldo de cuenta con ID: {} a {}", id, nuevoSaldo);

        if (nuevoSaldo == null || nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Intento de establecer saldo negativo en cuenta ID: {}, saldo: {}", id, nuevoSaldo);
            throw new IllegalArgumentException("El saldo no puede ser negativo");
        }

        CuentaBancaria cuenta = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada para actualizar saldo con ID: {}", id);
                    return new CuentaBancariaNotFoundException(id);
                });

        try {
            BigDecimal saldoAnterior = cuenta.getSaldo();
            cuenta.setSaldo(nuevoSaldo);
            CuentaBancaria cuentaActualizada = repository.save(cuenta);

            logger.info("Saldo actualizado exitosamente en cuenta ID: {} de {} a {}", 
                       id, saldoAnterior, nuevoSaldo);

            return mapper.toResponseDTO(cuentaActualizada);

        } catch (Exception e) {
            logger.error("Error al actualizar saldo de cuenta ID: {}", id, e);
            throw new RuntimeException("Error interno al actualizar el saldo", e);
        }
    }

    @Override
    @Timed(value = "cuenta.debitar.tiempo", description = "Tiempo para debitar cuenta")
    @Counted(value = "cuenta.debitar.operaciones", description = "Operaciones de débito realizadas")
    public CuentaBancariaResponseDTO debitarCuenta(Long id, BigDecimal monto) {
        logger.info("Debitando {} de la cuenta con ID: {}", monto, id);

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Intento de debitar monto inválido en cuenta ID: {}, monto: {}", id, monto);
            throw new IllegalArgumentException("El monto a debitar debe ser positivo");
        }

        CuentaBancaria cuenta = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada para débito con ID: {}", id);
                    return new CuentaBancariaNotFoundException(id);
                });

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            logger.warn("Saldo insuficiente para débito en cuenta ID: {}, saldo: {}, monto: {}", 
                       id, cuenta.getSaldo(), monto);
            throw new SaldoInsuficienteException(cuenta.getSaldo(), monto);
        }

        try {
            BigDecimal saldoAnterior = cuenta.getSaldo();
            cuenta.debitar(monto);
            CuentaBancaria cuentaActualizada = repository.save(cuenta);

            logger.info("Débito exitoso en cuenta ID: {}, saldo anterior: {}, monto debitado: {}, saldo actual: {}", 
                       id, saldoAnterior, monto, cuentaActualizada.getSaldo());

            return mapper.toResponseDTO(cuentaActualizada);

        } catch (Exception e) {
            logger.error("Error al debitar cuenta ID: {}, monto: {}", id, monto, e);
            throw new RuntimeException("Error interno al realizar el débito", e);
        }
    }

    @Override
    @Timed(value = "cuenta.acreditar.tiempo", description = "Tiempo para acreditar cuenta")
    @Counted(value = "cuenta.acreditar.operaciones", description = "Operaciones de crédito realizadas")
    public CuentaBancariaResponseDTO acreditarCuenta(Long id, BigDecimal monto) {
        logger.info("Acreditando {} a la cuenta con ID: {}", monto, id);

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Intento de acreditar monto inválido en cuenta ID: {}, monto: {}", id, monto);
            throw new IllegalArgumentException("El monto a acreditar debe ser positivo");
        }

        CuentaBancaria cuenta = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada para crédito con ID: {}", id);
                    return new CuentaBancariaNotFoundException(id);
                });

        try {
            BigDecimal saldoAnterior = cuenta.getSaldo();
            cuenta.acreditar(monto);
            CuentaBancaria cuentaActualizada = repository.save(cuenta);

            logger.info("Crédito exitoso en cuenta ID: {}, saldo anterior: {}, monto acreditado: {}, saldo actual: {}", 
                       id, saldoAnterior, monto, cuentaActualizada.getSaldo());

            return mapper.toResponseDTO(cuentaActualizada);

        } catch (Exception e) {
            logger.error("Error al acreditar cuenta ID: {}, monto: {}", id, monto, e);
            throw new RuntimeException("Error interno al realizar el crédito", e);
        }
    }

    @Override
    public CuentaBancariaResponseDTO activarCuenta(Long id) {
        logger.info("Activando cuenta con ID: {}", id);

        CuentaBancaria cuenta = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada para activar con ID: {}", id);
                    return new CuentaBancariaNotFoundException(id);
                });

        try {
            cuenta.activar();
            CuentaBancaria cuentaActualizada = repository.save(cuenta);

            logger.info("Cuenta activada exitosamente con ID: {}", id);

            return mapper.toResponseDTO(cuentaActualizada);

        } catch (Exception e) {
            logger.error("Error al activar cuenta ID: {}", id, e);
            throw new RuntimeException("Error interno al activar la cuenta", e);
        }
    }

    @Override
    public CuentaBancariaResponseDTO desactivarCuenta(Long id) {
        logger.info("Desactivando cuenta con ID: {}", id);

        CuentaBancaria cuenta = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cuenta bancaria no encontrada para desactivar con ID: {}", id);
                    return new CuentaBancariaNotFoundException(id);
                });

        try {
            cuenta.desactivar();
            CuentaBancaria cuentaActualizada = repository.save(cuenta);

            logger.info("Cuenta desactivada exitosamente con ID: {}", id);

            return mapper.toResponseDTO(cuentaActualizada);

        } catch (Exception e) {
            logger.error("Error al desactivar cuenta ID: {}", id, e);
            throw new RuntimeException("Error interno al desactivar la cuenta", e);
        }
    }

    @Override
    public void eliminarCuenta(Long id) {
        logger.info("Eliminando cuenta con ID: {}", id);

        if (!repository.existsById(id)) {
            logger.warn("Cuenta bancaria no encontrada para eliminar con ID: {}", id);
            throw new CuentaBancariaNotFoundException(id);
        }

        try {
            repository.deleteById(id);
            logger.info("Cuenta eliminada exitosamente con ID: {}", id);

        } catch (Exception e) {
            logger.error("Error al eliminar cuenta ID: {}", id, e);
            throw new RuntimeException("Error interno al eliminar la cuenta", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        logger.debug("Obteniendo estadísticas generales del sistema");

        Map<String, Object> estadisticas = new HashMap<>();
        
        try {
            Long totalCuentas = repository.count();
            Long cuentasActivas = repository.countActiveCuentas();
            BigDecimal saldoTotal = repository.getTotalSaldoActivo();

            estadisticas.put("totalCuentas", totalCuentas);
            estadisticas.put("cuentasActivas", cuentasActivas);
            estadisticas.put("cuentasInactivas", totalCuentas - cuentasActivas);
            estadisticas.put("saldoTotalActivo", saldoTotal);
            estadisticas.put("promedioSaldoPorCuenta", 
                           cuentasActivas > 0 ? saldoTotal.divide(BigDecimal.valueOf(cuentasActivas)) : BigDecimal.ZERO);

            logger.debug("Estadísticas generales obtenidas: {} cuentas totales, {} activas", 
                        totalCuentas, cuentasActivas);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas generales", e);
            throw new RuntimeException("Error interno al obtener estadísticas", e);
        }

        return estadisticas;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<TipoCuenta, Map<String, Object>> obtenerEstadisticasPorTipo() {
        logger.debug("Obteniendo estadísticas por tipo de cuenta");

        Map<TipoCuenta, Map<String, Object>> estadisticasPorTipo = new HashMap<>();
        
        try {
            List<Object[]> resultados = repository.getEstadisticasPorTipoCuenta();

            for (Object[] resultado : resultados) {
                TipoCuenta tipo = (TipoCuenta) resultado[0];
                Long cantidad = (Long) resultado[1];
                BigDecimal saldoTotal = (BigDecimal) resultado[2];

                Map<String, Object> estadisticas = new HashMap<>();
                estadisticas.put("cantidad", cantidad);
                estadisticas.put("saldoTotal", saldoTotal);
                estadisticas.put("promedioSaldo", 
                               cantidad > 0 ? saldoTotal.divide(BigDecimal.valueOf(cantidad)) : BigDecimal.ZERO);
                estadisticas.put("descripcion", tipo.getDescripcion());

                estadisticasPorTipo.put(tipo, estadisticas);
            }

            logger.debug("Estadísticas por tipo obtenidas para {} tipos de cuenta", estadisticasPorTipo.size());

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas por tipo", e);
            throw new RuntimeException("Error interno al obtener estadísticas por tipo", e);
        }

        return estadisticasPorTipo;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNumeroCuenta(String numeroCuenta) {
        logger.debug("Verificando existencia de número de cuenta: {}", numeroCuenta);
        return repository.existsByNumeroCuenta(numeroCuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerTitularesConCuentasDuplicadas() {
        logger.debug("Obteniendo titulares con cuentas duplicadas");
        
        List<String> titulares = repository.findTitularesConCuentasDuplicadas();
        
        logger.debug("Encontrados {} titulares con cuentas duplicadas", titulares.size());
        
        return titulares;
    }
} 