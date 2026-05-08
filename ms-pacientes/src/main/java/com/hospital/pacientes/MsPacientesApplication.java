package com.hospital.pacientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de Pacientes.
 *
 * <p>Este microservicio es responsable de:</p>
 * <ul>
 *   <li>Registro y gestión de pacientes del hospital</li>
 *   <li>Exposición de APIs REST para consulta de pacientes por otros microservicios</li>
 *   <li>Validación de existencia de pacientes (usado por Facturación y Agenda)</li>
 * </ul>
 *
 * <p>Puerto: 8081 | Contexto: /api</p>
 * <p>Swagger UI: http://localhost:8081/api/swagger-ui.html</p>
 */
@SpringBootApplication
public class MsPacientesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPacientesApplication.class, args);
    }
}
