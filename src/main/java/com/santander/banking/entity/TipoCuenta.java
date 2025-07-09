package com.santander.banking.entity;

/**
 * Enumeración que define los tipos de cuenta bancaria disponibles en el sistema.
 */
public enum TipoCuenta {
    CORRIENTE("Cuenta Corriente", "Cuenta para operaciones comerciales y personales"),
    AHORROS("Cuenta de Ahorros", "Cuenta para ahorro personal con rendimientos"),
    NOMINA("Cuenta Nómina", "Cuenta para recepción de salarios"),
    EMPRESARIAL("Cuenta Empresarial", "Cuenta para operaciones comerciales de empresas"),
    ESTUDIANTE("Cuenta Estudiante", "Cuenta especial para estudiantes con beneficios");

    private final String descripcion;
    private final String detalle;

    TipoCuenta(String descripcion, String detalle) {
        this.descripcion = descripcion;
        this.detalle = detalle;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDetalle() {
        return detalle;
    }

    /**
     * Determina si el tipo de cuenta permite sobregiros.
     */
    public boolean permitesobregiro() {
        return this == CORRIENTE || this == EMPRESARIAL;
    }

    /**
     * Obtiene la comisión de mantenimiento mensual por tipo de cuenta.
     */
    public double getComisionMantenimiento() {
        switch (this) {
            case CORRIENTE:
                return 5.0;
            case AHORROS:
                return 2.0;
            case NOMINA:
                return 0.0;
            case EMPRESARIAL:
                return 15.0;
            case ESTUDIANTE:
                return 0.0;
            default:
                return 0.0;
        }
    }
} 