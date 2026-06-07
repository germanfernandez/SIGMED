package modelo.dao;

import modelo.entidades.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    private Connection conexion;

    // Constructor
    public PacienteDAO() throws SQLException {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    //Registra un nuevo paciente en la base de datos.
    //Retorna el ID generado o -1 si falla.
    public int insertar(Paciente p) throws SQLException {
        String sql = "INSERT INTO pacientes " +
                "(nombre, apellido, dni, fecha_nacimiento, telefono, email, obra_social) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getApellido());
            stmt.setString(3, p.getDni());
            stmt.setString(4, p.getFechaNacimiento());
            stmt.setString(5, p.getTelefono());
            stmt.setString(6, p.getEmail());
            stmt.setString(7, p.getObraSocial());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // BUSCAR POR ID
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE id_paciente = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // BUSCAR POR DNI
    public Paciente buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE dni = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // BUSCAR POR NOMBRE, APELLIDO O DNI
    public List<Paciente> buscarPorNombre(String criterio) throws SQLException {
        String sql = "SELECT * FROM pacientes " +
                "WHERE nombre LIKE ? OR apellido LIKE ? OR dni LIKE ? " +
                "ORDER BY apellido, nombre";
        List<Paciente> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            String patron = "%" + criterio + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);
            stmt.setString(3, patron);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Paciente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM pacientes ORDER BY apellido, nombre";
        List<Paciente> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean modificar(Paciente p) throws SQLException {
        String sql = "UPDATE pacientes SET " +
                "nombre = ?, apellido = ?, dni = ?, fecha_nacimiento = ?, " +
                "telefono = ?, email = ?, obra_social = ? " +
                "WHERE id_paciente = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getApellido());
            stmt.setString(3, p.getDni());
            stmt.setString(4, p.getFechaNacimiento());
            stmt.setString(5, p.getTelefono());
            stmt.setString(6, p.getEmail());
            stmt.setString(7, p.getObraSocial());
            stmt.setInt(8, p.getIdPaciente());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE id_paciente = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // MAPEO ResultSet → Paciente
    private Paciente mapear(ResultSet rs) throws SQLException {
        return new Paciente(
                rs.getInt("id_paciente"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("fecha_nacimiento"),
                rs.getString("email"),
                rs.getString("obra_social")
        );
    }
}