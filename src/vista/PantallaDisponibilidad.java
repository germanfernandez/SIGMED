package vista;

import sigmed.Main;
import controlador.GestorSesion;
import controlador.GestorDisponibilidad;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;

public class PantallaDisponibilidad extends JDialog {

    private GestorSesion    sesion;
    private GestorDisponibilidad gestorDisponibilidad;

    private static final String[] DIAS = {
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"
    };

    // Un checkbox + hora inicio + hora fin por día
    private JCheckBox[] chkDias        = new JCheckBox[6];
    private JTextField[] txtHoraInicio = new JTextField[6];
    private JTextField[] txtHoraFin    = new JTextField[6];

    private JButton btnGuardar;
    private JButton btnCerrar;
    private JLabel  lblMensaje;

    public PantallaDisponibilidad(JFrame parent, GestorSesion sesion) throws SQLException {
        super(parent, "Configurar Disponibilidad", true);
        this.sesion           = sesion;
        this.gestorDisponibilidad = new GestorDisponibilidad();
        initComponents();
        configurarDialogo();
        cargarDisponibilidadExistente();
    }

    private void configurarDialogo() {
        setSize(460, 380);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void initComponents() {

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));
        setContentPane(panel);

        // TÍTULO
        String medico = sesion.getMedicoActual() != null
                ? sesion.getMedicoActual().getNombreCompleto() : "";
        JLabel lblTitulo = new JLabel("Disponibilidad horaria – " + medico);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTitulo.setForeground(new Color(0, 0, 128));
        lblTitulo.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // GRID DE DÍAS
        JPanel panelDias = new JPanel(new GridLayout(7, 4, 8, 10));
        panelDias.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Encabezados
        panelDias.add(new JLabel("Día",         SwingConstants.LEFT));
        panelDias.add(new JLabel("Habilitado",  SwingConstants.CENTER));
        panelDias.add(new JLabel("Hora inicio", SwingConstants.CENTER));
        panelDias.add(new JLabel("Hora fin",    SwingConstants.CENTER));

        for (int i = 0; i < DIAS.length; i++) {
            final int idx = i;
            panelDias.add(new JLabel(DIAS[i]));

            chkDias[i] = new JCheckBox();
            chkDias[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelDias.add(chkDias[i]);

            txtHoraInicio[i] = new JTextField("08:00:00", 8);
            txtHoraInicio[i].setEnabled(false);
            panelDias.add(txtHoraInicio[i]);

            txtHoraFin[i] = new JTextField("12:00:00", 8);
            txtHoraFin[i].setEnabled(false);
            panelDias.add(txtHoraFin[i]);

            // Habilitar/deshabilitar campos según el checkbox
            chkDias[i].addActionListener(e -> {
                txtHoraInicio[idx].setEnabled(chkDias[idx].isSelected());
                txtHoraFin[idx].setEnabled(chkDias[idx].isSelected());
            });
        }
        panel.add(panelDias, BorderLayout.CENTER);

        // mensaje + botones
        JPanel panelSur = new JPanel(new BorderLayout(4, 6));
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 10));
        panelSur.add(lblMensaje, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnCerrar  = new JButton("Cerrar");
        btnGuardar = new JButton("Guardar disponibilidad");
        btnGuardar.setFont(new Font("Tahoma", Font.BOLD, 11));
        getRootPane().setDefaultButton(btnGuardar);
        panelBotones.add(btnCerrar);
        panelBotones.add(btnGuardar);
        panelSur.add(panelBotones, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);

        // EVENTOS
        btnGuardar.addActionListener(e -> accionGuardar());
        btnCerrar.addActionListener(e  -> dispose());
    }

    private void accionGuardar() {
        if (sesion.getMedicoActual() == null) return;
        int idMedico = sesion.getMedicoActual().getIdMedico();
        int guardados = 0;

        try {
            for (int i = 0; i < DIAS.length; i++) {
                if (chkDias[i].isSelected()) {
                    String inicio = txtHoraInicio[i].getText().trim();
                    String fin    = txtHoraFin[i].getText().trim();
                    if (inicio.isEmpty() || fin.isEmpty()) {
                        mostrarError("Completá los horarios de " + DIAS[i] + ".");
                        return;
                    }
                    gestorDisponibilidad.guardarDisponibilidad(idMedico, DIAS[i], inicio, fin);
                    guardados++;
                } else {
                    // Si se desmarcó el día, eliminar la disponibilidad
                    gestorDisponibilidad.eliminarDisponibilidad(idMedico, DIAS[i]);
                }
            }
            if (guardados == 0) {
                mostrarError("Seleccioná al menos un día de atención.");
                return;
            }
            mostrarOk("Disponibilidad guardada correctamente para " + guardados + " día(s).");
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void mostrarOk(String msg) {
        lblMensaje.setForeground(new Color(0, 100, 0));
        lblMensaje.setText(msg);
    }

    private void mostrarError(String msg) {
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setText(msg);
    }

    // Carga desde la BD la disponibilidad existente del médico y pre-rellena los checkboxes y campos de hora.
    private void cargarDisponibilidadExistente() {
        if (sesion.getMedicoActual() == null) return;
        try {
            int idMedico = sesion.getMedicoActual().getIdMedico();
            Map<String, String[]> disponibilidad =
                    gestorDisponibilidad.obtenerDisponibilidad(idMedico);

            for (int i = 0; i < DIAS.length; i++) {
                String[] horario = disponibilidad.get(DIAS[i]);
                if (horario != null) {
                    chkDias[i].setSelected(true);
                    txtHoraInicio[i].setText(horario[0]);
                    txtHoraInicio[i].setEnabled(true);
                    txtHoraFin[i].setText(horario[1]);
                    txtHoraFin[i].setEnabled(true);
                } else {
                    chkDias[i].setSelected(false);
                    txtHoraInicio[i].setEnabled(false);
                    txtHoraFin[i].setEnabled(false);
                }
            }
        } catch (java.sql.SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

}