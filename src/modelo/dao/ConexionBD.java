package modelo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase ConexionBD
 *
 * Gestiona la conexión a la base de datos MySQL mediante JDBC.
 * Aplica el patrón Singleton para garantizar una única instancia
 * de conexión durante la ejecución del sistema.
 *
 */
public class ConexionBD {

    private static final String URL      = "jdbc:mysql://localhost:3306/sigmed_db" +
            "?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "";

    private static ConexionBD instancia;
    private Connection conexion;

    // Constructor
    private ConexionBD() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado. " +
                    "Verificá que mysql-connector-java esté en el classpath.", e);
        }
    }

    // Obtener instancia única
    public static ConexionBD getInstancia() throws SQLException {
        if (instancia == null || instancia.conexion.isClosed()) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // Obtener la conexión
    public Connection getConexion() {
        return conexion;
    }

    // Cerrar la conexión
    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                instancia = null;
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}