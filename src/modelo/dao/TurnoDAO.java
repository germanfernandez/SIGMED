package modelo.dao;

import modelo.entidades.Turno;
import modelo.entidades.Paciente;
import modelo.entidades.Medico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    private Connection   conexion;
    private PacienteDAO  pacienteDAO;
    private MedicoDAO    medicoDAO;

    // Constructor
    public TurnoDAO() throws SQLException {
        this.conexion    = ConexionBD.getInstancia().getConexion();
        this.pacienteDAO = new PacienteDAO();
        this.medicoDAO   = new MedicoDAO();
    }

    public int insertar(Turno t) throws SQLException {
        String sql = "INSERT INTO turnos " +
                "(id_paciente, id_medico, fecha, hora, motivo_consulta, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1,    t.getPaciente().getIdPaciente());
            stmt.setInt(2,    t.getMedico().getIdMedico());
            stmt.setString(3, t.getFecha());
            stmt.setString(4, t.getHora());
            stmt.setString(5, t.getMotivoConsulta());
            stmt.setString(6, t.getEstado());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public Turno buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM turnos WHERE id_turno = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // LISTAR POR FECHA (turnos del día)
    public List<Turno> listarPorFecha(String fecha) throws SQLException {
        String sql = "SELECT * FROM turnos WHERE fecha = ? ORDER BY hora";
        List<Turno> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Turno> listarPorMedicoYFecha(int idMedico, String fecha) throws SQLException {
        String sql = "SELECT * FROM turnos " +
                "WHERE id_medico = ? AND fecha = ? ORDER BY hora";
        List<Turno> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setString(2, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Turno> listarPorMedicoYPeriodo(int idMedico,
                                               String fechaInicio, String fechaFin) throws SQLException {
        String sql = "SELECT * FROM turnos " +
                "WHERE id_medico = ? AND fecha BETWEEN ? AND ? ORDER BY fecha, hora";
        List<Turno> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setString(2, fechaInicio);
            stmt.setString(3, fechaFin);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Turno> listarSalaEspera(int idMedico, String fecha) throws SQLException {
        String sql = "SELECT * FROM turnos " +
                "WHERE id_medico = ? AND fecha = ? AND estado = 'Presente' ORDER BY hora";
        List<Turno> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setString(2, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean verificarDisponibilidad(int idMedico,
                                           String fecha, String hora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM turnos " +
                "WHERE id_medico = ? AND fecha = ? AND hora = ? " +
                "AND estado != 'Ausente'";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setString(2, fecha);
            stmt.setString(3, hora);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) == 0; // true = disponible
            }
        }
        return false;
    }

    public boolean actualizarEstado(int idTurno, String nuevoEstado) throws SQLException {
        String sql = "UPDATE turnos SET estado = ?, fecha_modificacion = NOW() " +
                "WHERE id_turno = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idTurno);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean modificar(Turno t) throws SQLException {
        String sql = "UPDATE turnos SET " +
                "id_paciente = ?, id_medico = ?, fecha = ?, hora = ?, " +
                "motivo_consulta = ?, estado = ?, fecha_modificacion = NOW() " +
                "WHERE id_turno = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1,    t.getPaciente().getIdPaciente());
            stmt.setInt(2,    t.getMedico().getIdMedico());
            stmt.setString(3, t.getFecha());
            stmt.setString(4, t.getHora());
            stmt.setString(5, t.getMotivoConsulta());
            stmt.setString(6, t.getEstado());
            stmt.setInt(7,    t.getIdTurno());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM turnos WHERE id_turno = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // MAPEO ResultSet → Turno
    private Turno mapear(ResultSet rs) throws SQLException {
        Paciente paciente = pacienteDAO.buscarPorId(rs.getInt("id_paciente"));
        Medico   medico   = medicoDAO.buscarPorId(rs.getInt("id_medico"));

        Turno turno = new Turno(
                rs.getInt("id_turno"),
                paciente,
                medico,
                rs.getString("fecha"),
                rs.getString("hora"),
                rs.getString("motivo_consulta")
        );
        turno.actualizarEstado(rs.getString("estado"));
        return turno;
    }

    //Retorna las horas ocupadas de un médico en una fecha.
    // Solo considera turnos Pendiente y Presente (no Ausente ni Atendido).
    public List<String> obtenerHorasOcupadas(int idMedico,
                                             String fecha) throws SQLException {
        String sql = "SELECT hora FROM turnos " +
                "WHERE id_medico = ? AND fecha = ? " +
                "AND estado IN ('Pendiente', 'Presente')";
        List<String> ocupadas = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setString(2, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ocupadas.add(rs.getString("hora"));
            }
        }
        return ocupadas;
    }

    //Lista todos los turnos en un rango de fechas (todos los médicos).
    public List<Turno> listarPorRango(String fechaInicio, String fechaFin) throws SQLException {
        String sql = "SELECT * FROM turnos WHERE fecha BETWEEN ? AND ? ORDER BY fecha, hora";
        List<Turno> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, fechaInicio);
            stmt.setString(2, fechaFin);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

}