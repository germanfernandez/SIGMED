package controlador;

import modelo.dao.TurnoDAO;
import modelo.dao.PacienteDAO;
import modelo.entidades.Turno;
import modelo.entidades.Paciente;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class GestorSala {

    private TurnoDAO    turnoDAO;
    private PacienteDAO pacienteDAO;

    // Constructor
    public GestorSala() throws SQLException {
        this.turnoDAO    = new TurnoDAO();
        this.pacienteDAO = new PacienteDAO();
    }


    //Retorna los pacientes en sala de espera del médico autenticado.
    // Solo incluye turnos con estado Presente.
    public List<Turno> obtenerPacientesEnEspera(int idMedico) throws SQLException {
        String hoy = LocalDate.now().toString();
        return turnoDAO.listarSalaEspera(idMedico, hoy);
    }


    //Retorna el perfil completo de un paciente por ID.
    // Utilizado al seleccionar un paciente de la sala de espera.
    public Paciente obtenerPerfilPaciente(int idPaciente) throws SQLException {
        return pacienteDAO.buscarPorId(idPaciente);
    }
}