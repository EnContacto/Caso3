package com.hospital.pacientes.dto;

import com.hospital.pacientes.model.Paciente;

import java.time.LocalDate;
import java.time.Period;

/**
 * Clase utilitaria para mapear entre la entidad {@link Paciente} y los DTOs.
 *
 * <p>Centraliza la lógica de conversión para evitar duplicación
 * y mantener consistencia en todos los endpoints.</p>
 */
public class PacienteMapper {

    private PacienteMapper() {
        // Clase utilitaria - no instanciar
    }

    // -------------------------------------------------------
    // Request → Entidad
    // -------------------------------------------------------

    /**
     * Convierte un {@link PacienteDTO.Request} en una entidad {@link Paciente} nueva.
     *
     * @param request DTO de entrada
     * @return entidad Paciente lista para persistir
     */
    public static Paciente toEntity(PacienteDTO.Request request) {
        return Paciente.builder()
                .cedula(request.getCedula().trim())
                .nombres(capitalize(request.getNombres()))
                .apellidos(capitalize(request.getApellidos()))
                .fechaNacimiento(request.getFechaNacimiento())
                .genero(request.getGenero())
                .telefono(request.getTelefono())
                .email(request.getEmail() != null ? request.getEmail().toLowerCase().trim() : null)
                .direccion(request.getDireccion())
                .tipoSangre(request.getTipoSangre())
                .alergias(request.getAlergias())
                .activo(true)
                .build();
    }

    /**
     * Actualiza los campos de una entidad existente con los datos del request.
     * No modifica la cédula ni el ID.
     *
     * @param entidad  entidad existente a actualizar
     * @param request  nuevos datos del DTO
     */
    public static void updateEntityFromRequest(Paciente entidad, PacienteDTO.Request request) {
        entidad.setNombres(capitalize(request.getNombres()));
        entidad.setApellidos(capitalize(request.getApellidos()));
        entidad.setFechaNacimiento(request.getFechaNacimiento());
        entidad.setGenero(request.getGenero());
        entidad.setTelefono(request.getTelefono());
        entidad.setEmail(request.getEmail() != null ? request.getEmail().toLowerCase().trim() : null);
        entidad.setDireccion(request.getDireccion());
        entidad.setTipoSangre(request.getTipoSangre());
        entidad.setAlergias(request.getAlergias());
    }

    // -------------------------------------------------------
    // Entidad → Response DTO
    // -------------------------------------------------------

    /**
     * Convierte una entidad {@link Paciente} en el DTO de respuesta completa.
     *
     * @param paciente entidad a convertir
     * @return DTO de respuesta con todos los campos
     */
    public static PacienteDTO.Response toResponse(Paciente paciente) {
        return PacienteDTO.Response.builder()
                .id(paciente.getId())
                .cedula(paciente.getCedula())
                .nombres(paciente.getNombres())
                .apellidos(paciente.getApellidos())
                .nombreCompleto(paciente.getNombres() + " " + paciente.getApellidos())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .edad(calcularEdad(paciente.getFechaNacimiento()))
                .genero(paciente.getGenero())
                .telefono(paciente.getTelefono())
                .email(paciente.getEmail())
                .direccion(paciente.getDireccion())
                .tipoSangre(paciente.getTipoSangre())
                .alergias(paciente.getAlergias())
                .activo(paciente.getActivo())
                .fechaRegistro(paciente.getFechaRegistro())
                .fechaActualizacion(paciente.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte una entidad {@link Paciente} en el DTO de resumen (para otros microservicios).
     *
     * @param paciente entidad a convertir
     * @return DTO de resumen con datos mínimos necesarios
     */
    public static PacienteDTO.Summary toSummary(Paciente paciente) {
        return PacienteDTO.Summary.builder()
                .id(paciente.getId())
                .cedula(paciente.getCedula())
                .nombreCompleto(paciente.getNombres() + " " + paciente.getApellidos())
                .telefono(paciente.getTelefono())
                .email(paciente.getEmail())
                .tipoSangre(paciente.getTipoSangre())
                .activo(paciente.getActivo())
                .build();
    }

    // -------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------

    private static Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return null;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private static String capitalize(String texto) {
        if (texto == null || texto.isBlank()) return texto;
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                         .append(palabra.substring(1))
                         .append(" ");
            }
        }
        return resultado.toString().trim();
    }
}
