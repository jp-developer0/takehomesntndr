package com.santander.banking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manejador global de excepciones para la aplicación bancaria.
 * Centraliza el manejo de errores y proporciona respuestas consistentes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones cuando no se encuentra una cuenta bancaria.
     */
    @ExceptionHandler(CuentaBancariaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCuentaBancariaNotFoundException(
            CuentaBancariaNotFoundException ex, WebRequest request) {
        
        logger.warn("Cuenta bancaria no encontrada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "CUENTA_NO_ENCONTRADA",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones cuando se intenta crear una cuenta duplicada.
     */
    @ExceptionHandler(CuentaDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleCuentaDuplicadaException(
            CuentaDuplicadaException ex, WebRequest request) {
        
        logger.warn("Intento de crear cuenta duplicada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "CUENTA_DUPLICADA",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones de saldo insuficiente.
     */
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleSaldoInsuficienteException(
            SaldoInsuficienteException ex, WebRequest request) {
        
        logger.warn("Operación con saldo insuficiente: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "SALDO_INSUFICIENTE",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores de validación de argumentos de métodos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Error de validación en argumentos: {}", ex.getMessage());
        
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> fieldErrors = new HashMap<>();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "DATOS_INVALIDOS",
                "Los datos proporcionados no son válidos",
                request.getDescription(false),
                LocalDateTime.now(),
                fieldErrors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja violaciones de restricciones de validación.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        logger.warn("Violación de restricciones: {}", ex.getMessage());
        
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> fieldErrors = new HashMap<>();
        
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fieldErrors.put(fieldName, message);
        }
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "RESTRICCIONES_VIOLADAS",
                "Se violaron las restricciones de validación",
                request.getDescription(false),
                LocalDateTime.now(),
                fieldErrors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja argumentos ilegales.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ARGUMENTO_INVALIDO",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja estados ilegales de la aplicación.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        
        logger.warn("Estado ilegal de la aplicación: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "ESTADO_INVALIDO",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones genéricas no contempladas específicamente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Error interno del servidor: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "ERROR_INTERNO",
                "Ha ocurrido un error interno del servidor",
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Clase para representar respuestas de error estándar.
     */
    public static class ErrorResponse {
        private int status;
        private String codigo;
        private String mensaje;
        private String path;
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String codigo, String mensaje, String path, LocalDateTime timestamp) {
            this.status = status;
            this.codigo = codigo;
            this.mensaje = mensaje;
            this.path = path;
            this.timestamp = timestamp;
        }

        // Getters y setters
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    /**
     * Clase especializada para errores de validación con detalles de campos.
     */
    public static class ValidationErrorResponse extends ErrorResponse {
        private Map<String, String> erroresCampos;

        public ValidationErrorResponse(int status, String codigo, String mensaje, String path, 
                                     LocalDateTime timestamp, Map<String, String> erroresCampos) {
            super(status, codigo, mensaje, path, timestamp);
            this.erroresCampos = erroresCampos;
        }

        public Map<String, String> getErroresCampos() { return erroresCampos; }
        public void setErroresCampos(Map<String, String> erroresCampos) { this.erroresCampos = erroresCampos; }
    }
} 