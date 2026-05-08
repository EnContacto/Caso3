-- ============================================================
-- Migración V2: Datos iniciales de prueba
-- ============================================================

INSERT INTO pacientes (cedula, nombres, apellidos, fecha_nacimiento, genero, telefono, email, direccion, tipo_sangre, alergias)
VALUES
    ('0801234567', 'Juan Carlos', 'Pérez Gómez',    '1985-03-15', 'MASCULINO',  '0991234567', 'juan.perez@email.com',   'Av. Principal 123, Quito',    'O+',  'Penicilina'),
    ('0809876543', 'María Elena', 'Torres Rodas',   '1992-07-22', 'FEMENINO',   '0997654321', 'maria.torres@email.com', 'Calle Secundaria 456, Guayaquil', 'A+', null),
    ('1712345678', 'Pedro Luis',  'Vásquez Mora',   '1978-11-30', 'MASCULINO',  '0993456789', null,                     'Barrio Norte 789, Cuenca',    'B-',  'Aspirina, Ibuprofeno'),
    ('1798765432', 'Ana Sofía',   'Mendoza Reyes',  '2000-05-10', 'FEMENINO',   '0996543210', 'ana.mendoza@email.com',  'Urb. Sur 321, Loja',          'AB+', null);
