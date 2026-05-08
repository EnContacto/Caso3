-- ============================================================
-- Migración V1: Creación de tablas del microservicio Pacientes
-- Autor: Sistema Hospitalario
-- Fecha: 2024-01-01
-- ============================================================

CREATE TABLE IF NOT EXISTS pacientes (
    id              BIGSERIAL       PRIMARY KEY,
    cedula          VARCHAR(13)     NOT NULL UNIQUE,
    nombres         VARCHAR(100)    NOT NULL,
    apellidos       VARCHAR(100)    NOT NULL,
    fecha_nacimiento DATE           NOT NULL,
    genero          VARCHAR(10)     NOT NULL,
    telefono        VARCHAR(15),
    email           VARCHAR(150)    UNIQUE,
    direccion       VARCHAR(255),
    tipo_sangre     VARCHAR(5),
    alergias        TEXT,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    fecha_registro  TIMESTAMP       NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Índices para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_pacientes_cedula    ON pacientes(cedula);
CREATE INDEX IF NOT EXISTS idx_pacientes_apellidos ON pacientes(apellidos);
CREATE INDEX IF NOT EXISTS idx_pacientes_activo    ON pacientes(activo);

-- Comentarios de tabla
COMMENT ON TABLE  pacientes                    IS 'Registro de pacientes del hospital';
COMMENT ON COLUMN pacientes.cedula             IS 'Número de cédula o documento de identidad';
COMMENT ON COLUMN pacientes.tipo_sangre        IS 'Tipo de sangre: A+, A-, B+, B-, AB+, AB-, O+, O-';
COMMENT ON COLUMN pacientes.alergias           IS 'Listado de alergias conocidas del paciente';
COMMENT ON COLUMN pacientes.activo             IS 'Soft delete: false = paciente inactivo';
