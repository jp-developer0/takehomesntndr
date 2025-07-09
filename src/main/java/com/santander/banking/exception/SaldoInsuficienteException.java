package com.santander.banking.exception;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando se intenta realizar una operación con saldo insuficiente.
 */
public class SaldoInsuficienteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SaldoInsuficienteException(String message) {
        super(message);
    }

    public SaldoInsuficienteException(BigDecimal saldoActual, BigDecimal montoRequerido) {
        super(String.format("Saldo insuficiente. Saldo actual: %.2f, Monto requerido: %.2f", 
                           saldoActual, montoRequerido));
    }

    public SaldoInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
} 