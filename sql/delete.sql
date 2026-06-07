USE sigmed_db;
 
DELETE FROM notas_clinicas;
DELETE FROM turnos;
DELETE FROM disponibilidad_medico;
DELETE FROM pacientes;
DELETE FROM medicos;
DELETE FROM usuarios;
 
-- Verificación
SELECT * FROM notas_clinicas;
SELECT * FROM turnos;
SELECT * FROM disponibilidad_medico;
SELECT * FROM pacientes;
SELECT * FROM medicos;
SELECT * FROM usuarios;