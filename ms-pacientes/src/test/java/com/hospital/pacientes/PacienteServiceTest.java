package com.hospital.pacientes;

import com.hospital.pacientes.dto.PacienteDTO;
import com.hospital.pacientes.exception.PacienteException;
import com.hospital.pacientes.exception.PacienteNotFoundException;
import com.hospital.pacientes.model.Paciente;
import com.hospital.pacientes.repository.PacienteRepository;
import com.hospital.pacientes.serviceImpl.PacienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del servicio de Pacientes.
 *
 * <p>Usa Mockito para aislar la lógica de negocio
 * sin necesidad de levantar el contexto Spring ni la base de datos.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - PacienteService")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    private PacienteDTO.Request requestValido;
    private Paciente pacienteGuardado;

    @BeforeEach
    void setUp() {
        requestValido = PacienteDTO.Request.builder()
                .cedula("0801234567")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(1985, 3, 15))
                .genero(Paciente.Genero.MASCULINO)
                .telefono("0991234567")
                .email("juan.perez@email.com")
                .tipoSangre("O+")
                .build();

        pacienteGuardado = Paciente.builder()
                .id(1L)
                .cedula("0801234567")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .fechaNacimiento(LocalDate.of(1985, 3, 15))
                .genero(Paciente.Genero.MASCULINO)
                .telefono("0991234567")
                .email("juan.perez@email.com")
                .tipoSangre("O+")
                .activo(true)
                .build();
    }

    // -------------------------------------------------------
    // Tests de Registro
    // -------------------------------------------------------

    @Test
    @DisplayName("Debe registrar paciente correctamente cuando los datos son válidos")
    void debeRegistrarPacienteExitosamente() {
        when(pacienteRepository.existsByCedula(anyString())).thenReturn(false);
        when(pacienteRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteGuardado);

        PacienteDTO.Response resultado = pacienteService.registrarPaciente(requestValido);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCedula()).isEqualTo("0801234567");
        assertThat(resultado.getNombres()).isEqualTo("Juan Carlos");
        assertThat(resultado.getApellidos()).isEqualTo("Pérez Gómez");
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Debe lanzar PacienteException cuando la cédula ya existe")
    void debeLanzarExcepcionCuandoCedulaDuplicada() {
        when(pacienteRepository.existsByCedula("0801234567")).thenReturn(true);

        assertThatThrownBy(() -> pacienteService.registrarPaciente(requestValido))
                .isInstanceOf(PacienteException.class)
                .hasMessageContaining("0801234567");

        verify(pacienteRepository, never()).save(any());
    }

    // -------------------------------------------------------
    // Tests de Búsqueda
    // -------------------------------------------------------

    @Test
    @DisplayName("Debe retornar paciente cuando se busca por ID existente")
    void debeRetornarPacientePorIdExistente() {
        when(pacienteRepository.findByIdAndActivoTrue(1L))
                .thenReturn(Optional.of(pacienteGuardado));

        PacienteDTO.Response resultado = pacienteService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getCedula()).isEqualTo("0801234567");
    }

    @Test
    @DisplayName("Debe lanzar PacienteNotFoundException cuando el ID no existe")
    void debeLanzarExcepcionCuandoIdNoExiste() {
        when(pacienteRepository.findByIdAndActivoTrue(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.obtenerPorId(99L))
                .isInstanceOf(PacienteNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Debe retornar paciente cuando se busca por cédula existente")
    void debeRetornarPacientePorCedulaExistente() {
        when(pacienteRepository.findByCedulaAndActivoTrue("0801234567"))
                .thenReturn(Optional.of(pacienteGuardado));

        PacienteDTO.Response resultado = pacienteService.obtenerPorCedula("0801234567");

        assertThat(resultado.getCedula()).isEqualTo("0801234567");
    }

    // -------------------------------------------------------
    // Tests de Validación (inter-servicio)
    // -------------------------------------------------------

    @Test
    @DisplayName("Debe retornar true cuando el paciente activo existe")
    void debeRetornarTrueCuandoPacienteExiste() {
        when(pacienteRepository.findByCedulaAndActivoTrue("0801234567"))
                .thenReturn(Optional.of(pacienteGuardado));

        boolean existe = pacienteService.existePacienteActivo("0801234567");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Debe retornar false cuando el paciente no existe")
    void debeRetornarFalseCuandoPacienteNoExiste() {
        when(pacienteRepository.findByCedulaAndActivoTrue("9999999999"))
                .thenReturn(Optional.empty());

        boolean existe = pacienteService.existePacienteActivo("9999999999");

        assertThat(existe).isFalse();
    }

    // -------------------------------------------------------
    // Tests de Desactivación
    // -------------------------------------------------------

    @Test
    @DisplayName("Debe desactivar paciente correctamente (soft delete)")
    void debeDesactivarPacienteCorrectamente() {
        when(pacienteRepository.findByIdAndActivoTrue(1L))
                .thenReturn(Optional.of(pacienteGuardado));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteGuardado);

        pacienteService.desactivarPaciente(1L);

        verify(pacienteRepository, times(1)).save(argThat(p -> !p.getActivo()));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar desactivar paciente inexistente")
    void debeLanzarExcepcionAlDesactivarPacienteInexistente() {
        when(pacienteRepository.findByIdAndActivoTrue(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.desactivarPaciente(99L))
                .isInstanceOf(PacienteNotFoundException.class);
    }
}
