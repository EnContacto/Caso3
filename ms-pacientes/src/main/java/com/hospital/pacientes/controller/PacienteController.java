package com.hospital.pacientes.controller;

import com.hospital.pacientes.dto.PacienteDTO;
import com.hospital.pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST del microservicio de Pacientes.
 *
 * <p>Expone los endpoints HTTP para la gestión de pacientes y para
 * comunicación con otros microservicios del sistema hospitalario.</p>
 *
 * <p>Base URL: {@code /api/pacientes}</p>
 */
@Slf4j
@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gestión completa de pacientes del sistema hospitalario")
public class PacienteController {

    private final PacienteService pacienteService;

    // -------------------------------------------------------
    // POST /pacientes - Registrar paciente
    // -------------------------------------------------------

    @PostMapping
    @Operation(summary = "Registrar nuevo paciente",
               description = "Crea un nuevo registro de paciente en el sistema. La cédula debe ser única.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Paciente registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Cédula o email ya registrado")
    })
    public ResponseEntity<PacienteDTO.ApiResponse<PacienteDTO.Response>> registrarPaciente(
            @Valid @RequestBody PacienteDTO.Request request) {

        log.info("[API] POST /pacientes - Registrando paciente cédula: {}", request.getCedula());

        PacienteDTO.Response paciente = pacienteService.registrarPaciente(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PacienteDTO.ApiResponse.created("Paciente registrado exitosamente", paciente));
    }

    // -------------------------------------------------------
    // GET /pacientes - Listar todos
    // -------------------------------------------------------

    @GetMapping
    @Operation(summary = "Listar pacientes activos",
               description = "Retorna una lista paginada de todos los pacientes activos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    public ResponseEntity<Page<PacienteDTO.Response>> listarPacientes(
            @Parameter(description = "Número de página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de la página", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo de ordenamiento", example = "apellidos")
            @RequestParam(defaultValue = "apellidos") String sortBy) {

        log.debug("[API] GET /pacientes - página: {}, tamaño: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(pacienteService.listarPacientes(pageable));
    }

    // -------------------------------------------------------
    // GET /pacientes/buscar - Búsqueda por término
    // -------------------------------------------------------

    @GetMapping("/buscar")
    @Operation(summary = "Buscar pacientes",
               description = "Busca pacientes por nombre, apellido o cédula (búsqueda parcial, case-insensitive).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<Page<PacienteDTO.Response>> buscarPacientes(
            @Parameter(description = "Término de búsqueda", example = "Pérez", required = true)
            @RequestParam String q,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("[API] GET /pacientes/buscar - término: '{}'", q);

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(pacienteService.buscarPacientes(q, pageable));
    }

    // -------------------------------------------------------
    // GET /pacientes/{id} - Obtener por ID
    // -------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID",
               description = "Retorna los datos completos de un paciente según su ID interno.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    public ResponseEntity<PacienteDTO.ApiResponse<PacienteDTO.Response>> obtenerPorId(
            @Parameter(description = "ID del paciente", example = "1", required = true)
            @PathVariable Long id) {

        log.debug("[API] GET /pacientes/{}", id);

        PacienteDTO.Response paciente = pacienteService.obtenerPorId(id);
        return ResponseEntity.ok(PacienteDTO.ApiResponse.success("Paciente encontrado", paciente));
    }

    // -------------------------------------------------------
    // GET /pacientes/cedula/{cedula} - Obtener por cédula
    // -------------------------------------------------------

    @GetMapping("/cedula/{cedula}")
    @Operation(summary = "Obtener paciente por cédula",
               description = "Retorna los datos completos de un paciente según su número de cédula.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado con esa cédula")
    })
    public ResponseEntity<PacienteDTO.ApiResponse<PacienteDTO.Response>> obtenerPorCedula(
            @Parameter(description = "Número de cédula", example = "0801234567", required = true)
            @PathVariable String cedula) {

        log.info("[API] GET /pacientes/cedula/{}", cedula);

        PacienteDTO.Response paciente = pacienteService.obtenerPorCedula(cedula);
        return ResponseEntity.ok(PacienteDTO.ApiResponse.success("Paciente encontrado", paciente));
    }

    // -------------------------------------------------------
    // GET /pacientes/cedula/{cedula}/resumen - Para otros microservicios
    // -------------------------------------------------------

    @GetMapping("/cedula/{cedula}/resumen")
    @Operation(summary = "Resumen de paciente por cédula (inter-servicio)",
               description = """
                       Endpoint optimizado para comunicación entre microservicios.
                       Retorna únicamente los datos necesarios para validar al paciente.
                       Usado por: ms-agenda, ms-facturacion, ms-laboratorio.
                       """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    public ResponseEntity<PacienteDTO.Summary> obtenerResumenPorCedula(
            @Parameter(description = "Número de cédula", example = "0801234567", required = true)
            @PathVariable String cedula) {

        log.info("[API] GET /pacientes/cedula/{}/resumen (llamada inter-servicio)", cedula);

        return ResponseEntity.ok(pacienteService.obtenerResumenPorCedula(cedula));
    }

    // -------------------------------------------------------
    // GET /pacientes/cedula/{cedula}/existe - Validación rápida
    // -------------------------------------------------------

    @GetMapping("/cedula/{cedula}/existe")
    @Operation(summary = "Verificar existencia de paciente activo",
               description = "Retorna true/false para verificar si un paciente activo existe con esa cédula.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Verificación completada")
    })
    public ResponseEntity<Map<String, Object>> existePaciente(
            @Parameter(description = "Número de cédula", example = "0801234567", required = true)
            @PathVariable String cedula) {

        log.debug("[API] GET /pacientes/cedula/{}/existe", cedula);

        boolean existe = pacienteService.existePacienteActivo(cedula);

        return ResponseEntity.ok(Map.of(
                "cedula", cedula,
                "existe", existe,
                "mensaje", existe
                        ? "Paciente activo encontrado"
                        : "No existe paciente activo con esa cédula"
        ));
    }

    // -------------------------------------------------------
    // PUT /pacientes/{id} - Actualizar
    // -------------------------------------------------------

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del paciente",
               description = "Actualiza todos los campos del paciente. La cédula no puede modificarse.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
        @ApiResponse(responseCode = "409", description = "Email duplicado")
    })
    public ResponseEntity<PacienteDTO.ApiResponse<PacienteDTO.Response>> actualizarPaciente(
            @Parameter(description = "ID del paciente", example = "1", required = true)
            @PathVariable Long id,

            @Valid @RequestBody PacienteDTO.Request request) {

        log.info("[API] PUT /pacientes/{}", id);

        PacienteDTO.Response actualizado = pacienteService.actualizarPaciente(id, request);
        return ResponseEntity.ok(
                PacienteDTO.ApiResponse.success("Paciente actualizado exitosamente", actualizado));
    }

    // -------------------------------------------------------
    // DELETE /pacientes/{id} - Desactivar (soft delete)
    // -------------------------------------------------------

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar paciente",
               description = "Desactiva al paciente (soft delete). El registro se mantiene en la base de datos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paciente desactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    public ResponseEntity<PacienteDTO.ApiResponse<Void>> desactivarPaciente(
            @Parameter(description = "ID del paciente", example = "1", required = true)
            @PathVariable Long id) {

        log.info("[API] DELETE /pacientes/{} (soft delete)", id);

        pacienteService.desactivarPaciente(id);
        return ResponseEntity.ok(
                PacienteDTO.ApiResponse.success("Paciente desactivado exitosamente", null));
    }

    // -------------------------------------------------------
    // GET /pacientes/estadisticas - Info del microservicio
    // -------------------------------------------------------

    @GetMapping("/estadisticas")
    @Operation(summary = "Estadísticas del microservicio",
               description = "Retorna estadísticas básicas del servicio de pacientes.")
    public ResponseEntity<Map<String, Object>> estadisticas() {
        return ResponseEntity.ok(Map.of(
                "microservicio", "ms-pacientes",
                "version", "1.0.0",
                "puerto", 8081,
                "totalPacientesActivos", pacienteService.contarPacientesActivos(),
                "estado", "operativo"
        ));
    }
}
