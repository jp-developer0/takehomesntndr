package com.santander.banking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.banking.dto.CuentaBancariaRequestDTO;
import com.santander.banking.entity.TipoCuenta;
import com.santander.banking.repository.CuentaBancariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para CuentaBancariaController.
 * Verifica la funcionalidad completa del API REST incluyendo validaciones, 
 * persistencia en base de datos y manejo de errores.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.org.springframework.web=DEBUG"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Tests de integración para CuentaBancariaController")
class CuentaBancariaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CuentaBancariaRepository repository;

    private CuentaBancariaRequestDTO cuentaValida;

    @BeforeEach
    void setUp() {
        // Limpiar base de datos
        repository.deleteAll();
        
        // Configurar datos de prueba
        cuentaValida = new CuentaBancariaRequestDTO();
        cuentaValida.setNumeroCuenta("1234567890");
        cuentaValida.setTitular("Juan Pérez García");
        cuentaValida.setSaldo(new BigDecimal("1000.00"));
        cuentaValida.setTipoCuenta(TipoCuenta.CORRIENTE);
        cuentaValida.setMoneda("EUR");
    }

    @Test
    @DisplayName("POST /cuentas - Debería crear cuenta exitosamente")
    void deberiaCrearCuentaExitosamente() throws Exception {
        // When & Then
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCuenta", is("1234567890")))
                .andExpect(jsonPath("$.titular", is("Juan Pérez García")))
                .andExpect(jsonPath("$.saldo", is(1000.00)))
                .andExpect(jsonPath("$.tipoCuenta", is("CORRIENTE")))
                .andExpect(jsonPath("$.moneda", is("EUR")))
                .andExpect(jsonPath("$.activa", is(true)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.fechaCreacion", notNullValue()));
    }

    @Test
    @DisplayName("POST /cuentas - Debería fallar con datos inválidos")
    void deberiaFallarConDatosInvalidos() throws Exception {
        // Given - cuenta con datos inválidos
        CuentaBancariaRequestDTO cuentaInvalida = new CuentaBancariaRequestDTO();
        cuentaInvalida.setNumeroCuenta("123"); // Muy corto
        cuentaInvalida.setTitular(""); // Vacío
        cuentaInvalida.setSaldo(new BigDecimal("-100")); // Negativo
        cuentaInvalida.setTipoCuenta(null); // Nulo
        cuentaInvalida.setMoneda("INVALID"); // Formato inválido

        // When & Then
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaInvalida)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigo", is("DATOS_INVALIDOS")))
                .andExpect(jsonPath("$.erroresCampos", notNullValue()));
    }

    @Test
    @DisplayName("POST /cuentas - Debería fallar con número de cuenta duplicado")
    void deberiaFallarConNumeroCuentaDuplicado() throws Exception {
        // Given - crear primera cuenta
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        // When & Then - intentar crear cuenta con mismo número
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigo", is("CUENTA_DUPLICADA")))
                .andExpect(jsonPath("$.mensaje", containsString("1234567890")));
    }

    @Test
    @DisplayName("GET /cuentas/{id} - Debería obtener cuenta por ID exitosamente")
    void deberiaObtenerCuentaPorIdExitosamente() throws Exception {
        // Given - crear cuenta primero
        ResultActions createResult = mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        String responseContent = createResult.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Long cuentaId = mapper.readTree(responseContent).get("id").asLong();

        // When & Then
        mockMvc.perform(get("/cuentas/{id}", cuentaId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(cuentaId.intValue())))
                .andExpect(jsonPath("$.numeroCuenta", is("1234567890")))
                .andExpect(jsonPath("$.titular", is("Juan Pérez García")));
    }

    @Test
    @DisplayName("GET /cuentas/{id} - Debería fallar con ID inexistente")
    void deberiaFallarConIdInexistente() throws Exception {
        // When & Then
        mockMvc.perform(get("/cuentas/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigo", is("CUENTA_NO_ENCONTRADA")))
                .andExpect(jsonPath("$.mensaje", containsString("999")));
    }

    @Test
    @DisplayName("GET /cuentas/numero/{numeroCuenta} - Debería obtener cuenta por número")
    void deberiaObtenerCuentaPorNumero() throws Exception {
        // Given - crear cuenta primero
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/cuentas/numero/{numeroCuenta}", "1234567890"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCuenta", is("1234567890")))
                .andExpect(jsonPath("$.titular", is("Juan Pérez García")));
    }

    @Test
    @DisplayName("GET /cuentas - Debería listar cuentas con paginación")
    void deberiaListarCuentasConPaginacion() throws Exception {
        // Given - crear múltiples cuentas
        for (int i = 1; i <= 5; i++) {
            CuentaBancariaRequestDTO cuenta = new CuentaBancariaRequestDTO();
            cuenta.setNumeroCuenta("123456789" + i);
            cuenta.setTitular("Usuario " + i);
            cuenta.setSaldo(new BigDecimal("1000.00"));
            cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
            cuenta.setMoneda("EUR");
            
            mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuenta)))
                    .andExpect(status().isCreated());
        }

        // When & Then
        mockMvc.perform(get("/cuentas")
                .param("page", "0")
                .param("size", "3")
                .param("sortBy", "titular")
                .param("sortDir", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(3)));
    }

    @Test
    @DisplayName("POST /cuentas/{id}/debitar - Debería debitar cuenta exitosamente")
    void deberiaDebitarCuentaExitosamente() throws Exception {
        // Given - crear cuenta primero
        ResultActions createResult = mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        String responseContent = createResult.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Long cuentaId = mapper.readTree(responseContent).get("id").asLong();

        // When & Then
        mockMvc.perform(post("/cuentas/{id}/debitar", cuentaId)
                .param("monto", "250.00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.saldo", is(750.00)))
                .andExpect(jsonPath("$.id", is(cuentaId.intValue())));
    }

    @Test
    @DisplayName("POST /cuentas/{id}/debitar - Debería fallar por saldo insuficiente")
    void deberiaFallarPorSaldoInsuficiente() throws Exception {
        // Given - crear cuenta primero
        ResultActions createResult = mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        String responseContent = createResult.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Long cuentaId = mapper.readTree(responseContent).get("id").asLong();

        // When & Then - intentar debitar más del saldo disponible
        mockMvc.perform(post("/cuentas/{id}/debitar", cuentaId)
                .param("monto", "2000.00"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigo", is("SALDO_INSUFICIENTE")));
    }

    @Test
    @DisplayName("POST /cuentas/{id}/acreditar - Debería acreditar cuenta exitosamente")
    void deberiaAcreditarCuentaExitosamente() throws Exception {
        // Given - crear cuenta primero
        ResultActions createResult = mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        String responseContent = createResult.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Long cuentaId = mapper.readTree(responseContent).get("id").asLong();

        // When & Then
        mockMvc.perform(post("/cuentas/{id}/acreditar", cuentaId)
                .param("monto", "500.00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.saldo", is(1500.00)))
                .andExpect(jsonPath("$.id", is(cuentaId.intValue())));
    }

    @Test
    @DisplayName("PATCH /cuentas/{id}/desactivar - Debería desactivar cuenta")
    void deberiaDesactivarCuenta() throws Exception {
        // Given - crear cuenta primero
        ResultActions createResult = mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        String responseContent = createResult.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Long cuentaId = mapper.readTree(responseContent).get("id").asLong();

        // When & Then
        mockMvc.perform(patch("/cuentas/{id}/desactivar", cuentaId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.activa", is(false)))
                .andExpect(jsonPath("$.id", is(cuentaId.intValue())));
    }

    @Test
    @DisplayName("DELETE /cuentas/{id} - Debería eliminar cuenta exitosamente")
    void deberiaEliminarCuentaExitosamente() throws Exception {
        // Given - crear cuenta primero
        ResultActions createResult = mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        String responseContent = createResult.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Long cuentaId = mapper.readTree(responseContent).get("id").asLong();

        // When & Then
        mockMvc.perform(delete("/cuentas/{id}", cuentaId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verificar que la cuenta ya no existe
        mockMvc.perform(get("/cuentas/{id}", cuentaId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /cuentas/estadisticas - Debería obtener estadísticas del sistema")
    void deberiaObtenerEstadisticasDelSistema() throws Exception {
        // Given - crear varias cuentas
        for (int i = 1; i <= 3; i++) {
            CuentaBancariaRequestDTO cuenta = new CuentaBancariaRequestDTO();
            cuenta.setNumeroCuenta("123456789" + i);
            cuenta.setTitular("Usuario " + i);
            cuenta.setSaldo(new BigDecimal("1000.00"));
            cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
            cuenta.setMoneda("EUR");
            
            mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuenta)))
                    .andExpect(status().isCreated());
        }

        // When & Then
        mockMvc.perform(get("/cuentas/estadisticas"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCuentas", is(3)))
                .andExpect(jsonPath("$.cuentasActivas", is(3)))
                .andExpect(jsonPath("$.cuentasInactivas", is(0)))
                .andExpect(jsonPath("$.saldoTotalActivo", is(3000.0)));
    }

    @Test
    @DisplayName("GET /cuentas/buscar/titular - Debería buscar cuentas por titular")
    void deberiaBuscarCuentasPorTitular() throws Exception {
        // Given - crear cuentas con diferentes titulares
        String[] titulares = {"Juan Pérez", "María García", "Juan López"};
        for (int i = 0; i < titulares.length; i++) {
            CuentaBancariaRequestDTO cuenta = new CuentaBancariaRequestDTO();
            cuenta.setNumeroCuenta("123456789" + i);
            cuenta.setTitular(titulares[i]);
            cuenta.setSaldo(new BigDecimal("1000.00"));
            cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
            cuenta.setMoneda("EUR");
            
            mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuenta)))
                    .andExpect(status().isCreated());
        }

        // When & Then - buscar por "Juan" debería encontrar 2 cuentas
        mockMvc.perform(get("/cuentas/buscar/titular")
                .param("titular", "Juan"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].titular", hasItems("Juan Pérez", "Juan López")));
    }

    @Test
    @DisplayName("GET /cuentas/buscar/tipo - Debería buscar cuentas por tipo")
    void deberiaBuscarCuentasPorTipo() throws Exception {
        // Given - crear cuentas de diferentes tipos
        TipoCuenta[] tipos = {TipoCuenta.CORRIENTE, TipoCuenta.AHORROS, TipoCuenta.CORRIENTE};
        for (int i = 0; i < tipos.length; i++) {
            CuentaBancariaRequestDTO cuenta = new CuentaBancariaRequestDTO();
            cuenta.setNumeroCuenta("123456789" + i);
            cuenta.setTitular("Usuario " + i);
            cuenta.setSaldo(new BigDecimal("1000.00"));
            cuenta.setTipoCuenta(tipos[i]);
            cuenta.setMoneda("EUR");
            
            mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuenta)))
                    .andExpect(status().isCreated());
        }

        // When & Then - buscar por CORRIENTE debería encontrar 2 cuentas
        mockMvc.perform(get("/cuentas/buscar/tipo")
                .param("tipoCuenta", "CORRIENTE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].tipoCuenta", hasItems("CORRIENTE", "CORRIENTE")));
    }

    @Test
    @DisplayName("GET /cuentas/existe/{numeroCuenta} - Debería verificar existencia de cuenta")
    void deberiaVerificarExistenciaDeCuenta() throws Exception {
        // Given - crear cuenta
        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuentaValida)))
                .andExpect(status().isCreated());

        // When & Then - verificar que existe
        mockMvc.perform(get("/cuentas/existe/{numeroCuenta}", "1234567890"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // When & Then - verificar que no existe
        mockMvc.perform(get("/cuentas/existe/{numeroCuenta}", "9999999999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
} 