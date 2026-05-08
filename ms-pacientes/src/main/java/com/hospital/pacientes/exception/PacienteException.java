package com.hospital.pacientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de negocio para el microservicio de pacientes.
 * Retorna HTTP 409 Conflict para conflictos de datos (ej: cédula duplicada).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PacienteException extends RuntimeException {

    public PacienteException(String message) {
        super(message);
    }

    public PacienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
