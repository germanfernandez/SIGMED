package controlador;

import modelo.dao.TurnoDAO;
import modelo.entidades.Turno;
import java.sql.SQLException;
import java.util.List;

public class GestorAgenda {

    private TurnoDAO turnoDAO;

    // Constructor
    public GestorAgenda() throws SQLException {
        this.turnoDAO = new TurnoDAO();
    }


    //Retorna la agenda del médico para una fecha específica.
    public List<Turno> obtenerAgendaPorFecha(int idMedico, String fecha) throws SQLException {
        return turnoDAO.listarPorMedicoYFecha(idMedico, fecha);
    }


    //Retorna la agenda del médico en un período de fechas.
    public List<Turno> obtenerAgendaPorPeriodo(int idMedico, String fechaInicio, String fechaFin) throws SQLException {
        return turnoDAO.listarPorMedicoYPeriodo(idMedico, fechaInicio, fechaFin);
    }
}