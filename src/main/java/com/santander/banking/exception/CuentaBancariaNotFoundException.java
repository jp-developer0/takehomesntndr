package com.santander.banking.exception;

/**
 * Excepción lanzada cuando no se encuentra una cuenta bancaria.
 */
public class CuentaBancariaNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CuentaBancariaNotFoundException(String message) {
        super(message);
    }

    public CuentaBancariaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CuentaBancariaNotFoundException(Long id) {
        super("Cuenta bancaria no encontrada con ID: " + id);
    }

    public CuentaBancariaNotFoundException(String numeroCuenta, boolean isNumeroCuenta) {
        super("Cuenta bancaria no encontrada con número: " + numeroCuenta);
    }
} 