package com.hospital.pacientes.service;

import com.hospital.pacientes.dto.PacienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz de servicio para la gestión de pacientes.
 *
 * <p>Define el contrato de operaciones de negocio del microservicio,
 * desacoplando la implementación del controlador REST.</p>
 */
public interface PacienteService {

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * @param request datos del paciente a registrar
     * @return DTO con el paciente creado
     * @throws com.hospital.pacientes.exception.PacienteException si la cédula ya está registrada
     */
    PacienteDTO.Response registrarPaciente(PacienteDTO.Request request);

    /**
     * Obtiene la información completa de un paciente por su ID.
     *
     * @param id identificador del paciente
     * @return DTO con datos completos del paciente
     * @throws com.hospital.pacientes.exception.PacienteNotFoundException si no existe
     */
    PacienteDTO.Response obtenerPorId(Long id);

    /**
     * Obtiene la información de un paciente por su número de cédula.
     * Usado frecuentemente por otros microservicios (Agenda, Facturación).
     *
     * @param cedula número de cédula del paciente
     * @return DTO con datos completos del paciente
     * @throws com.hospital.pacientes.exception.PacienteNotFoundException si no existe
     */
    PacienteDTO.Response obtenerPorCedula(String cedula);

    /**
     * Obtiene un resumen básico del paciente por cédula.
     * Endpoint optimizado para llamadas entre microservicios.
     *
     * @param cedula número de cédula
     * @return DTO de resumen con datos mínimos
     * @throws com.hospital.pacientes.exception.PacienteNotFoundException si no existe
     */
    PacienteDTO.Summary obtenerResumenPorCedula(String cedula);

    /**
     * Lista todos los pacientes activos con paginación.
     *
     * @param pageable configuración de paginación
     * @return página de pacientes en formato respuesta completa
     */
    Page<PacienteDTO.Response> listarPacientes(Pageable pageable);

    /**
     * Busca pacientes por nombre, apellido o cédula.
     *
     * @param termino  texto de búsqueda
     * @param pageable paginación
     * @return página de resultados que coinciden con el término
     */
    Page<PacienteDTO.Response> buscarPacientes(String termino, Pageable pageable);

    /**
     * Actualiza los datos de un paciente existente.
     *
     * @param id      identificador del paciente a actualizar
     * @param request nuevos datos del paciente
     * @return DTO con los datos actualizados
     * @throws com.hospital.pacientes.exception.PacienteNotFoundException si no existe
     */
    PacienteDTO.Response actualizarPaciente(Long id, PacienteDTO.Request request);

    /**
     * Desactiva un paciente (soft delete).
     * No elimina el registro de la base de datos para mantener histórico.
     *
     * @param id identificador del paciente a desactivar
     * @throws com.hospital.pacientes.exception.PacienteNotFoundException si no existe
     */
    void desactivarPaciente(Long id);

    /**
     * Verifica si existe un paciente activo con la cédula dada.
     * Endpoint de validación para otros microservicios.
     *
     * @param cedula número de cédula a verificar
     * @return true si el paciente existe y está activo
     */
    boolean existePacienteActivo(String cedula);

    /**
     * Obtiene el número total de pacientes activos.
     *
     * @return cantidad de pacientes activos
     */
    long contarPacientesActivos();
}
