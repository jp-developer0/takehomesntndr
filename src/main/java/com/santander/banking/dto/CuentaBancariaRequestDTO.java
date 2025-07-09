package com.santander.banking.dto;

import com.santander.banking.entity.TipoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para las peticiones de creación y actualización de cuentas bancarias.
 */
@Schema(description = "Datos requeridos para crear o actualizar una cuenta bancaria")
public class CuentaBancariaRequestDTO {

    @Schema(description = "Número único de la cuenta bancaria", example = "1234567890", required = true)
    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Size(min = 10, max = 20, message = "El número de cuenta debe tener entre 10 y 20 caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta solo puede contener dígitos")
    private String numeroCuenta;

    @Schema(description = "Nombre completo del titular de la cuenta", example = "Juan Pérez García", required = true)
    @NotBlank(message = "El titular no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre del titular debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El titular solo puede contener letras y espacios")
    private String titular;

    @Schema(description = "Saldo inicial de la cuenta", example = "1000.50")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo no puede ser negativo")
    @Digits(integer = 13, fraction = 2, message = "El saldo debe tener máximo 13 dígitos enteros y 2 decimales")
    private BigDecimal saldo;

    @Schema(description = "Tipo de cuenta bancaria", example = "CORRIENTE", required = true)
    @NotNull(message = "El tipo de cuenta no puede ser nulo")
    private TipoCuenta tipoCuenta;

    @Schema(description = "Código de moneda ISO 4217", example = "EUR")
    @Pattern(regexp = "[A-Z]{3}", message = "La moneda debe ser un código ISO de 3 letras mayúsculas")
    private String moneda;

    // Constructor por defecto
    public CuentaBancariaRequestDTO() {
        this.saldo = BigDecimal.ZERO;
        this.moneda = "EUR";
    }

    // Constructor con parámetros
    public CuentaBancariaRequestDTO(String numeroCuenta, String titular, BigDecimal saldo, 
                                   TipoCuenta tipoCuenta, String moneda) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = saldo != null ? saldo : BigDecimal.ZERO;
        this.tipoCuenta = tipoCuenta;
        this.moneda = moneda != null ? moneda : "EUR";
    }

    // Getters y Setters
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
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    @Override
    public String toString() {
        return "CuentaBancariaRequestDTO{" +
                "numeroCuenta='" + numeroCuenta + '\'' +
                ", titular='" + titular + '\'' +
                ", saldo=" + saldo +
                ", tipoCuenta=" + tipoCuenta +
                ", moneda='" + moneda + '\'' +
                '}';
    }
} 