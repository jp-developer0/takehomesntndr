package com.santander.banking.dto;

import com.santander.banking.entity.TipoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para las respuestas de consulta de cuentas bancarias.
 */
@Schema(description = "Información completa de una cuenta bancaria")
public class CuentaBancariaResponseDTO {

    @Schema(description = "Identificador único de la cuenta", example = "1")
    private Long id;

    @Schema(description = "Número único de la cuenta bancaria", example = "1234567890")
    private String numeroCuenta;

    @Schema(description = "Nombre completo del titular de la cuenta", example = "Juan Pérez García")
    private String titular;

    @Schema(description = "Saldo actual de la cuenta", example = "1000.50")
    private BigDecimal saldo;

    @Schema(description = "Tipo de cuenta bancaria", example = "CORRIENTE")
    private TipoCuenta tipoCuenta;

    @Schema(description = "Descripción del tipo de cuenta", example = "Cuenta Corriente")
    private String descripcionTipoCuenta;

    @Schema(description = "Código de moneda ISO 4217", example = "EUR")
    private String moneda;

    @Schema(description = "Fecha de creación de la cuenta", example = "2023-01-15T10:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última actualización", example = "2023-01-20T14:45:00")
    private LocalDateTime fechaActualizacion;

    @Schema(description = "Indica si la cuenta está activa", example = "true")
    private Boolean activa;

    @Schema(description = "Comisión de mantenimiento mensual", example = "5.0")
    private Double comisionMantenimiento;

    // Constructor por defecto
    public CuentaBancariaResponseDTO() {}

    // Constructor completo
    public CuentaBancariaResponseDTO(Long id, String numeroCuenta, String titular, 
                                   BigDecimal saldo, TipoCuenta tipoCuenta, String moneda,
                                   LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                                   Boolean activa) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = saldo;
        this.tipoCuenta = tipoCuenta;
        this.descripcionTipoCuenta = tipoCuenta != null ? tipoCuenta.getDescripcion() : null;
        this.moneda = moneda;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.activa = activa;
        this.comisionMantenimiento = tipoCuenta != null ? tipoCuenta.getComisionMantenimiento() : 0.0;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
        this.descripcionTipoCuenta = tipoCuenta != null ? tipoCuenta.getDescripcion() : null;
        this.comisionMantenimiento = tipoCuenta != null ? tipoCuenta.getComisionMantenimiento() : 0.0;
    }

    public String getDescripcionTipoCuenta() {
        return descripcionTipoCuenta;
    }

    public void setDescripcionTipoCuenta(String descripcionTipoCuenta) {
        this.descripcionTipoCuenta = descripcionTipoCuenta;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public Double getComisionMantenimiento() {
        return comisionMantenimiento;
    }

    public void setComisionMantenimiento(Double comisionMantenimiento) {
        this.comisionMantenimiento = comisionMantenimiento;
    }

    @Override
    public String toString() {
        return "CuentaBancariaResponseDTO{" +
                "id=" + id +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", titular='" + titular + '\'' +
                ", saldo=" + saldo +
                ", tipoCuenta=" + tipoCuenta +
                ", moneda='" + moneda + '\'' +
                ", activa=" + activa +
                '}';
    }
} 