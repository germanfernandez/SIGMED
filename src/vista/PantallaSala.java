package vista;

import sigmed.Main;
import controlador.GestorSesion;
import controlador.GestorAgenda;
import controlador.GestorHistoria;
import controlador.GestorSala;
import controlador.GestorRecepcion;
import modelo.entidades.Turno;
import java.util.ArrayList;
import modelo.entidades.NotaClinica;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class PantallaSala extends JFrame {

    // Sesión y gestores
    private GestorSesion    sesion;
    private GestorHistoria  gestorHistoria;
    private GestorSala      gestorSala;
    private GestorAgenda    gestorAgenda;
    private GestorRecepcion gestorRecepcion;

    // Lista de turnos en espera
    private List<Turno> turnosEnEspera;

    // Componentes sala de espera
    private DefaultListModel<String> modeloSala;
    private JList<String>            listaSala;
    private JButton                  btnFinalizar;

    // Componentes historia clínica
    private JLabel                   lblNombrePaciente;
    private JLabel                   lblDatosPaciente;
    private JLabel                   lblRestriccionAcceso;
    private DefaultListModel<String>      modeloHistorial;
    private List<NotaClinica>             historialNotas = new ArrayList<>();
    private JList<String>            listaHistorial;
    private JTextArea                txtDiagnostico;
    private JTextArea                txtTratamiento;
    private JTextArea                txtObservaciones;
    private JButton                  btnGuardarNota;
    private JButton                  btnCancelarNota;
    private JLabel                   lblEstado;
    private JPanel                   panelContenido;
    private java.awt.CardLayout      cardLayout;

    // Constructor
    public PantallaSala(GestorSesion sesion) throws SQLException {
        this.sesion          = sesion;
        this.gestorHistoria  = new GestorHistoria();
        this.gestorSala      = new GestorSala();
        this.gestorAgenda    = new GestorAgenda();
        this.gestorRecepcion = new GestorRecepcion();
        initComponents();
        configurarVentana();
        cargarSalaDeEspera();
    }

    private void configurarVentana() {
        String nombreMedico = sesion.getMedicoActual() != null
                ? sesion.getMedicoActual().getNombreCompleto()
                : sesion.getRolActual();
        setTitle("SIGMED – Atención de Pacientes | " + nombreMedico);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.cerrarSesionYVolver();
            }
        });
        setSize(900, 560);
        setLocationRelativeTo(null);
    }

    private void initComponents() {

        // MENU BAR
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // Colores para estado activo / inactivo
        Color colorActivo   = new Color(180, 190, 210);
        Color colorInactivo = menuBar.getBackground();
        Font  fuenteActiva  = new Font("Tahoma", Font.BOLD, 12);
        Font  fuenteNormal  = new Font("Tahoma", Font.PLAIN, 12);

        JMenuItem menuSala   = new JMenuItem("Sala de Espera");
        JMenuItem menuAgenda = new JMenuItem("Mi Agenda");
        JMenuItem[] navItems = {menuSala, menuAgenda};

        // Método para resaltar el ítem activo
        Runnable resetNav = () -> {
            for (JMenuItem item : navItems) {
                item.setBackground(colorInactivo);
                item.setFont(fuenteNormal);
                item.setOpaque(false);
            }
        };

        menuSala.addActionListener(e -> {
            resetNav.run();
            menuSala.setBackground(colorActivo);
            menuSala.setFont(fuenteActiva);
            menuSala.setOpaque(true);
            cardLayout.show(panelContenido, "sala");
        });
        menuBar.add(menuSala);

        menuAgenda.addActionListener(e -> {
            resetNav.run();
            menuAgenda.setBackground(colorActivo);
            menuAgenda.setFont(fuenteActiva);
            menuAgenda.setOpaque(true);
            cardLayout.show(panelContenido, "agenda");
        });
        menuBar.add(menuAgenda);

        JMenuItem menuDisponibilidad = new JMenuItem("Disponibilidad");
        menuDisponibilidad.addActionListener(e -> {
            // Sombrear Disponibilidad mientras el diálogo esté abierto
            menuDisponibilidad.setBackground(colorActivo);
            menuDisponibilidad.setFont(fuenteActiva);
            menuDisponibilidad.setOpaque(true);
            try {
                new PantallaDisponibilidad(this, sesion).setVisible(true);
                // Al cerrarse → restaurar Sala de Espera
                menuDisponibilidad.setBackground(colorInactivo);
                menuDisponibilidad.setFont(fuenteNormal);
                menuDisponibilidad.setOpaque(false);
                menuSala.setBackground(colorActivo);
                menuSala.setFont(fuenteActiva);
                menuSala.setOpaque(true);
            } catch (SQLException ex) { Main.mostrarErrorConexion(ex); }
        });
        menuBar.add(menuDisponibilidad);

        JMenuItem menuSalir = new JMenuItem("Salir");
        menuSalir.addActionListener(e -> Main.cerrarSesionYVolver());
        menuBar.add(menuSalir);

        // Marcar Sala de Espera como activo por defecto
        menuSala.setBackground(colorActivo);
        menuSala.setFont(fuenteActiva);
        menuSala.setOpaque(true);

        setJMenuBar(menuBar);

        // PANEL PRINCIPAL
        JPanel panelPrincipal = new JPanel(new BorderLayout(6, 4));
        panelPrincipal.setBorder(new EmptyBorder(6, 6, 0, 6));
        setContentPane(panelPrincipal);

        // TABS
        cardLayout     = new java.awt.CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.add(crearPanelSala(),   "sala");
        panelContenido.add(crearPanelAgenda(), "agenda");
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        // BARRA DE ESTADO
        JPanel panelEstado = new JPanel(new BorderLayout());
        panelEstado.setBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY));
        lblEstado = new JLabel(
                " Atención de Pacientes | Médico");
        lblEstado.setFont(new Font("Tahoma", Font.PLAIN, 9));
        lblEstado.setForeground(Color.GRAY);
        panelEstado.add(lblEstado, BorderLayout.WEST);
        panelPrincipal.add(panelEstado, BorderLayout.SOUTH);
    }

    private JPanel crearPanelSala() {
        JPanel panel = new JPanel(new BorderLayout(6, 0));

        // JList sala de espera
        String tituloSala = sesion.getMedicoActual() != null
                ? "Sala de espera — " + sesion.getMedicoActual().getNombreCompleto()
                : "Sala de espera";

        JPanel panelSala = new JPanel(new BorderLayout(4, 6));
        panelSala.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), tituloSala,
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Tahoma", Font.BOLD, 11), new Color(0, 0, 128)
                ),
                new EmptyBorder(4, 4, 4, 4)
        ));
        panelSala.setPreferredSize(new Dimension(220, 0));

        modeloSala = new DefaultListModel<>();
        listaSala  = new JList<>(modeloSala);
        listaSala.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSala.setFixedCellHeight(56);
        listaSala.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JPanel cell = new JPanel(new BorderLayout(2, 0));
                cell.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                String[] p = value.toString().split("\\|");
                JLabel lHora   = new JLabel(p.length > 0 ? p[0] : "");
                JLabel lNombre = new JLabel(p.length > 1 ? p[1] : "");
                JLabel lMotivo = new JLabel(p.length > 2 ? p[2] : "");
                lHora.setFont(new Font("Tahoma", Font.PLAIN, 9));
                lNombre.setFont(new Font("Tahoma", Font.BOLD, 11));
                lMotivo.setFont(new Font("Tahoma", Font.PLAIN, 10));
                JPanel info = new JPanel(new GridLayout(3, 1));
                info.add(lHora); info.add(lNombre); info.add(lMotivo);
                cell.add(info, BorderLayout.CENTER);
                Color bg = isSelected ? new Color(0, 0, 128) : Color.WHITE;
                cell.setBackground(bg);
                info.setBackground(bg);
                lHora.setForeground(isSelected   ? new Color(170,170,220) : Color.GRAY);
                lNombre.setForeground(isSelected ? Color.WHITE             : Color.BLACK);
                lMotivo.setForeground(isSelected ? new Color(170,170,220) : Color.GRAY);
                return cell;
            }
        });

        panelSala.add(new JScrollPane(listaSala), BorderLayout.CENTER);
        btnFinalizar = new JButton("Finalizar Atencion");
        btnFinalizar.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelSala.add(btnFinalizar, BorderLayout.SOUTH);
        panel.add(panelSala, BorderLayout.WEST);

        // Historia clínica
        JPanel panelHistoria = new JPanel(new BorderLayout(6, 4));

        // Info del paciente + restricción de acceso
        JPanel panelInfoPaciente = new JPanel(new BorderLayout(4, 2));
        panelInfoPaciente.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.GRAY),
                new EmptyBorder(4, 8, 4, 8)
        ));
        panelInfoPaciente.setBackground(new Color(232, 228, 220));

        JPanel panelNombreRestr = new JPanel(new BorderLayout(4, 2));
        panelNombreRestr.setBackground(new Color(232, 228, 220));
        lblNombrePaciente = new JLabel("Seleccioná un paciente de la sala de espera");
        lblNombrePaciente.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelNombreRestr.add(lblNombrePaciente, BorderLayout.CENTER);

        // restricción visible al usuario
        String medico = sesion.getMedicoActual() != null
                ? sesion.getMedicoActual().getNombreCompleto()
                : sesion.getRolActual();
        lblRestriccionAcceso = new JLabel("Historial restringido a pacientes de " + medico);
        lblRestriccionAcceso.setFont(new Font("Tahoma", Font.PLAIN, 9));
        lblRestriccionAcceso.setForeground(new Color(100, 0, 0));
        panelNombreRestr.add(lblRestriccionAcceso, BorderLayout.SOUTH);
        panelInfoPaciente.add(panelNombreRestr, BorderLayout.CENTER);

        lblDatosPaciente = new JLabel(" ");
        lblDatosPaciente.setFont(new Font("Tahoma", Font.PLAIN, 10));
        lblDatosPaciente.setForeground(Color.GRAY);
        panelInfoPaciente.add(lblDatosPaciente, BorderLayout.SOUTH);
        panelHistoria.add(panelInfoPaciente, BorderLayout.NORTH);

        // Split: historial + formulario nota
        JSplitPane splitHistoria = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitHistoria.setDividerLocation(200);
        splitHistoria.setDividerSize(4);

        // JList historial
        JPanel panelListaHistorial = new JPanel(new BorderLayout());
        panelListaHistorial.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Historial clínico",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 10), new Color(0, 0, 128)
        ));
        modeloHistorial = new DefaultListModel<>();
        listaHistorial  = new JList<>(modeloHistorial);
        listaHistorial.setFont(new Font("Tahoma", Font.PLAIN, 10));
        listaHistorial.setFixedCellHeight(42);
        listaHistorial.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JPanel cell = new JPanel(new GridLayout(2, 1));
                cell.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                String[] p = value.toString().split("\\|");
                JLabel lFecha  = new JLabel(p.length > 0 ? p[0] : "");
                JLabel lDiag   = new JLabel(p.length > 1 ? p[1] : "");
                lFecha.setFont(new Font("Tahoma", Font.BOLD, 10));
                lDiag.setFont(new Font("Tahoma", Font.PLAIN, 10));
                cell.add(lFecha); cell.add(lDiag);
                Color bg = isSelected ? new Color(0, 0, 128) : Color.WHITE;
                cell.setBackground(bg);
                lFecha.setForeground(isSelected ? Color.WHITE              : Color.BLACK);
                lDiag.setForeground(isSelected  ? new Color(170,170,220) : Color.GRAY);
                return cell;
            }
        });
        panelListaHistorial.add(new JScrollPane(listaHistorial), BorderLayout.CENTER);
        splitHistoria.setLeftComponent(panelListaHistorial);

        // Formulario nueva nota
        JPanel panelNota = new JPanel(new BorderLayout(4, 4));
        panelNota.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Registrar nota clínica",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Tahoma", Font.BOLD, 10), new Color(0, 100, 0)
                ),
                new EmptyBorder(4, 6, 4, 6)
        ));

        JPanel panelTextAreas = new JPanel(new GridLayout(3, 1, 4, 6));

        JPanel pDiag = new JPanel(new BorderLayout(2, 2));
        pDiag.add(new JLabel("Diagnóstico:"), BorderLayout.NORTH);
        txtDiagnostico = new JTextArea(2, 1);
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        txtDiagnostico.setBackground(new Color(255, 253, 224));
        pDiag.add(new JScrollPane(txtDiagnostico), BorderLayout.CENTER);
        panelTextAreas.add(pDiag);

        JPanel pTrat = new JPanel(new BorderLayout(2, 2));
        pTrat.add(new JLabel("Tratamiento indicado:"), BorderLayout.NORTH);
        txtTratamiento = new JTextArea(2, 1);
        txtTratamiento.setLineWrap(true);
        txtTratamiento.setWrapStyleWord(true);
        txtTratamiento.setBackground(new Color(255, 253, 224));
        pTrat.add(new JScrollPane(txtTratamiento), BorderLayout.CENTER);
        panelTextAreas.add(pTrat);

        JPanel pObs = new JPanel(new BorderLayout(2, 2));
        pObs.add(new JLabel("Observaciones:"), BorderLayout.NORTH);
        txtObservaciones = new JTextArea(2, 1);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBackground(new Color(255, 253, 224));
        pObs.add(new JScrollPane(txtObservaciones), BorderLayout.CENTER);
        panelTextAreas.add(pObs);

        panelNota.add(panelTextAreas, BorderLayout.CENTER);

        JPanel panelBotonesNota = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnGuardarNota  = new JButton("Guardar nota");
        btnCancelarNota = new JButton("Cancelar");
        getRootPane().setDefaultButton(btnGuardarNota);
        panelBotonesNota.add(btnCancelarNota);
        panelBotonesNota.add(btnGuardarNota);
        panelNota.add(panelBotonesNota, BorderLayout.SOUTH);

        splitHistoria.setRightComponent(panelNota);
        panelHistoria.add(splitHistoria, BorderLayout.CENTER);
        panel.add(panelHistoria, BorderLayout.CENTER);

        // EVENTOS
        listaSala.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarPacienteSeleccionado();
        });
        btnFinalizar.addActionListener(e    -> accionFinalizar());
        btnGuardarNota.addActionListener(e  -> accionGuardarNota());
        btnCancelarNota.addActionListener(e -> limpiarFormularioNota());
        listaHistorial.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalleNota();
        });

        return panel;
    }

    // Carga sala de espera desde BD
    private void cargarSalaDeEspera() {
        modeloSala.clear();
        try {
            int idMedico = sesion.getMedicoActual() != null
                    ? sesion.getMedicoActual().getIdMedico() : 0;
            turnosEnEspera = gestorSala.obtenerPacientesEnEspera(idMedico);
            for (Turno t : turnosEnEspera) {
                modeloSala.addElement(
                        t.getHora() + "|" +
                                t.getPaciente().getNombreCompleto() + "|" +
                                (t.getMotivoConsulta() != null ? t.getMotivoConsulta() : "—")
                );
            }
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    // Carga historial del paciente seleccionado
    private void cargarPacienteSeleccionado() {
        int idx = listaSala.getSelectedIndex();
        if (idx == -1 || turnosEnEspera == null) return;

        Turno turno = turnosEnEspera.get(idx);
        lblNombrePaciente.setText(turno.getPaciente().getNombreCompleto());
        lblDatosPaciente.setText(
                "DNI: " + turno.getPaciente().getDni() +
                        "  ·  Tel.: " + (turno.getPaciente().getTelefono() != null
                        ? turno.getPaciente().getTelefono() : "—")
        );

        modeloHistorial.clear();
        historialNotas.clear();
        try {
            int idPaciente = turno.getPaciente().getIdPaciente();
            int idMedico   = sesion.getMedicoActual().getIdMedico();
            List<NotaClinica> historial =
                    gestorHistoria.consultarHistorial(idPaciente, idMedico);
            historialNotas.addAll(historial);
            for (NotaClinica n : historial) {
                String preview = n.getDiagnostico().length() > 40
                        ? n.getDiagnostico().substring(0, 40) + "..."
                        : n.getDiagnostico();
                modeloHistorial.addElement(n.getFecha() + "  |  " + preview);
            }
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void accionFinalizar() {
        if (listaSala.isSelectionEmpty() || turnosEnEspera == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un paciente de la sala de espera.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idx = listaSala.getSelectedIndex();
        int idTurno = turnosEnEspera.get(idx).getIdTurno();

        try {
            // Verifica que exista nota clínica antes de finalizar
            gestorHistoria.finalizarAtencion(idTurno);
            cargarSalaDeEspera();
            modeloHistorial.clear();
            lblNombrePaciente.setText("Seleccioná un paciente de la sala de espera");
            lblDatosPaciente.setText(" ");
            limpiarFormularioNota();
            lblEstado.setText(" Atencion finalizada. Turno actualizado a Atendido.");
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Nota clinica pendiente", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void accionGuardarNota() {
        if (listaSala.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay paciente seleccionado.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int idx    = listaSala.getSelectedIndex();
            int idTurno = turnosEnEspera.get(idx).getIdTurno();

            gestorHistoria.registrarNota(
                    idTurno,
                    txtDiagnostico.getText().trim(),
                    txtTratamiento.getText().trim(),
                    txtObservaciones.getText().trim()
            );
            cargarSalaDeEspera();
            limpiarFormularioNota();
            lblEstado.setText(" Nota clínica registrada. Turno actualizado a Atendido.");

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void limpiarFormularioNota() {
        txtDiagnostico.setText("");
        txtTratamiento.setText("");
        txtObservaciones.setText("");
    }

    // Panel Mi Agenda
    private DefaultTableModel modeloAgenda;

    private JPanel crearPanelAgenda() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Barra de fecha + botón buscar
        JPanel panelTop = new JPanel(new BorderLayout(6, 0));
        panelTop.add(new JLabel("Fecha (yyyy-MM-dd):"), BorderLayout.WEST);
        JTextField txtFechaAgenda = new JTextField(
                java.time.LocalDate.now().toString(), 12);
        panelTop.add(txtFechaAgenda, BorderLayout.CENTER);
        JButton btnVerAgenda = new JButton("Ver agenda");
        panelTop.add(btnVerAgenda, BorderLayout.EAST);
        panel.add(panelTop, BorderLayout.NORTH);

        // Tabla de turnos
        String[] cols = {"Hora", "Paciente", "Motivo", "Estado"};
        modeloAgenda = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaAgenda = new JTable(modeloAgenda);
        tablaAgenda.setRowHeight(20);
        tablaAgenda.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaAgenda.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaAgenda.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaAgenda.getColumnModel().getColumn(3).setPreferredWidth(90);
        panel.add(new JScrollPane(tablaAgenda), BorderLayout.CENTER);

        // Evento buscar
        btnVerAgenda.addActionListener(e -> {
            modeloAgenda.setRowCount(0);
            try {
                int idMedico = sesion.getMedicoActual().getIdMedico();
                String fecha = txtFechaAgenda.getText().trim();
                java.util.List<modelo.entidades.Turno> lista =
                        new GestorAgenda().obtenerAgendaPorFecha(idMedico, fecha);
                for (modelo.entidades.Turno t : lista) {
                    modeloAgenda.addRow(new Object[]{
                            t.getHora(),
                            t.getPaciente().getNombreCompleto(),
                            t.getMotivoConsulta() != null ? t.getMotivoConsulta() : "—",
                            t.getEstado()
                    });
                }
            } catch (java.sql.SQLException ex) {
                Main.mostrarErrorConexion(ex);
            }
        });

        return panel;
    }

    // Abrir detalle de nota clínica
    private void mostrarDetalleNota() {
        int idx = listaHistorial.getSelectedIndex();
        if (idx == -1 || idx >= historialNotas.size()) return;
        new PantallaDetalleNota(historialNotas.get(idx)).setVisible(true);
    }


}