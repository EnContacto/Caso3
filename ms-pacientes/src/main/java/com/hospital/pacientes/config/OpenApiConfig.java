package com.hospital.pacientes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI 3 / Swagger UI para el microservicio de Pacientes.
 *
 * <p>Documenta automáticamente todos los endpoints REST expuestos.
 * Accesible en: http://localhost:8081/api/swagger-ui.html</p>
 *
 * <p>Cumple con el punto 13 del enunciado: Documentar APIs con OpenAPI/Swagger.</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS-Pacientes API")
                        .description("""
                                ## Microservicio de Gestión de Pacientes
                                
                                Parte del sistema hospitalario basado en microservicios.
                                
                                ### Funcionalidades:
                                - Registro y gestión de pacientes
                                - Búsqueda por nombre, apellido o cédula
                                - Validación de existencia para otros microservicios
                                - Soft delete con historial preservado
                                
                                ### Comunicación entre servicios:
                                Este microservicio expone el endpoint `/pacientes/cedula/{cedula}/resumen`
                                que es consumido por ms-agenda y ms-facturación para validar pacientes.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sistema Hospitalario - Carrera de Sistemas de Información")
                                .email("sistemas@hospital.edu.ec"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081/api")
                                .description("Servidor de Desarrollo Local"),
                        new Server()
                                .url("http://ms-pacientes:8081/api")
                                .description("Servidor Docker Interno")
                ));
    }
}
