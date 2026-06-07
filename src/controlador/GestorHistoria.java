package controlador;

import modelo.dao.NotaClinicaDAO;
import modelo.dao.TurnoDAO;
import modelo.entidades.NotaClinica;
import modelo.entidades.Turno;
import java.sql.SQLException;
import java.util.List;

public class GestorHistoria {

    private NotaClinicaDAO notaDAO;
    private TurnoDAO       turnoDAO;

    // Constructor
    public GestorHistoria() throws SQLException {
        this.notaDAO  = new NotaClinicaDAO();
        this.turnoDAO = new TurnoDAO();
    }

    //Registra una nota clínica y actualiza el turno a Atendido.
    public int registrarNota(int idTurno, String diagnostico,
                             String tratamiento, String observaciones) throws SQLException {

        Turno turno = turnoDAO.buscarPorId(idTurno);
        if (turno == null)
            throw new IllegalArgumentException("Turno no encontrado: " + idTurno);

        // Verificar que no exista ya una nota para este turno
        if (notaDAO.buscarPorTurno(idTurno) != null)
            throw new IllegalStateException("Este turno ya tiene una nota clínica registrada.");

        // Crear y persistir la nota
        NotaClinica nota = new NotaClinica(
                turno,
                java.time.LocalDate.now().toString(),
                diagnostico, tratamiento, observaciones
        );
        // Solo guarda la nota — el turno se finaliza con finalizarAtencion()
        return notaDAO.insertar(nota);
    }


    //Consulta el historial clínico de un paciente.
    public List<NotaClinica> consultarHistorial(int idPaciente,
                                                int idMedico) throws SQLException {
        return notaDAO.listarHistorialPorPacienteYMedico(idPaciente, idMedico);
    }

    //Obtiene la nota clínica de un turno específico.
    public NotaClinica obtenerNotaPorTurno(int idTurno) throws SQLException {
        return notaDAO.buscarPorTurno(idTurno);
    }

    /**
     * Finaliza la atención del turno cambiando su estado a Atendido.
     * Verifica que exista una nota clínica registrada antes de finalizar.
     *
     * @throws IllegalStateException si no existe nota clínica para el turno
     */
    public void finalizarAtencion(int idTurno) throws SQLException {
        // Verificar que exista nota clínica para este turno
        NotaClinica nota = notaDAO.buscarPorTurno(idTurno);
        if (nota == null) {
            throw new IllegalStateException(
                    "Debe registrar la nota clínica antes de finalizar la atención.");
        }
        // Cambiar estado del turno a Atendido
        turnoDAO.actualizarEstado(idTurno, Turno.ESTADO_ATENDIDO);
    }

}