package vista;

import sigmed.Main;
import controlador.GestorSesion;
import controlador.GestorTurno;
import controlador.GestorPaciente;
import modelo.entidades.Turno;
import modelo.entidades.Paciente;
import modelo.entidades.Medico;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PantallaTurno extends JFrame {

    // Sesión y gestores
    private GestorSesion  sesion;
    private GestorTurno   gestorTurno;
    private GestorPaciente gestorPaciente;

    // Componentes formulario
    private JComboBox<String> cmbPaciente;
    private JComboBox<String> cmbMedico;
    private JTextField        txtFecha;
    private JComboBox<String> cmbHora;
    private JLabel            lblDisponibilidad;
    private JTextArea         txtMotivo;
    private JButton           btnConfirmar;
    private JButton           btnCancelar;
    private JButton           btnModificar;
    private JButton           btnEliminar;

    // Componentes tabla
    private JTable            tablaTurnos;
    private DefaultTableModel modeloTabla;
    private JTextField        txtBuscar;
    private JComboBox<String> cmbFiltroEstado;

    // Datos cargados
    private List<Paciente>              listaPacientes;
    private List<modelo.entidades.Medico> listaMedicos;
    private List<modelo.entidades.Turno>  turnosMostrados    = new ArrayList<>();
    private int                           idTurnoSeleccionado = -1;

    private JLabel                              lblEstado;
    private TableRowSorter<DefaultTableModel>   sorter;

    // Constructor
    public PantallaTurno(GestorSesion sesion) throws SQLException {
        this.sesion         = sesion;
        this.gestorTurno    = new GestorTurno();
        this.gestorPaciente = new GestorPaciente();
        initComponents();
        configurarVentana();
        cargarCombos();
        cargarTurnosDelDia();
    }

    private void configurarVentana() {
        setTitle("SIGMED – Gestión de Turnos | Secretaria");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.cerrarSesionYVolver();
            }
        });
        setSize(920, 540);
        setLocationRelativeTo(null);
    }

    private void initComponents() {

        // MENU BAR
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        Color colorActivo   = new Color(180, 190, 210);
        Color colorInactivo = menuBar.getBackground();
        Font  fuenteActiva  = new Font("Tahoma", Font.BOLD, 12);
        Font  fuenteNormal  = new Font("Tahoma", Font.PLAIN, 12);

        JMenuItem menuPacientes = new JMenuItem("Pacientes");
        JMenuItem menuTurnos    = new JMenuItem("Turnos");
        JMenuItem menuRecepcion = new JMenuItem("Recepción");
        JMenuItem[] navItems    = {menuPacientes, menuTurnos, menuRecepcion};

        Runnable resetNav = () -> {
            for (JMenuItem item : navItems) {
                item.setBackground(colorInactivo);
                item.setFont(fuenteNormal);
                item.setOpaque(false);
            }
        };

        menuPacientes.addActionListener(e -> {
            // Sombrear Pacientes mientras el diálogo esté abierto
            resetNav.run();
            menuPacientes.setBackground(colorActivo);
            menuPacientes.setFont(fuenteActiva);
            menuPacientes.setOpaque(true);
            try {
                new PantallaPaciente(this).setVisible(true);
                // Al cerrarse el diálogo (modal) → restaurar Turnos
                resetNav.run();
                menuTurnos.setBackground(colorActivo);
                menuTurnos.setFont(fuenteActiva);
                menuTurnos.setOpaque(true);
            } catch (SQLException ex) { Main.mostrarErrorConexion(ex); }
        });
        menuBar.add(menuPacientes);

        menuTurnos.addActionListener(e -> {
            resetNav.run();
            menuTurnos.setBackground(colorActivo);
            menuTurnos.setFont(fuenteActiva);
            menuTurnos.setOpaque(true);
        });
        menuBar.add(menuTurnos);

        menuRecepcion.addActionListener(e -> {
            // Sombrear Recepción mientras el diálogo esté abierto
            resetNav.run();
            menuRecepcion.setBackground(colorActivo);
            menuRecepcion.setFont(fuenteActiva);
            menuRecepcion.setOpaque(true);
            try {
                new PantallaRecepcion(this).setVisible(true);
                // Al cerrarse → refrescar tabla y restaurar Turnos
                cargarTurnosDelDia();
                resetNav.run();
                menuTurnos.setBackground(colorActivo);
                menuTurnos.setFont(fuenteActiva);
                menuTurnos.setOpaque(true);
            } catch (SQLException ex) { Main.mostrarErrorConexion(ex); }
        });
        menuBar.add(menuRecepcion);

        JMenuItem menuSalir = new JMenuItem("Salir");
        menuSalir.addActionListener(e -> Main.cerrarSesionYVolver());
        menuBar.add(menuSalir);

        // Turnos activo por defecto
        menuTurnos.setBackground(colorActivo);
        menuTurnos.setFont(fuenteActiva);
        menuTurnos.setOpaque(true);

        setJMenuBar(menuBar);

        // PANEL PRINCIPAL
        JPanel panelPrincipal = new JPanel(new BorderLayout(6, 4));
        panelPrincipal.setBorder(new EmptyBorder(6, 6, 0, 6));
        setContentPane(panelPrincipal);

        // JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(5);
        panelPrincipal.add(splitPane, BorderLayout.CENTER);

        // Formulario
        JPanel panelFormulario = new JPanel(new BorderLayout(4, 6));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblTituloForm = new JLabel("Nuevo / Modificar Turno");
        lblTituloForm.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTituloForm.setForeground(new Color(0, 0, 128));
        lblTituloForm.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panelFormulario.add(lblTituloForm, BorderLayout.NORTH);

        JPanel panelCampos = new JPanel(new GridLayout(8, 1, 4, 4));
        panelCampos.setBorder(new EmptyBorder(6, 0, 6, 0));

        panelCampos.add(new JLabel("Paciente:"));
        cmbPaciente = new JComboBox<>();
        panelCampos.add(cmbPaciente);

        panelCampos.add(new JLabel("Médico asignado:"));
        cmbMedico = new JComboBox<>();
        panelCampos.add(cmbMedico);

        panelCampos.add(new JLabel("Fecha:"));
        txtFecha = new JTextField();
        agregarPlaceholder(txtFecha, "yyyy-MM-dd");
        txtFecha.setText(LocalDate.now().toString());
        txtFecha.setForeground(Color.BLACK);
        panelCampos.add(txtFecha);

        panelCampos.add(new JLabel("Hora disponible:"));
        cmbHora = new JComboBox<>();
        panelCampos.add(cmbHora);

        panelFormulario.add(panelCampos, BorderLayout.CENTER);

        JPanel panelSurForm = new JPanel(new BorderLayout(4, 6));

        lblDisponibilidad = new JLabel(" ");
        lblDisponibilidad.setFont(new Font("Tahoma", Font.BOLD, 10));
        panelSurForm.add(lblDisponibilidad, BorderLayout.NORTH);

        JPanel panelMotivo = new JPanel(new BorderLayout(2, 2));
        panelMotivo.add(new JLabel("Motivo de consulta:"), BorderLayout.NORTH);
        txtMotivo = new JTextArea(3, 1);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        panelMotivo.add(new JScrollPane(txtMotivo), BorderLayout.CENTER);
        panelSurForm.add(panelMotivo, BorderLayout.CENTER);

        JPanel panelBotonesForm = new JPanel(new GridLayout(2, 2, 6, 6));
        panelBotonesForm.setBorder(new EmptyBorder(6, 0, 0, 0));
        btnConfirmar = new JButton("Confirmar");
        btnCancelar  = new JButton("Cancelar");
        btnModificar = new JButton("Modificar");
        btnEliminar  = new JButton("Eliminar");
        getRootPane().setDefaultButton(btnConfirmar);
        panelBotonesForm.add(btnConfirmar);
        panelBotonesForm.add(btnCancelar);
        panelBotonesForm.add(btnModificar);
        panelBotonesForm.add(btnEliminar);
        panelSurForm.add(panelBotonesForm, BorderLayout.SOUTH);

        panelFormulario.add(panelSurForm, BorderLayout.SOUTH);
        splitPane.setLeftComponent(panelFormulario);

        // JTable
        JPanel panelTabla = new JPanel(new BorderLayout(4, 6));
        panelTabla.setBorder(new EmptyBorder(0, 6, 0, 0));

        JLabel lblTituloTabla = new JLabel("Turnos del día – " + LocalDate.now());
        lblTituloTabla.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTituloTabla.setForeground(new Color(0, 0, 128));
        lblTituloTabla.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panelTabla.add(lblTituloTabla, BorderLayout.NORTH);

        // Panel de filtros: fecha, búsqueda y estado en un solo bloque
        JPanel panelFiltros = new JPanel(new java.awt.GridLayout(2, 1, 0, 3));
        panelFiltros.setBorder(new EmptyBorder(4, 0, 4, 0));

        // Fecha + botón + Estado
        JPanel panelFila1 = new JPanel(new BorderLayout(6, 0));
        JPanel panelFechaBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelFechaBtn.add(new JLabel("Fecha:"));
        JTextField txtFechaTabla = new JTextField(12);
        agregarPlaceholder(txtFechaTabla, "yyyy-MM-dd");
        txtFechaTabla.setText(LocalDate.now().toString());
        txtFechaTabla.setForeground(Color.BLACK);
        panelFechaBtn.add(txtFechaTabla);
        JButton btnVerFecha = new JButton("Ver");
        panelFechaBtn.add(btnVerFecha);
        panelFila1.add(panelFechaBtn, BorderLayout.WEST);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        panelFiltro.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos","Pendiente","Presente","Ausente","Atendido"});
        cmbFiltroEstado.setPreferredSize(new Dimension(110, 24));
        panelFiltro.add(cmbFiltroEstado);
        panelFila1.add(panelFiltro, BorderLayout.EAST);
        panelFiltros.add(panelFila1);

        // Buscar
        JPanel panelBusqueda = new JPanel(new BorderLayout(4, 0));
        JPanel panelBusqIzq = new JPanel(new BorderLayout(4, 0));
        panelBusqIzq.add(new JLabel("Buscar:"), BorderLayout.WEST);
        txtBuscar = new JTextField();
        panelBusqIzq.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(panelBusqIzq, BorderLayout.CENTER);
        panelFiltros.add(panelBusqueda);

        panelTabla.add(panelFiltros, BorderLayout.NORTH);
        JPanel panelBusquedaDummy = panelBusqueda; // referencia dummy para compatibilidad

        String[] columnas = {"Fecha", "Hora", "Paciente", "Médico asignado", "Motivo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaTurnos = new JTable(modeloTabla);
        tablaTurnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTurnos.getTableHeader().setReorderingAllowed(false);
        tablaTurnos.setRowHeight(20);
        tablaTurnos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaTurnos.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaTurnos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaTurnos.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaTurnos.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaTurnos.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Renderer de estados
        tablaTurnos.getColumnModel().getColumn(5).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object v,
                                                                   boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                        if (!sel) {
                            switch (v == null ? "" : v.toString()) {
                                case "Pendiente": setForeground(new Color(139, 69,  19)); break;
                                case "Presente":  setForeground(new Color(0,  100,   0)); break;
                                case "Ausente":   setForeground(new Color(180,  0,   0)); break;
                                case "Atendido":  setForeground(new Color(0,    0, 128)); break;
                                default:          setForeground(Color.BLACK);
                            }
                        }
                        return this;
                    }
                }
        );
        sorter = new TableRowSorter<>(modeloTabla);
        tablaTurnos.setRowSorter(sorter);
        panelTabla.add(new JScrollPane(tablaTurnos), BorderLayout.CENTER);
        splitPane.setRightComponent(panelTabla);

        // BARRA DE ESTADO
        JPanel panelEstado = new JPanel(new BorderLayout());
        panelEstado.setBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY));
        lblEstado = new JLabel(
                " Gestión de Turnos | Secretaria");
        lblEstado.setFont(new Font("Tahoma", Font.PLAIN, 9));
        lblEstado.setForeground(Color.GRAY);
        panelEstado.add(lblEstado, BorderLayout.WEST);
        panelPrincipal.add(panelEstado, BorderLayout.SOUTH);

        // EVENTOS
        btnConfirmar.addActionListener(e -> accionConfirmar());
        btnModificar.addActionListener(e -> accionModificar());
        btnCancelar.addActionListener(e  -> limpiarFormulario());
        btnEliminar.addActionListener(e  -> accionEliminar());

        tablaTurnos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarTurnoSeleccionado();
        });
        // Al cambiar fecha o médico, actualizar horas disponibles
        txtFecha.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { actualizarHorasDisponibles(); }
        });
        cmbMedico.addActionListener(e -> actualizarHorasDisponibles());
        cmbFiltroEstado.addActionListener(e -> aplicarFiltro());
        btnVerFecha.addActionListener(e -> cargarTurnosPorFecha(txtFechaTabla.getText().trim()));
        txtFechaTabla.addActionListener(e -> cargarTurnosPorFecha(txtFechaTabla.getText().trim()));
        txtFechaTabla.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (!txtFechaTabla.getText().trim().isEmpty() &&
                        !txtFechaTabla.getText().equals("yyyy-MM-dd"))
                    cargarTurnosPorFecha(txtFechaTabla.getText().trim());
            }
        });
        txtBuscar.addActionListener(e -> aplicarFiltro());
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
        });
    }

    // Carga combos desde la BD
    private void cargarCombos() {
        try {
            listaPacientes = gestorPaciente.listarTodos();
            for (Paciente p : listaPacientes)
                cmbPaciente.addItem(p.getNombreCompleto());

            modelo.dao.MedicoDAO medicoDAO = new modelo.dao.MedicoDAO();
            listaMedicos = medicoDAO.listarTodos();
            for (Medico m : listaMedicos)
                cmbMedico.addItem(m.toString());

        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    // Carga turnos del día desde la BD
    private void cargarTurnosDelDia() {
        cargarTurnosPorFecha(LocalDate.now().toString());
    }

    private void accionConfirmar() {
        try {
            int idxPaciente = cmbPaciente.getSelectedIndex();
            int idxMedico   = cmbMedico.getSelectedIndex();
            String fecha    = txtFecha.getText().trim();
            String hora     = cmbHora.getSelectedItem() != null
                    ? (String) cmbHora.getSelectedItem() : "";
            String motivo   = txtMotivo.getText().trim();

            if (fecha.isEmpty() || cmbHora.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "Seleccioná una fecha y un horario disponible.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idPaciente = listaPacientes.get(idxPaciente).getIdPaciente();
            int idMedico   = listaMedicos.get(idxMedico).getIdMedico();

            // Verificación final de disponibilidad antes de confirmar
            boolean disponible = gestorTurno.verificarDisponibilidad(idMedico, fecha, hora + ":00");
            if (!disponible) {
                lblDisponibilidad.setText("El horario " + hora + " ya se encuentra ocupado. Elegí otro.");
                lblDisponibilidad.setForeground(Color.RED);
                actualizarHorasDisponibles(); // refresca el combo
                return;
            }
            gestorTurno.registrarTurno(idPaciente, idMedico, fecha, hora, motivo);
            cargarTurnosDelDia();
            limpiarFormulario();
            lblEstado.setText(" Turno registrado correctamente.");

        } catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void accionModificar() {
        if (idTurnoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un turno de la tabla para modificar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idxPaciente = cmbPaciente.getSelectedIndex();
            int idxMedico   = cmbMedico.getSelectedIndex();
            String nuevaFecha = txtFecha.getText().trim();
            String nuevaHora  = cmbHora.getSelectedItem() != null
                    ? (String) cmbHora.getSelectedItem() : "";

            if (nuevaFecha.isEmpty() || nuevaFecha.equals("yyyy-MM-dd") || nuevaHora.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Completá fecha y horario antes de modificar.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener el turno actual y actualizar sus campos
            modelo.entidades.Turno turno = gestorTurno.buscarTurno(idTurnoSeleccionado);
            if (turno == null) {
                JOptionPane.showMessageDialog(this, "Turno no encontrado.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            turno.setPaciente(listaPacientes.get(idxPaciente));
            turno.setMedico(listaMedicos.get(idxMedico));
            turno.setMotivoConsulta(txtMotivo.getText().trim());

            // modificarTurno valida disponibilidad si cambió fecha u hora
            gestorTurno.modificarTurno(turno, nuevaFecha, nuevaHora);

            cargarTurnosPorFecha(nuevaFecha);
            limpiarFormulario();
            lblEstado.setText(" Turno modificado correctamente.");

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Horario no disponible", JOptionPane.WARNING_MESSAGE);
            actualizarHorasDisponibles();
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void accionEliminar() {
        if (idTurnoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un turno de la tabla para cancelar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "El turno pasará al estado Ausente y el horario quedará libre. ¿Confirma?",
                "Cancelar turno", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                gestorTurno.cancelarTurno(idTurnoSeleccionado);
                String fecha = txtFecha.getText().trim();
                cargarTurnosPorFecha(fecha.isEmpty() ? LocalDate.now().toString() : fecha);
                limpiarFormulario();
                lblEstado.setText(" Turno cancelado. Estado actualizado a Ausente.");
            } catch (SQLException e) {
                Main.mostrarErrorConexion(e);
            }
        }
    }

    private void cargarTurnoSeleccionado() {
        int fila = tablaTurnos.getSelectedRow();
        if (fila == -1 || turnosMostrados.isEmpty()) return;

        // Convertir índice de vista a modelo (por el sorter)
        int filaModelo = tablaTurnos.convertRowIndexToModel(fila);
        if (filaModelo >= turnosMostrados.size()) return;

        modelo.entidades.Turno t = turnosMostrados.get(filaModelo);
        idTurnoSeleccionado = t.getIdTurno();

        // Cargar fecha
        txtFecha.setText(t.getFecha());
        txtFecha.setForeground(java.awt.Color.BLACK);

        // Seleccionar médico en combo
        if (listaMedicos != null) {
            for (int i = 0; i < listaMedicos.size(); i++) {
                if (listaMedicos.get(i).getIdMedico() == t.getMedico().getIdMedico()) {
                    cmbMedico.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Seleccionar paciente en combo
        if (listaPacientes != null) {
            for (int i = 0; i < listaPacientes.size(); i++) {
                if (listaPacientes.get(i).getIdPaciente() == t.getPaciente().getIdPaciente()) {
                    cmbPaciente.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Actualizar horas y agregar la hora actual del turno si no está
        actualizarHorasDisponibles();
        String horaActual = t.getHora().length() > 5
                ? t.getHora().substring(0, 5) : t.getHora();
        boolean encontrada = false;
        for (int i = 0; i < cmbHora.getItemCount(); i++) {
            if (cmbHora.getItemAt(i).equals(horaActual)) {
                cmbHora.setSelectedIndex(i);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            // La hora actual pertenece a este turno — agregarla al combo
            cmbHora.addItem(horaActual);
            cmbHora.setSelectedItem(horaActual);
        }

        // Cargar motivo
        txtMotivo.setText(t.getMotivoConsulta() != null ? t.getMotivoConsulta() : "");
        lblEstado.setText(" Turno seleccionado. Modifica los campos y presioná Modificar.");
    }

    private void limpiarFormulario() {
        if (cmbPaciente.getItemCount() > 0) cmbPaciente.setSelectedIndex(0);
        if (cmbMedico.getItemCount()   > 0) cmbMedico.setSelectedIndex(0);
        txtFecha.setText(LocalDate.now().toString());
        cmbHora.removeAllItems();
        txtMotivo.setText("");
        lblDisponibilidad.setText(" ");
    }

    // Filtrar tabla por estado y/o búsqueda
    private void aplicarFiltro() {
        String estado   = (String) cmbFiltroEstado.getSelectedItem();
        String busqueda = txtBuscar.getText().trim();

        List<RowFilter<Object, Object>> filtros = new ArrayList<>();

        if (estado != null && !"Todos".equals(estado)) {
            filtros.add(RowFilter.regexFilter("(?i)" + estado, 5)); // col 5 = Estado
        }
        if (!busqueda.isEmpty()) {
            // busca en Paciente (col 2) y Médico (col 3)
            filtros.add(RowFilter.regexFilter("(?i)" + busqueda, 2, 3));
        }

        if (filtros.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filtros.size() == 1) {
            sorter.setRowFilter(filtros.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filtros));
        }
    }

    // Actualizar horas disponibles según médico y fecha
    private void actualizarHorasDisponibles() {
        cmbHora.removeAllItems();
        lblDisponibilidad.setText(" ");

        int idxMedico = cmbMedico.getSelectedIndex();
        String fecha  = txtFecha.getText().trim();

        if (idxMedico < 0 || listaMedicos == null || fecha.isEmpty()) return;

        // Validar que la fecha no esté vacía
        if (fecha.isEmpty()) {
            lblDisponibilidad.setText(" ");
            return;
        }

        try {
            int idMedico = listaMedicos.get(idxMedico).getIdMedico();
            java.util.List<String> slots =
                    gestorTurno.obtenerHorasDisponibles(idMedico, fecha);

            if (slots.isEmpty()) {
                lblDisponibilidad.setText("Sin horarios disponibles para esa fecha.");
                lblDisponibilidad.setForeground(Color.RED);
            } else {
                for (String s : slots) cmbHora.addItem(s);
                String msg = slots.size() == 1
                        ? "1 horario disponible."
                        : slots.size() + " horarios disponibles.";
                lblDisponibilidad.setText(msg);
                lblDisponibilidad.setForeground(new Color(0, 100, 0));
            }
        } catch (java.sql.SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    // Cargar turnos de una fecha específica
    private void cargarTurnosPorFecha(String fecha) {
        if (fecha.isEmpty() || fecha.equals("yyyy-MM-dd")) return;
        modeloTabla.setRowCount(0);
        turnosMostrados.clear();
        idTurnoSeleccionado = -1;
        try {
            turnosMostrados = gestorTurno.obtenerTurnosDelDia(fecha);
            for (modelo.entidades.Turno t : turnosMostrados) {
                modeloTabla.addRow(new Object[]{
                        t.getFecha(), t.getHora(),
                        t.getPaciente().getNombreCompleto(),
                        t.getMedico().getNombreCompleto(),
                        t.getMotivoConsulta() != null ? t.getMotivoConsulta() : "—",
                        t.getEstado()
                });
            }
        } catch (java.sql.SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    //Agrega comportamiento de placeholder a un JTextField.
    //Muestra el texto en gris cuando el campo está vacío.
    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setForeground(Color.GRAY);
        campo.setText(placeholder);
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setForeground(Color.GRAY);
                    campo.setText(placeholder);
                }
            }
        });
    }

}