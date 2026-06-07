package modelo.dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DisponibilidadDAO {

    private Connection conexion;

    // Constructor
    public DisponibilidadDAO() throws SQLException {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }


    //Verifica si un médico tiene configurado un día y horario específico.
    public boolean estaDisponible(int idMedico, String diaSemana,
                                  String hora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM disponibilidad_medico " +
                "WHERE id_medico = ? AND dia_semana = ? " +
                "AND hora_inicio <= ? AND hora_fin > ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1,    idMedico);
            stmt.setString(2, diaSemana);
            stmt.setString(3, hora);
            stmt.setString(4, hora);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }


    //Guarda la disponibilidad de un médico para un día.
    //Si ya existe para ese día la actualiza.
    public boolean guardarDisponibilidad(int idMedico, String diaSemana,
                                         String horaInicio, String horaFin) throws SQLException {
        String sql = "INSERT INTO disponibilidad_medico " +
                "(id_medico, dia_semana, hora_inicio, hora_fin) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE hora_inicio = ?, hora_fin = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1,    idMedico);
            stmt.setString(2, diaSemana);
            stmt.setString(3, horaInicio);
            stmt.setString(4, horaFin);
            stmt.setString(5, horaInicio);
            stmt.setString(6, horaFin);
            return stmt.executeUpdate() > 0;
        }
    }


    //Elimina la disponibilidad de un médico para un día.
    public boolean eliminarDisponibilidad(int idMedico, String diaSemana) throws SQLException {
        String sql = "DELETE FROM disponibilidad_medico " +
                "WHERE id_medico = ? AND dia_semana = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setString(2, diaSemana);
            return stmt.executeUpdate() > 0;
        }
    }


    //Retorna la disponibilidad configurada del médico como mapa donde la clave es el día y el valor es un array [hora_inicio, hora_fin].
    public Map<String, String[]> obtenerDisponibilidad(int idMedico) throws SQLException {
        String sql = "SELECT dia_semana, hora_inicio, hora_fin " +
                "FROM disponibilidad_medico WHERE id_medico = ?";
        Map<String, String[]> mapa = new HashMap<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapa.put(
                            rs.getString("dia_semana"),
                            new String[]{
                                    rs.getString("hora_inicio"),
                                    rs.getString("hora_fin")
                            }
                    );
                }
            }
        }
        return mapa;
    }

}