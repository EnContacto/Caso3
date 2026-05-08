package com.hospital.pacientes.serviceImpl;

import com.hospital.pacientes.dto.PacienteDTO;
import com.hospital.pacientes.dto.PacienteMapper;
import com.hospital.pacientes.exception.PacienteException;
import com.hospital.pacientes.exception.PacienteNotFoundException;
import com.hospital.pacientes.model.Paciente;
import com.hospital.pacientes.repository.PacienteRepository;
import com.hospital.pacientes.service.PacienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la lógica de negocio para la gestión de pacientes.
 *
 * <p>Aplica el principio de separación de responsabilidades:
 * esta clase contiene únicamente lógica de negocio, no lógica HTTP.</p>
 *
 * <p>Usa {@code @Transactional} para garantizar integridad de datos
 * y {@code @Slf4j} para registro de eventos relevantes (Fase 4 - logs de interacción).</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    // -------------------------------------------------------
    // Crear
    // -------------------------------------------------------

    @Override
    @Transactional
    public PacienteDTO.Response registrarPaciente(PacienteDTO.Request request) {
        log.info("[PACIENTES] Intentando registrar paciente con cédula: {}", request.getCedula());

        // Validar que la cédula no esté ya registrada
        if (pacienteRepository.existsByCedula(request.getCedula())) {
            log.warn("[PACIENTES] Cédula duplicada detectada: {}", request.getCedula());
            throw new PacienteException(
                "Ya existe un paciente registrado con la cédula: " + request.getCedula()
            );
        }

        // Validar email único si fue proporcionado
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (pacienteRepository.existsByEmailAndIdNot(request.getEmail().toLowerCase(), -1L)) {
                throw new PacienteException("El email ya está registrado por otro paciente.");
            }
        }

        Paciente paciente = PacienteMapper.toEntity(request);
        Paciente guardado = pacienteRepository.save(paciente);

        log.info("[PACIENTES] Paciente registrado exitosamente. ID: {}, Cédula: {}",
                guardado.getId(), guardado.getCedula());

        return PacienteMapper.toResponse(guardado);
    }

    // -------------------------------------------------------
    // Leer
    // -------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public PacienteDTO.Response obtenerPorId(Long id) {
        log.debug("[PACIENTES] Buscando paciente por ID: {}", id);

        Paciente paciente = pacienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new PacienteNotFoundException(
                    "Paciente no encontrado con ID: " + id
                ));

        return PacienteMapper.toResponse(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteDTO.Response obtenerPorCedula(String cedula) {
        log.debug("[PACIENTES] Buscando paciente por cédula: {}", cedula);

        Paciente paciente = pacienteRepository.findByCedulaAndActivoTrue(cedula)
                .orElseThrow(() -> new PacienteNotFoundException(
                    "Paciente no encontrado con cédula: " + cedula
                ));

        return PacienteMapper.toResponse(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteDTO.Summary obtenerResumenPorCedula(String cedula) {
        log.debug("[PACIENTES] Solicitud de resumen para cédula: {} (llamada inter-servicio)", cedula);

        Paciente paciente = pacienteRepository.findByCedulaAndActivoTrue(cedula)
                .orElseThrow(() -> new PacienteNotFoundException(
                    "Paciente no encontrado con cédula: " + cedula
                ));

        return PacienteMapper.toSummary(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PacienteDTO.Response> listarPacientes(Pageable pageable) {
        log.debug("[PACIENTES] Listando pacientes activos. Página: {}, Tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return pacienteRepository.findAllByActivoTrue(pageable)
                .map(PacienteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PacienteDTO.Response> buscarPacientes(String termino, Pageable pageable) {
        log.debug("[PACIENTES] Buscando pacientes con término: '{}'", termino);

        return pacienteRepository.buscarPorTermino(termino.trim(), pageable)
                .map(PacienteMapper::toResponse);
    }

    // -------------------------------------------------------
    // Actualizar
    // -------------------------------------------------------

    @Override
    @Transactional
    public PacienteDTO.Response actualizarPaciente(Long id, PacienteDTO.Request request) {
        log.info("[PACIENTES] Actualizando paciente con ID: {}", id);

        Paciente paciente = pacienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new PacienteNotFoundException(
                    "Paciente no encontrado con ID: " + id
                ));

        // Validar email único (excluyendo el propio paciente)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (pacienteRepository.existsByEmailAndIdNot(request.getEmail().toLowerCase(), id)) {
                throw new PacienteException("El email ya está registrado por otro paciente.");
            }
        }

        PacienteMapper.updateEntityFromRequest(paciente, request);
        Paciente actualizado = pacienteRepository.save(paciente);

        log.info("[PACIENTES] Paciente ID: {} actualizado correctamente.", id);
        return PacienteMapper.toResponse(actualizado);
    }

    // -------------------------------------------------------
    // Eliminar (soft delete)
    // -------------------------------------------------------

    @Override
    @Transactional
    public void desactivarPaciente(Long id) {
        log.info("[PACIENTES] Desactivando paciente con ID: {}", id);

        Paciente paciente = pacienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new PacienteNotFoundException(
                    "Paciente no encontrado con ID: " + id
                ));

        paciente.setActivo(false);
        pacienteRepository.save(paciente);

        log.info("[PACIENTES] Paciente ID: {} desactivado (soft delete).", id);
    }

    // -------------------------------------------------------
    // Validaciones para otros microservicios
    // -------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean existePacienteActivo(String cedula) {
        boolean existe = pacienteRepository.findByCedulaAndActivoTrue(cedula).isPresent();
        log.debug("[PACIENTES] Verificación de existencia para cédula {}: {}", cedula, existe);
        return existe;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPacientesActivos() {
        return pacienteRepository.countByActivoTrue();
    }
}
