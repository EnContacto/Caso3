package com.hospital.pacientes.repository;

import com.hospital.pacientes.model.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Paciente}.
 *
 * <p>Extiende {@link JpaRepository} que provee operaciones CRUD base.
 * Se agregan queries personalizadas para búsquedas específicas del dominio hospitalario.</p>
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Busca un paciente activo por su número de cédula.
     *
     * @param cedula número de cédula del paciente
     * @return Optional con el paciente si existe y está activo
     */
    Optional<Paciente> findByCedulaAndActivoTrue(String cedula);

    /**
     * Verifica si existe un paciente con la cédula dada (activo o no).
     *
     * @param cedula número de cédula
     * @return true si existe algún registro con esa cédula
     */
    boolean existsByCedula(String cedula);

    /**
     * Verifica si existe un paciente con el email dado, excluyendo un ID específico.
     * Útil para validaciones en actualizaciones.
     *
     * @param email email a verificar
     * @param id    ID a excluir de la búsqueda
     * @return true si existe otro paciente con ese email
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Busca un paciente activo por su ID.
     *
     * @param id identificador del paciente
     * @return Optional con el paciente si existe y está activo
     */
    Optional<Paciente> findByIdAndActivoTrue(Long id);

    /**
     * Lista todos los pacientes activos con soporte de paginación.
     *
     * @param pageable configuración de paginación y ordenamiento
     * @return página de pacientes activos
     */
    Page<Paciente> findAllByActivoTrue(Pageable pageable);

    /**
     * Búsqueda dinámica por nombre, apellido o cédula (case-insensitive).
     *
     * @param termino texto a buscar
     * @param pageable paginación
     * @return página de resultados coincidentes
     */
    @Query("""
            SELECT p FROM Paciente p
            WHERE p.activo = true AND (
                LOWER(p.nombres)   LIKE LOWER(CONCAT('%', :termino, '%')) OR
                LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) OR
                p.cedula           LIKE CONCAT('%', :termino, '%')
            )
            ORDER BY p.apellidos ASC
            """)
    Page<Paciente> buscarPorTermino(@Param("termino") String termino, Pageable pageable);

    /**
     * Cuenta el total de pacientes activos en el sistema.
     *
     * @return cantidad de pacientes activos
     */
    long countByActivoTrue();
}
