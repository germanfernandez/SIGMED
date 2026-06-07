package vista;

import sigmed.Main;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MenuSIGMED extends JFrame {

    private JTextField     txtUsuario;
    private JPasswordField txtContrasena;
    private JButton        btnIngresar;
    private JButton        btnCancelar;
    private JLabel         lblMensaje;

    public MenuSIGMED() {
        initComponents();
        configurarVentana();
    }

    private void configurarVentana() {
        setTitle("SIGMED – Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 280);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(16, 20, 16, 20));
        setContentPane(panelPrincipal);

        // título
        JLabel lblTitulo = new JLabel(
            "Sistema de Gestión de Consultorio Médico",
            SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(0, 0, 128));
        lblTitulo.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // formulario
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 8, 10));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            new EmptyBorder(16, 16, 16, 16)
        ));

        panelFormulario.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panelFormulario.add(txtUsuario);

        panelFormulario.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField();
        panelFormulario.add(txtContrasena);

        panelFormulario.add(new JLabel(""));
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 10));
        lblMensaje.setForeground(Color.RED);
        panelFormulario.add(lblMensaje);

        panelFormulario.add(new JLabel(""));
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnIngresar = new JButton("Ingresar");
        btnCancelar = new JButton("Cancelar");
        getRootPane().setDefaultButton(btnIngresar);
        panelBotones.add(btnIngresar);
        panelBotones.add(btnCancelar);
        panelFormulario.add(panelBotones);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // pie
        JLabel lblPie = new JLabel("Acceso restringido al personal autorizado · SIGMED v1.0", SwingConstants.CENTER);
        lblPie.setFont(new Font("Tahoma", Font.PLAIN, 10));
        lblPie.setForeground(Color.GRAY);
        panelPrincipal.add(lblPie, BorderLayout.SOUTH);

        // EVENTOS
        btnIngresar.addActionListener(e -> accionIngresar());
        btnCancelar.addActionListener(e -> System.exit(0));
    }

    private void accionIngresar() {
        String usuario    = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Usuario y contraseña son obligatorios.");
            return;
        }

        // GestorSesion valida y determina el rol desde la BD
        boolean ok = Main.sesion.validarCredenciales(usuario, contrasena);

        if (!ok) {
            mostrarError("Usuario o contraseña incorrectos.");
            txtContrasena.setText("");
            txtUsuario.requestFocus();
            return;
        }

        dispose();
        Main.abrirVistaPorRol(); // redirige según rol retornado
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setText(mensaje);
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MenuSIGMED().setVisible(true);
        });
    }
}
