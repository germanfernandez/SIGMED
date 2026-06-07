package controlador;

import modelo.dao.DisponibilidadDAO;
import java.sql.SQLException;
import java.util.Map;

public class GestorDisponibilidad {

    private DisponibilidadDAO disponibilidadDAO;

    // Constructor
    public GestorDisponibilidad() throws SQLException {
        this.disponibilidadDAO = new DisponibilidadDAO();
    }


    //Verifica si un médico tiene disponibilidad para un día y horario.
    public boolean verificarDisponibilidad(int idMedico, String diaSemana, String hora) throws SQLException {
        return disponibilidadDAO.estaDisponible(idMedico, diaSemana, hora);
    }


    //Guarda o actualiza la disponibilidad de un médico para un día.
    //Si ya existe para ese día la sobreescribe.
    public boolean guardarDisponibilidad(int idMedico, String diaSemana, String horaInicio, String horaFin) throws SQLException {
        if (horaInicio == null || horaInicio.trim().isEmpty() || horaFin    == null || horaFin.trim().isEmpty())
            throw new IllegalArgumentException("Los horarios de inicio y fin son obligatorios.");
        return disponibilidadDAO.guardarDisponibilidad(
                idMedico, diaSemana, horaInicio, horaFin);
    }


    //Elimina la disponibilidad de un médico para un día específico.
    public boolean eliminarDisponibilidad(int idMedico, String diaSemana) throws SQLException {
        return disponibilidadDAO.eliminarDisponibilidad(idMedico, diaSemana);
    }


    //Retorna la disponibilidad configurada del médico.
    //Clave: día de la semana. Valor: [hora_inicio, hora_fin].
    public Map<String, String[]> obtenerDisponibilidad(int idMedico) throws SQLException {
        return disponibilidadDAO.obtenerDisponibilidad(idMedico);
    }

}