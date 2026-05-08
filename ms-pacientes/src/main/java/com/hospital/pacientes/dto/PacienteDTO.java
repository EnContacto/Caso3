package com.hospital.pacientes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.pacientes.model.Paciente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clases DTO (Data Transfer Object) para el microservicio de Pacientes.
 *
 * <p>Separan la capa de persistencia (entidades JPA) de la capa de
 * presentación (API REST), protegiendo los campos internos y
 * controlando qué datos se exponen al exterior.</p>
 */
public class PacienteDTO {

    // ===================================================================
    // REQUEST DTO - Creación de paciente
    // ===================================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "PacienteRequest", description = "Datos para registrar o actualizar un paciente")
    public static class Request {

        @NotBlank(message = "La cédula es obligatoria")
        @Size(min = 10, max = 13, message = "La cédula debe tener entre 10 y 13 caracteres")
        @Pattern(regexp = "^[0-9]+$", message = "La cédula solo debe contener números")
        @Schema(description = "Número de cédula o documento", example = "0801234567")
        private String cedula;

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
        @Schema(description = "Nombres del paciente", example = "Juan Carlos")
        private String nombres;

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
        @Schema(description = "Apellidos del paciente", example = "Pérez Gómez")
        private String apellidos;

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Fecha de nacimiento (yyyy-MM-dd)", example = "1985-03-15")
        private LocalDate fechaNacimiento;

        @NotNull(message = "El género es obligatorio")
        @Schema(description = "Género del paciente", example = "MASCULINO",
                allowableValues = {"MASCULINO", "FEMENINO", "OTRO"})
        private Paciente.Genero genero;

        @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "Formato de teléfono inválido")
        @Schema(description = "Número de teléfono", example = "0991234567")
        private String telefono;

        @Email(message = "El formato del email es inválido")
        @Schema(description = "Correo electrónico", example = "juan.perez@email.com")
        private String email;

        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        @Schema(description = "Dirección de residencia", example = "Av. Principal 123, Quito")
        private String direccion;

        @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Tipo de sangre inválido. Ej: A+, O-, AB+")
        @Schema(description = "Tipo de sangre", example = "O+",
                allowableValues = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"})
        private String tipoSangre;

        @Schema(description = "Alergias conocidas del paciente", example = "Penicilina, Aspirina")
        private String alergias;
    }

    // ===================================================================
    // RESPONSE DTO - Respuesta completa
    // ===================================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "PacienteResponse", description = "Datos completos del paciente")
    public static class Response {

        @Schema(description = "ID interno del paciente", example = "1")
        private Long id;

        @Schema(description = "Número de cédula", example = "0801234567")
        private String cedula;

        @Schema(description = "Nombres del paciente", example = "Juan Carlos")
        private String nombres;

        @Schema(description = "Apellidos del paciente", example = "Pérez Gómez")
        private String apellidos;

        @Schema(description = "Nombre completo", example = "Juan Carlos Pérez Gómez")
        private String nombreCompleto;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Fecha de nacimiento", example = "1985-03-15")
        private LocalDate fechaNacimiento;

        @Schema(description = "Edad calculada en años", example = "39")
        private Integer edad;

        @Schema(description = "Género", example = "MASCULINO")
        private Paciente.Genero genero;

        @Schema(description = "Teléfono", example = "0991234567")
        private String telefono;

        @Schema(description = "Email", example = "juan.perez@email.com")
        private String email;

        @Schema(description = "Dirección", example = "Av. Principal 123, Quito")
        private String direccion;

        @Schema(description = "Tipo de sangre", example = "O+")
        private String tipoSangre;

        @Schema(description = "Alergias", example = "Penicilina")
        private String alergias;

        @Schema(description = "Estado activo del paciente", example = "true")
        private Boolean activo;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Fecha de registro en el sistema")
        private LocalDateTime fechaRegistro;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Última actualización del registro")
        private LocalDateTime fechaActualizacion;
    }

    // ===================================================================
    // RESPONSE DTO - Resumen para listas y llamadas entre microservicios
    // ===================================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "PacienteSummary", description = "Resumen básico del paciente para uso entre microservicios")
    public static class Summary {

        @Schema(description = "ID del paciente", example = "1")
        private Long id;

        @Schema(description = "Cédula del paciente", example = "0801234567")
        private String cedula;

        @Schema(description = "Nombre completo", example = "Juan Carlos Pérez Gómez")
        private String nombreCompleto;

        @Schema(description = "Teléfono", example = "0991234567")
        private String telefono;

        @Schema(description = "Email", example = "juan.perez@email.com")
        private String email;

        @Schema(description = "Tipo de sangre", example = "O+")
        private String tipoSangre;

        @Schema(description = "Estado activo", example = "true")
        private Boolean activo;
    }

    // ===================================================================
    // RESPONSE DTO - Respuesta estándar paginada
    // ===================================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ApiResponse", description = "Respuesta estándar de la API")
    public static class ApiResponse<T> {

        @Schema(description = "Código de respuesta HTTP", example = "200")
        private int status;

        @Schema(description = "Mensaje descriptivo", example = "Paciente registrado exitosamente")
        private String message;

        @Schema(description = "Datos de la respuesta")
        private T data;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Timestamp de la respuesta")
        private LocalDateTime timestamp;

        public static <T> ApiResponse<T> success(String message, T data) {
            return ApiResponse.<T>builder()
                    .status(200)
                    .message(message)
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        public static <T> ApiResponse<T> created(String message, T data) {
            return ApiResponse.<T>builder()
                    .status(201)
                    .message(message)
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}
