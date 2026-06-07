package controlador;

import modelo.dao.TurnoDAO;
import modelo.entidades.Turno;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class GestorRecepcion {

    private TurnoDAO turnoDAO;

    // Constructor
    public GestorRecepcion() throws SQLException {
        this.turnoDAO = new TurnoDAO();
    }

    //Retorna los turnos del día actual para todos los médicos.
    public List<Turno> obtenerTurnosDelDia() throws SQLException {
        String hoy = LocalDate.now().toString();
        return turnoDAO.listarPorFecha(hoy);
    }

    /**
     * Marca la llegada de un paciente al consultorio.
     * Actualiza el estado del turno de Pendiente a Presente.
     *
     * @return true si la operación fue exitosa
     */
    public boolean recepcionarPaciente(int idTurno) throws SQLException {
        Turno turno = turnoDAO.buscarPorId(idTurno);

        if (turno == null)
            throw new IllegalArgumentException("Turno no encontrado.");

        if (!turno.getEstado().equals(Turno.ESTADO_PENDIENTE))
            throw new IllegalStateException(
                    "El turno no puede ser recepcionado. Estado actual: " + turno.getEstado());

        return turnoDAO.actualizarEstado(idTurno, Turno.ESTADO_PRESENTE);
    }


    //Retorna los pacientes en sala de espera de un médico.
    public List<Turno> obtenerSalaEspera(int idMedico) throws SQLException {
        String hoy = LocalDate.now().toString();
        return turnoDAO.listarSalaEspera(idMedico, hoy);
    }
}