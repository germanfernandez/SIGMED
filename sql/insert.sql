USE sigmed_db;
 
INSERT INTO usuarios (username, password, rol) VALUES
    ('secretaria01',  '$2a$10$vccaq6x4YC.9Ah6wgwAisuWUNG6Pvd0KapDyUjsrXazjRiqld3jCe', 'Secretaria'),
    ('drdimartino',   '$2a$10$5VXnVcFxSDHK818RXr4YR.rBfIDlJmg8yZ/LtaE7lmeyypZ/N6JcO', 'Médico Pediatra'),
    ('drafernandez',  '$2a$10$3CQqYwbjG2wEte5i6iUSXu.eZML57gKGnjQem10m8wiZsdAMUfMCO', 'Médica Dermatóloga');
 
INSERT INTO medicos (id_usuario, nombre, apellido, dni, telefono, especialidad, matricula) VALUES
    (2, 'Federico', 'Di Martino', '22111333', '1144556677', 'Pediatría',    'MP-12345'),
    (3, 'Julia',    'Fernández',  '28444555', '1155667788', 'Dermatología', 'MD-67890');
 
INSERT INTO disponibilidad_medico (id_medico, dia_semana, hora_inicio, hora_fin) VALUES
    (1, 'Lunes',     '08:00:00', '12:00:00'),
    (1, 'Miércoles', '08:00:00', '12:00:00'),
    (1, 'Viernes',   '08:00:00', '12:00:00'),
    (2, 'Martes',    '14:00:00', '18:00:00'),
    (2, 'Jueves',    '14:00:00', '18:00:00');
 
INSERT INTO pacientes (nombre, apellido, dni, fecha_nacimiento, telefono, obra_social) VALUES
    ('Ana Carolina', 'González', '35900342', '2010-03-12', '1145238801', 'OSDE'),
    ('Hugo',         'López',    '29762007', '1985-07-24', '1167452310', 'PAMI'),
    ('María José',   'Rodríguez','41234567', '1995-11-03', '1154789023', 'Swiss Medical');
 
INSERT INTO turnos (id_paciente, id_medico, fecha, hora, motivo_consulta, estado) VALUES
    (1, 1, '2025-05-20', '08:00:00', 'Control de rutina',   'Presente'),
    (2, 1, '2025-05-20', '09:00:00', 'Fiebre persistente',  'Ausente'),
    (3, 2, '2025-05-20', '14:00:00', 'Revisión lunar',      'Pendiente');
 
INSERT INTO notas_clinicas (id_turno, diagnostico, tratamiento, observaciones) VALUES
    (1, 'Paciente en buen estado general. Desarrollo acorde a la edad.',
        'Se indica continuar con alimentación balanceada y actividad física.',
        'Próximo control en 6 meses.');