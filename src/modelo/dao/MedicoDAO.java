package modelo.dao;

import modelo.entidades.Medico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {

    private Connection conexion;

    // Constructor
    public MedicoDAO() throws SQLException {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    // BUSCAR POR ID
    public Medico buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM medicos WHERE id_medico = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // BUSCAR POR ID_USUARIO
    public Medico buscarPorIdUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM medicos WHERE id_usuario = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // LISTAR TODOS
    public List<Medico> listarTodos() throws SQLException {
        String sql = "SELECT * FROM medicos ORDER BY apellido, nombre";
        List<Medico> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // MAPEO ResultSet → Medico
    private Medico mapear(ResultSet rs) throws SQLException {
        return new Medico(
                rs.getInt("id_medico"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni") != null ? rs.getString("dni") : "",
                rs.getString("telefono") != null ? rs.getString("telefono") : "",
                rs.getString("especialidad"),
                rs.getString("matricula")
        );
    }
}