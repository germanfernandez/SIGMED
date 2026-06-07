USE sigmed_db;
 
SELECT
    p.apellido                          AS 'Apellido paciente',
    p.nombre                            AS 'Nombre paciente',
    CONCAT(m.apellido, ', ', m.nombre)  AS 'Médico',
    m.especialidad                      AS 'Especialidad',
    t.fecha                             AS 'Fecha turno',
    t.hora                              AS 'Hora',
    t.motivo_consulta                   AS 'Motivo',
    t.estado                            AS 'Estado'
FROM turnos t
JOIN pacientes p ON t.id_paciente = p.id_paciente
JOIN medicos m   ON t.id_medico   = m.id_medico
ORDER BY t.fecha, t.hora;
 