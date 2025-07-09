package com.santander.banking.unit;

import com.santander.banking.dto.CuentaBancariaRequestDTO;
import com.santander.banking.dto.CuentaBancariaResponseDTO;
import com.santander.banking.entity.CuentaBancaria;
import com.santander.banking.entity.TipoCuenta;
import com.santander.banking.exception.CuentaBancariaNotFoundException;
import com.santander.banking.exception.CuentaDuplicadaException;
import com.santander.banking.exception.SaldoInsuficienteException;
import com.santander.banking.repository.CuentaBancariaRepository;
import com.santander.banking.service.impl.CuentaBancariaServiceImpl;
import com.santander.banking.util.CuentaBancariaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CuentaBancariaServiceImpl.
 * Verifica toda la lógica de negocio del servicio bancario.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para CuentaBancariaService")
class CuentaBancariaServiceImplTest {

    @Mock
    private CuentaBancariaRepository repository;

    @Mock
    private CuentaBancariaMapper mapper;

    @InjectMocks
    private CuentaBancariaServiceImpl service;

    private CuentaBancariaRequestDTO requestDTO;
    private CuentaBancaria cuentaEntity;
    private CuentaBancariaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        requestDTO = new CuentaBancariaRequestDTO();
        requestDTO.setNumeroCuenta("1234567890");
        requestDTO.setTitular("Juan Pérez");
        requestDTO.setSaldo(new BigDecimal("1000.00"));
        requestDTO.setTipoCuenta(TipoCuenta.CORRIENTE);
        requestDTO.setMoneda("EUR");

        cuentaEntity = CuentaBancaria.builder()
                .numeroCuenta("1234567890")
                .titular("Juan Pérez")
                .saldo(new BigDecimal("1000.00"))
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .moneda("EUR")
                .build();

        responseDTO = new CuentaBancariaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNumeroCuenta("1234567890");
        responseDTO.setTitular("Juan Pérez");
        responseDTO.setSaldo(new BigDecimal("1000.00"));
        responseDTO.setTipoCuenta(TipoCuenta.CORRIENTE);
        responseDTO.setMoneda("EUR");
        responseDTO.setActiva(true);
    }

    @Test
    @DisplayName("Debería crear cuenta exitosamente cuando los datos son válidos")
    void deberiaCrearCuentaExitosamente() {
        // Given
        when(mapper.isValidRequestDTO(requestDTO)).thenReturn(true);
        when(repository.existsByNumeroCuenta(requestDTO.getNumeroCuenta())).thenReturn(false);
        when(mapper.toEntity(requestDTO)).thenReturn(cuentaEntity);
        when(repository.save(cuentaEntity)).thenReturn(cuentaEntity);
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        CuentaBancariaResponseDTO resultado = service.crearCuenta(requestDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNumeroCuenta()).isEqualTo("1234567890");
        assertThat(resultado.getTitular()).isEqualTo("Juan Pérez");
        
        verify(repository).existsByNumeroCuenta("1234567890");
        verify(repository).save(cuentaEntity);
        verify(mapper).toResponseDTO(cuentaEntity);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el DTO es inválido")
    void deberiaLanzarExcepcionCuandoDTOEsInvalido() {
        // Given
        when(mapper.isValidRequestDTO(requestDTO)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.crearCuenta(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los datos proporcionados no son válidos");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando número de cuenta ya existe")
    void deberiaLanzarExcepcionCuandoNumeroCuentaYaExiste() {
        // Given
        when(mapper.isValidRequestDTO(requestDTO)).thenReturn(true);
        when(repository.existsByNumeroCuenta(requestDTO.getNumeroCuenta())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> service.crearCuenta(requestDTO))
                .isInstanceOf(CuentaDuplicadaException.class);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debería obtener cuenta por ID exitosamente")
    void deberiaObtenerCuentaPorIdExitosamente() {
        // Given
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(cuentaEntity));
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        CuentaBancariaResponseDTO resultado = service.obtenerCuentaPorId(id);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        
        verify(repository).findById(id);
        verify(mapper).toResponseDTO(cuentaEntity);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando cuenta no existe por ID")
    void deberiaLanzarExcepcionCuandoCuentaNoExistePorId() {
        // Given
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.obtenerCuentaPorId(id))
                .isInstanceOf(CuentaBancariaNotFoundException.class);

        verify(repository).findById(id);
        verify(mapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debería obtener cuenta por número exitosamente")
    void deberiaObtenerCuentaPorNumeroExitosamente() {
        // Given
        String numeroCuenta = "1234567890";
        when(repository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(cuentaEntity));
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        CuentaBancariaResponseDTO resultado = service.obtenerCuentaPorNumero(numeroCuenta);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNumeroCuenta()).isEqualTo(numeroCuenta);
        
        verify(repository).findByNumeroCuenta(numeroCuenta);
    }

    @Test
    @DisplayName("Debería obtener todas las cuentas con paginación")
    void deberiaObtenerTodasLasCuentasConPaginacion() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CuentaBancaria> cuentas = Arrays.asList(cuentaEntity);
        Page<CuentaBancaria> page = new PageImpl<>(cuentas, pageable, 1);
        
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        Page<CuentaBancariaResponseDTO> resultado = service.obtenerTodasLasCuentas(pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        
        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("Debería debitar cuenta exitosamente")
    void deberiaDebitarCuentaExitosamente() {
        // Given
        Long id = 1L;
        BigDecimal monto = new BigDecimal("100.00");
        cuentaEntity.setSaldo(new BigDecimal("1000.00"));
        
        when(repository.findById(id)).thenReturn(Optional.of(cuentaEntity));
        when(repository.save(cuentaEntity)).thenReturn(cuentaEntity);
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        CuentaBancariaResponseDTO resultado = service.debitarCuenta(id, monto);

        // Then
        assertThat(resultado).isNotNull();
        verify(repository).findById(id);
        verify(repository).save(cuentaEntity);
        
        // Verificar que el saldo se redujo
        assertThat(cuentaEntity.getSaldo()).isEqualTo(new BigDecimal("900.00"));
    }

    @Test
    @DisplayName("Debería lanzar excepción por saldo insuficiente al debitar")
    void deberiaLanzarExcepcionPorSaldoInsuficienteAlDebitar() {
        // Given
        Long id = 1L;
        BigDecimal monto = new BigDecimal("2000.00"); // Mayor al saldo disponible
        cuentaEntity.setSaldo(new BigDecimal("1000.00"));
        
        when(repository.findById(id)).thenReturn(Optional.of(cuentaEntity));

        // When & Then
        assertThatThrownBy(() -> service.debitarCuenta(id, monto))
                .isInstanceOf(SaldoInsuficienteException.class);

        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción por monto inválido al debitar")
    void deberiaLanzarExcepcionPorMontoInvalidoAlDebitar() {
        // Given
        Long id = 1L;
        BigDecimal montoNegativo = new BigDecimal("-100.00");

        // When & Then
        assertThatThrownBy(() -> service.debitarCuenta(id, montoNegativo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El monto a debitar debe ser positivo");

        verify(repository, never()).findById(any());
    }

    @Test
    @DisplayName("Debería acreditar cuenta exitosamente")
    void deberiaAcreditarCuentaExitosamente() {
        // Given
        Long id = 1L;
        BigDecimal monto = new BigDecimal("500.00");
        cuentaEntity.setSaldo(new BigDecimal("1000.00"));
        
        when(repository.findById(id)).thenReturn(Optional.of(cuentaEntity));
        when(repository.save(cuentaEntity)).thenReturn(cuentaEntity);
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        CuentaBancariaResponseDTO resultado = service.acreditarCuenta(id, monto);

        // Then
        assertThat(resultado).isNotNull();
        verify(repository).findById(id);
        verify(repository).save(cuentaEntity);
        
        // Verificar que el saldo aumentó
        assertThat(cuentaEntity.getSaldo()).isEqualTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Debería activar cuenta exitosamente")
    void deberiaActivarCuentaExitosamente() {
        // Given
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(cuentaEntity));
        when(repository.save(cuentaEntity)).thenReturn(cuentaEntity);
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        CuentaBancariaResponseDTO resultado = service.activarCuenta(id);

        // Then
        assertThat(resultado).isNotNull();
        verify(repository).findById(id);
        verify(repository).save(cuentaEntity);
    }

    @Test
    @DisplayName("Debería desactivar cuenta exitosamente")
    void deberiaDesactivarCuentaExitosamente() {
        // Given
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(cuentaEntity));
        when(repository.save(cuentaEntity)).thenReturn(cuentaEntity);
        when(mapper.toResponseDTO(cuentaEntity)).thenReturn(responseDTO);

        // When
        CuentaBancariaResponseDTO resultado = service.desactivarCuenta(id);

        // Then
        assertThat(resultado).isNotNull();
        verify(repository).findById(id);
        verify(repository).save(cuentaEntity);
    }

    @Test
    @DisplayName("Debería eliminar cuenta exitosamente")
    void deberiaEliminarCuentaExitosamente() {
        // Given
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        // When
        service.eliminarCuenta(id);

        // Then
        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar cuenta inexistente")
    void deberiaLanzarExcepcionAlEliminarCuentaInexistente() {
        // Given
        Long id = 999L;
        when(repository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.eliminarCuenta(id))
                .isInstanceOf(CuentaBancariaNotFoundException.class);

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debería obtener estadísticas correctamente")
    void deberiaObtenerEstadisticasCorrectamente() {
        // Given
        when(repository.count()).thenReturn(10L);
        when(repository.countActiveCuentas()).thenReturn(8L);
        when(repository.getTotalSaldoActivo()).thenReturn(new BigDecimal("50000.00"));

        // When
        Map<String, Object> estadisticas = service.obtenerEstadisticas();

        // Then
        assertThat(estadisticas).isNotNull();
        assertThat(estadisticas.get("totalCuentas")).isEqualTo(10L);
        assertThat(estadisticas.get("cuentasActivas")).isEqualTo(8L);
        assertThat(estadisticas.get("cuentasInactivas")).isEqualTo(2L);
        assertThat(estadisticas.get("saldoTotalActivo")).isEqualTo(new BigDecimal("50000.00"));
        
        verify(repository).count();
        verify(repository).countActiveCuentas();
        verify(repository).getTotalSaldoActivo();
    }

    @Test
    @DisplayName("Debería verificar existencia de número de cuenta")
    void deberiaVerificarExistenciaDeNumeroCuenta() {
        // Given
        String numeroCuenta = "1234567890";
        when(repository.existsByNumeroCuenta(numeroCuenta)).thenReturn(true);

        // When
        boolean existe = service.existeNumeroCuenta(numeroCuenta);

        // Then
        assertThat(existe).isTrue();
        verify(repository).existsByNumeroCuenta(numeroCuenta);
    }

    @Test
    @DisplayName("Debería buscar cuentas por titular")
    void deberiaBuscarCuentasPorTitular() {
        // Given
        String titular = "Juan";
        List<CuentaBancaria> cuentas = Arrays.asList(cuentaEntity);
        List<CuentaBancariaResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        
        when(repository.findByTitularContainingIgnoreCase(titular)).thenReturn(cuentas);
        when(mapper.toResponseDTOList(cuentas)).thenReturn(responseDTOs);

        // When
        List<CuentaBancariaResponseDTO> resultado = service.buscarCuentasPorTitular(titular);

        // Then
        assertThat(resultado).isNotNull().hasSize(1);
        verify(repository).findByTitularContainingIgnoreCase(titular);
        verify(mapper).toResponseDTOList(cuentas);
    }

    @Test
    @DisplayName("Debería buscar cuentas por tipo")
    void deberiaBuscarCuentasPorTipo() {
        // Given
        TipoCuenta tipo = TipoCuenta.CORRIENTE;
        List<CuentaBancaria> cuentas = Arrays.asList(cuentaEntity);
        List<CuentaBancariaResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        
        when(repository.findByTipoCuenta(tipo)).thenReturn(cuentas);
        when(mapper.toResponseDTOList(cuentas)).thenReturn(responseDTOs);

        // When
        List<CuentaBancariaResponseDTO> resultado = service.buscarCuentasPorTipo(tipo);

        // Then
        assertThat(resultado).isNotNull().hasSize(1);
        verify(repository).findByTipoCuenta(tipo);
        verify(mapper).toResponseDTOList(cuentas);
    }
} 