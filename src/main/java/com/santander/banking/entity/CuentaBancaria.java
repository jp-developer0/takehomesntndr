package com.santander.banking.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una cuenta bancaria en el sistema.
 * Implementa el patrón Builder para la creación de instancias.
 */
@Entity
@Table(name = "cuentas_bancarias", 
       uniqueConstraints = @UniqueConstraint(columnNames = "numero_cuenta"))
public class CuentaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 20)
    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Size(min = 10, max = 20, message = "El número de cuenta debe tener entre 10 y 20 caracteres")
    private String numeroCuenta;

    @Column(name = "titular", nullable = false, length = 100)
    @NotBlank(message = "El titular no puede estar vacío")
    @Size(max = 100, message = "El nombre del titular no puede exceder 100 caracteres")
    private String titular;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El saldo no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo no puede ser negativo")
    @Digits(integer = 13, fraction = 2, message = "El saldo debe tener máximo 13 dígitos enteros y 2 decimales")
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false)
    @NotNull(message = "El tipo de cuenta no puede ser nulo")
    private TipoCuenta tipoCuenta;

    @Column(name = "moneda", nullable = false, length = 3)
    @NotBlank(message = "La moneda no puede estar vacía")
    @Pattern(regexp = "[A-Z]{3}", message = "La moneda debe ser un código ISO de 3 letras mayúsculas")
    private String moneda;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    // Constructor sin parámetros requerido por JPA
    protected CuentaBancaria() {}

    // Constructor privado para usar con Builder
    private CuentaBancaria(Builder builder) {
        this.numeroCuenta = builder.numeroCuenta;
        this.titular = builder.titular;
        this.saldo = builder.saldo;
        this.tipoCuenta = builder.tipoCuenta;
        this.moneda = builder.moneda;
        this.fechaCreacion = builder.fechaCreacion;
        this.fechaActualizacion = builder.fechaActualizacion;
        this.activa = builder.activa;
    }

    // Getters
    public Long getId() { return id; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public String getTitular() { return titular; }
    public BigDecimal getSaldo() { return saldo; }
    public TipoCuenta getTipoCuenta() { return tipoCuenta; }
    public String getMoneda() { return moneda; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public Boolean getActiva() { return activa; }

    // Setters con validaciones de negocio
    public void setSaldo(BigDecimal nuevoSaldo) {
        if (nuevoSaldo != null && nuevoSaldo.compareTo(BigDecimal.ZERO) >= 0) {
            this.saldo = nuevoSaldo;
            this.fechaActualizacion = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("El saldo no puede ser negativo");
        }
    }

    public void setTitular(String nuevoTitular) {
        if (nuevoTitular != null && !nuevoTitular.trim().isEmpty()) {
            this.titular = nuevoTitular.trim();
            this.fechaActualizacion = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("El titular no puede estar vacío");
        }
    }

    public void activar() {
        this.activa = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void desactivar() {
        this.activa = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de negocio
    public void debitar(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a debitar debe ser positivo");
        }
        if (this.saldo.compareTo(monto) < 0) {
            throw new IllegalStateException("Saldo insuficiente para realizar la operación");
        }
        this.saldo = this.saldo.subtract(monto);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void acreditar(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a acreditar debe ser positivo");
        }
        this.saldo = this.saldo.add(monto);
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.activa == null) {
            this.activa = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuentaBancaria that = (CuentaBancaria) o;
        return Objects.equals(numeroCuenta, that.numeroCuenta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroCuenta);
    }

    @Override
    public String toString() {
        return "CuentaBancaria{" +
                "id=" + id +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", titular='" + titular + '\'' +
                ", saldo=" + saldo +
                ", tipoCuenta=" + tipoCuenta +
                ", moneda='" + moneda + '\'' +
                ", activa=" + activa +
                '}';
    }

    // Patrón Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String numeroCuenta;
        private String titular;
        private BigDecimal saldo = BigDecimal.ZERO;
        private TipoCuenta tipoCuenta;
        private String moneda = "EUR";
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
        private Boolean activa = true;

        private Builder() {}

        public Builder numeroCuenta(String numeroCuenta) {
            this.numeroCuenta = numeroCuenta;
            return this;
        }

        public Builder titular(String titular) {
            this.titular = titular;
            return this;
        }

        public Builder saldo(BigDecimal saldo) {
            this.saldo = saldo;
            return this;
        }

        public Builder tipoCuenta(TipoCuenta tipoCuenta) {
            this.tipoCuenta = tipoCuenta;
            return this;
        }

        public Builder moneda(String moneda) {
            this.moneda = moneda;
            return this;
        }

        public Builder activa(Boolean activa) {
            this.activa = activa;
            return this;
        }

        public CuentaBancaria build() {
            // Validaciones básicas antes de construir
            if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
                throw new IllegalArgumentException("El número de cuenta es obligatorio");
            }
            if (titular == null || titular.trim().isEmpty()) {
                throw new IllegalArgumentException("El titular es obligatorio");
            }
            if (tipoCuenta == null) {
                throw new IllegalArgumentException("El tipo de cuenta es obligatorio");
            }
            
            return new CuentaBancaria(this);
        }
    }
} 