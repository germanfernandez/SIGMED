package modelo.dao;

import modelo.entidades.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UsuarioDAO {

    private Connection conexion;

    // Constructor
    public UsuarioDAO() throws SQLException {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    //Valida las credenciales y retorna la entidad Usuario si son correctas.
    //Retorna null si las credenciales son incorrectas o el usuario está inactivo.
    public Usuario validarCredenciales(String username, String password) throws SQLException {
        // Busca el usuario por username (activo)
        // La verificación del password se hace con BCrypt
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashAlmacenado = rs.getString("password");
                    // BCrypt verifica el password ingresado contra el hash guardado
                    if (BCrypt.checkpw(password, hashAlmacenado)) {
                        return mapear(rs);
                    }
                }
            }
        }
        return null; // credenciales incorrectas o usuario inactivo
    }

    //Retorna el id_usuario a partir del username.
    public int obtenerIdUsuario(String username) throws SQLException {
        String sql = "SELECT id_usuario FROM usuarios WHERE username = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id_usuario");
            }
        }
        return -1;
    }

    // MAPEO ResultSet → Usuario
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("rol"),
                rs.getInt("activo") == 1
        );
    }
}