package controlador;

import modelo.dao.UsuarioDAO;
import modelo.entidades.Usuario;
import modelo.dao.MedicoDAO;
import modelo.entidades.Medico;
import java.sql.SQLException;

public class GestorSesion {

    private UsuarioDAO usuarioDAO;
    private MedicoDAO  medicoDAO;

    // Datos de la sesión activa
    private Usuario usuarioActual;  // entidad Usuario autenticada
    private String  rolActual;
    private int     idUsuarioActual;
    private Medico  medicoActual; // null si el usuario es Secretaria

    // Constructor
    public GestorSesion() throws SQLException {
        this.usuarioDAO = new UsuarioDAO();
        this.medicoDAO  = new MedicoDAO();
    }

    /**
     * Valida las credenciales del usuario.
     * Si son correctas carga el rol y el médico asociado (si corresponde).
     *
     * @return true si el login fue exitoso
     */
    public boolean validarCredenciales(String username, String password) {
        try {
            Usuario usuario = usuarioDAO.validarCredenciales(username, password);

            if (usuario == null) return false; // credenciales incorrectas

            this.usuarioActual   = usuario;
            this.rolActual       = usuario.getRol();
            this.idUsuarioActual = usuario.getIdUsuario();

            // Si el rol es médico, cargamos el objeto Medico de la sesión
            if (!usuario.esSecretaria()) {
                this.medicoActual = medicoDAO.buscarPorIdUsuario(idUsuarioActual);
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }

    // Cerrar sesión
    public void cerrarSesion() {
        this.usuarioActual    = null;
        this.rolActual        = null;
        this.idUsuarioActual  = 0;
        this.medicoActual     = null;
    }

    // Getters
    public String getRolActual()       { return rolActual; }
    public int    getIdUsuarioActual() { return idUsuarioActual; }
    public Medico getMedicoActual()    { return medicoActual; }

    public boolean esSecretaria() {
        return "Secretaria".equals(rolActual);
    }
    public boolean esMedico() {
        return rolActual != null && !rolActual.equals("Secretaria");
    }
}