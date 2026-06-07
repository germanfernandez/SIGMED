package modelo.dao;

import modelo.entidades.NotaClinica;
import modelo.entidades.Turno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotaClinicaDAO {

    private Connection conexion;
    private TurnoDAO   turnoDAO;

    // Constructor
    public NotaClinicaDAO() throws SQLException {
        this.conexion = ConexionBD.getInstancia().getConexion();
        this.turnoDAO = new TurnoDAO();
    }

    // INSERTAR
    public int insertar(NotaClinica n) throws SQLException {
        String sql = "INSERT INTO notas_clinicas " +
                "(id_turno, fecha, diagnostico, tratamiento, observaciones) " +
                "VALUES (?, CURRENT_DATE, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1,    n.getTurno().getIdTurno());
            stmt.setString(2, n.getDiagnostico());
            stmt.setString(3, n.getTratamiento());
            stmt.setString(4, n.getObservaciones());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // BUSCAR POR TURNO
    public NotaClinica buscarPorTurno(int idTurno) throws SQLException {
        String sql = "SELECT * FROM notas_clinicas WHERE id_turno = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idTurno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // LISTAR HISTORIAL POR PACIENTE
    // obtiene todas las notas de un paciente a través de los turnos
    public List<NotaClinica> listarHistorialPorPaciente(int idPaciente) throws SQLException {
        String sql = "SELECT n.* FROM notas_clinicas n " +
                "JOIN turnos t ON n.id_turno = t.id_turno " +
                "WHERE t.id_paciente = ? " +
                "ORDER BY n.fecha DESC";
        List<NotaClinica> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPaciente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // LISTAR HISTORIAL POR PACIENTE Y MÉDICO
    // Restringe el historial al médico autenticado
    public List<NotaClinica> listarHistorialPorPacienteYMedico(
            int idPaciente, int idMedico) throws SQLException {
        String sql = "SELECT n.* FROM notas_clinicas n " +
                "JOIN turnos t ON n.id_turno = t.id_turno " +
                "WHERE t.id_paciente = ? AND t.id_medico = ? " +
                "ORDER BY n.fecha DESC";
        List<NotaClinica> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPaciente);
            stmt.setInt(2, idMedico);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── MAPEO ResultSet → NotaClinica ────────────────────────────────
    private NotaClinica mapear(ResultSet rs) throws SQLException {
        Turno turno = turnoDAO.buscarPorId(rs.getInt("id_turno"));
        return new NotaClinica(
                rs.getInt("id_nota"),
                turno,
                rs.getString("fecha"),
                rs.getString("diagnostico"),
                rs.getString("tratamiento"),
                rs.getString("observaciones")
        );
    }
}