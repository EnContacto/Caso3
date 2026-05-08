package com.hospital.pacientes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa a un paciente del sistema hospitalario.
 *
 * <p>Cada paciente tiene un registro único identificado por su cédula.
 * Implementa soft-delete mediante el campo {@code activo}.</p>
 */
@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"alergias"})
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 13)
    private String cedula;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Genero genero;

    @Column(length = 15)
    private String telefono;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 255)
    private String direccion;

    @Column(name = "tipo_sangre", length = 5)
    private String tipoSangre;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    // -------------------------------------------------------
    // Enum interno para género
    // -------------------------------------------------------

    public enum Genero {
        MASCULINO, FEMENINO, OTRO
    }
}
