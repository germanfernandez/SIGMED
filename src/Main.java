package sigmed;

import controlador.GestorSesion;
import vista.MenuSIGMED;
import vista.PantallaTurno;
import vista.PantallaSala;

import javax.swing.*;
import java.awt.Window;
import java.sql.SQLException;

// Clase Main
public class Main {

    // Sesión compartida entre todas las vistas
    public static GestorSesion sesion;

    static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Look & Feel del sistema operativo
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("No se pudo aplicar el Look & Feel: "
                        + e.getMessage());
            }

            // Inicializar GestorSesion (verifica conexión a MySQL)
            try {
                sesion = new GestorSesion();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo conectar a la base de datos.\n" +
                                "Verificá que WampServer esté iniciado y MySQL activo.\n\n" +
                                "Detalle: " + e.getMessage(),
                        "Error de conexión",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }

            // Abrir pantalla de login
            abrirLogin();
        });
    }


    //Abre la pantalla de inicio de sesión.
    public static void abrirLogin() {
        new MenuSIGMED().setVisible(true);
    }

    //Redirige a la vista correspondiente según el rol autenticado.
    //Llamado desde LoginFrame tras un login exitoso.
    public static void abrirVistaPorRol() {
        String rol = sesion.getRolActual();

        if (rol == null) {
            abrirLogin();
            return;
        }

        switch (rol) {

            case "Secretaria":
                try {
                    new PantallaTurno(sesion).setVisible(true);
                } catch (SQLException e) {
                    mostrarErrorConexion(e);
                }
                break;

            case "Médico Pediatra":
            case "Médica Dermatóloga":
                try {
                    new PantallaSala(sesion).setVisible(true);
                } catch (SQLException e) {
                    mostrarErrorConexion(e);
                }
                break;

            default:
                JOptionPane.showMessageDialog(
                        null,
                        "Rol no reconocido: " + rol,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                abrirLogin();
        }
    }

     //Cierra la sesión activa y vuelve al login.
     //Llamado desde cualquier vista al seleccionar Salir.
    public static void cerrarSesionYVolver() {
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Deseás cerrar la sesión y volver al inicio?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            sesion.cerrarSesion();
            // Cerrar todas las ventanas abiertas (PantallaTurno, PantallaSala, dialogs)
            for (Window w : Window.getWindows()) {
                w.dispose();
            }
            abrirLogin();
        }
    }


    //Muestra un diálogo de error de conexión a la base de datos.
    //Llamado desde cualquier vista cuando falla una operación JDBC.
    public static void mostrarErrorConexion(SQLException e) {
        JOptionPane.showMessageDialog(
                null,
                "Error de conexión con la base de datos.\n\n" +
                        "Detalle: " + e.getMessage(),
                "Error de conexión",
                JOptionPane.ERROR_MESSAGE
        );
    }
}