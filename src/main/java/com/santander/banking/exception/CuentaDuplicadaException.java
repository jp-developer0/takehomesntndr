package com.santander.banking.exception;

/**
 * Excepción lanzada cuando se intenta crear una cuenta con un número que ya existe.
 */
public class CuentaDuplicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CuentaDuplicadaException(String message) {
        super(message);
    }

    public CuentaDuplicadaException(String message, Throwable cause) {
        super(message, cause);
    }
} 