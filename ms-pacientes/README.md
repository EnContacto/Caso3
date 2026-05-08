# ms-pacientes 🏥

Microservicio de **Gestión de Pacientes** del Sistema Hospitalario.
Parte del proyecto de migración de arquitectura monolítica a microservicios.

---

## 📋 Descripción

Este microservicio es responsable de:
- Registro y gestión completa de pacientes
- Consulta de pacientes por ID o cédula
- Búsqueda paginada con filtros
- Validación de existencia de pacientes para otros microservicios
- Soft delete con historial preservado

---

## 🛠️ Tecnologías

| Tecnología       | Versión  | Propósito                        |
|------------------|----------|----------------------------------|
| Java             | 21       | Lenguaje principal (LTS)         |
| Spring Boot      | 3.2.5    | Framework backend                |
| Spring Data JPA  | —        | Persistencia ORM                 |
| Spring Validation| —        | Validación de entradas           |
| Spring Actuator  | —        | Health checks y métricas         |
| PostgreSQL       | 15+      | Base de datos                    |
| Flyway           | —        | Migraciones de esquema           |
| Lombok           | —        | Reducción de boilerplate         |
| SpringDoc OpenAPI| 2.5.0    | Documentación Swagger            |

---

## 🚀 Ejecución

### Con Docker Compose (recomendado)
```bash
docker-compose up ms-pacientes
```

### Local con Maven
```bash
# Requiere PostgreSQL corriendo en localhost:5432
mvn spring-boot:run
```

### Variables de entorno
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hospital_pacientes
DB_USER=postgres
DB_PASSWORD=postgres
```

---

## 📡 Endpoints REST

| Método | Endpoint                              | Descripción                         |
|--------|---------------------------------------|-------------------------------------|
| POST   | `/api/pacientes`                      | Registrar nuevo paciente            |
| GET    | `/api/pacientes`                      | Listar pacientes (paginado)         |
| GET    | `/api/pacientes/buscar?q=término`     | Buscar por nombre/apellido/cédula   |
| GET    | `/api/pacientes/{id}`                 | Obtener por ID                      |
| GET    | `/api/pacientes/cedula/{cedula}`      | Obtener por cédula                  |
| GET    | `/api/pacientes/cedula/{cedula}/resumen` | Resumen para otros microservicios |
| GET    | `/api/pacientes/cedula/{cedula}/existe` | Verificar existencia              |
| PUT    | `/api/pacientes/{id}`                 | Actualizar paciente                 |
| DELETE | `/api/pacientes/{id}`                 | Desactivar (soft delete)            |
| GET    | `/api/pacientes/estadisticas`         | Estadísticas del servicio           |

---

## 📚 Documentación

- **Swagger UI**: http://localhost:8081/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/api/v3/api-docs
- **Health Check**: http://localhost:8081/api/actuator/health

---

## 🔗 Comunicación con otros microservicios

Este microservicio es **proveedor** de datos para:

| Microservicio    | Endpoint consumido                           | Propósito                  |
|------------------|----------------------------------------------|----------------------------|
| ms-agenda        | `GET /cedula/{cedula}/resumen`               | Validar paciente al agendar|
| ms-facturacion   | `GET /cedula/{cedula}/existe`                | Validar antes de facturar  |
| ms-laboratorio   | `GET /cedula/{cedula}/resumen`               | Asociar resultado           |

---

## 🗄️ Base de datos

- **Motor**: PostgreSQL 15
- **Esquema**: `public`
- **Tabla principal**: `pacientes`
- **Migraciones**: Flyway (V1, V2)

```
Puerto del contenedor DB: 5433 (externo) → 5432 (interno)
Nombre DB: hospital_pacientes
```

---

## 🧪 Tests

```bash
# Ejecutar todos los tests
mvn test

# Tests con reporte
mvn test surefire-report:report
```

---

## 📦 Estructura del proyecto

```
ms-pacientes/
├── src/
│   ├── main/
│   │   ├── java/com/hospital/pacientes/
│   │   │   ├── MsPacientesApplication.java
│   │   │   ├── config/          # OpenApiConfig
│   │   │   ├── controller/      # PacienteController
│   │   │   ├── dto/             # DTOs + Mapper
│   │   │   ├── exception/       # Excepciones + Handler
│   │   │   ├── model/           # Entidad Paciente
│   │   │   ├── repository/      # PacienteRepository
│   │   │   ├── service/         # Interface PacienteService
│   │   │   └── serviceImpl/     # PacienteServiceImpl
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-test.yml
│   │       └── db/migration/    # Scripts Flyway
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```
