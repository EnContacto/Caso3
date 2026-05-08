package com.hospital.pacientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un paciente no es encontrado en el sistema.
 * Retorna HTTP 404 Not Found automáticamente.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PacienteNotFoundException extends RuntimeException {

    public PacienteNotFoundException(String message) {
        super(message);
    }

    public PacienteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
