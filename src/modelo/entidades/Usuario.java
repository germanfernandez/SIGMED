package modelo.entidades;

public class Usuario {

    // Atributos (encapsulados)
    private int     idUsuario;
    private String  username;
    private String  password;
    private String  rol;
    private boolean activo;

    // Constructor completo
    public Usuario(int idUsuario, String username,
                   String password, String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.username  = username;
        this.password  = password;
        this.rol       = rol;
        this.activo    = activo;
    }

    // Constructor sin ID (para alta nueva)
    public Usuario(String username, String password, String rol) {
        this(0, username, password, rol, true);
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("El username no puede estar vacío.");
        this.username = username;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        this.password = password;
    }

    public String getRol() { return rol; }
    public void setRol(String rol) {
        if (rol == null || rol.trim().isEmpty())
            throw new IllegalArgumentException("El rol no puede estar vacío.");
        this.rol = rol;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // Métodos utilitarios
    public boolean esSecretaria() { return "Secretaria".equals(rol); }
    public boolean esMedico()     { return !esSecretaria(); }

    // toString
    @Override
    public String toString() {
        return username + " [" + rol + "]" + (activo ? "" : " (inactivo)");
    }
}