package vista;

import sigmed.Main;
import controlador.GestorPaciente;
import modelo.entidades.Paciente;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PantallaPaciente extends JDialog {

    private GestorPaciente   gestorPaciente;
    private List<Paciente>   listaPacientes;

    private JTextField        txtBuscar;
    private JTable            tablaPacientes;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtFechaNac;
    private JTextField txtObraSocial;

    private boolean  modoNuevo = false;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnCancelar;
    private JLabel  lblMensaje;

    public PantallaPaciente(JFrame parent) throws SQLException {
        super(parent, "Gestión de Pacientes", true);
        this.gestorPaciente = new GestorPaciente();
        initComponents();
        configurarDialogo();
        cargarPacientes();
    }

    private void configurarDialogo() {
        setSize(940, 580);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void initComponents() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(6, 6));
        panelPrincipal.setBorder(new EmptyBorder(8, 8, 8, 8));
        setContentPane(panelPrincipal);

        // JTable de pacientes
        JPanel panelTabla = new JPanel(new BorderLayout(4, 6));

        JLabel lblTitulo = new JLabel("Listado de pacientes");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTitulo.setForeground(new Color(0, 0, 128));
        lblTitulo.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panelTabla.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelBusqueda = new JPanel(new BorderLayout(6, 0));
        panelBusqueda.setBorder(new EmptyBorder(4, 0, 4, 0));
        panelBusqueda.add(new JLabel("Buscar:"), BorderLayout.WEST);
        txtBuscar = new JTextField();
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        JButton btnBuscar = new JButton("Buscar");
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);
        panelTabla.add(panelBusqueda, BorderLayout.NORTH);

        String[] columnas = {"ID", "Apellido", "Nombre", "DNI", "Teléfono", "Obra Social"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPacientes.setRowHeight(20);
        tablaPacientes.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaPacientes.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaPacientes.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaPacientes.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaPacientes.getColumnModel().getColumn(4).setPreferredWidth(90);
        tablaPacientes.getColumnModel().getColumn(5).setPreferredWidth(100);
        panelTabla.add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);

        // Formulario
        JPanel panelForm = new JPanel(new BorderLayout(4, 6));
        panelForm.setPreferredSize(new Dimension(320, 0));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblTituloForm = new JLabel("Datos del paciente");
        lblTituloForm.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTituloForm.setForeground(new Color(0, 0, 128));
        lblTituloForm.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panelForm.add(lblTituloForm, BorderLayout.NORTH);

        JPanel panelCampos = new JPanel(new GridLayout(7, 2, 6, 8));
        panelCampos.setBorder(new EmptyBorder(8, 0, 8, 0));

        panelCampos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        panelCampos.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelCampos.add(txtApellido);

        panelCampos.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelCampos.add(txtDni);

        panelCampos.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panelCampos.add(txtTelefono);

        panelCampos.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelCampos.add(txtEmail);

        panelCampos.add(new JLabel("Fecha nac. (yyyy-MM-dd):"));
        txtFechaNac = new JTextField();
        panelCampos.add(txtFechaNac);

        panelCampos.add(new JLabel("Obra social:"));
        txtObraSocial = new JTextField();
        panelCampos.add(txtObraSocial);

        panelForm.add(panelCampos, BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout(4, 6));
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblMensaje.setPreferredSize(new Dimension(300, 36));
        lblMensaje.setVerticalAlignment(SwingConstants.TOP);
        panelSur.add(lblMensaje, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 6, 6));
        btnNuevo    = new JButton("Nuevo");
        btnGuardar  = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Limpiar");
        getRootPane().setDefaultButton(btnGuardar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        panelSur.add(panelBotones, BorderLayout.CENTER);
        panelForm.add(panelSur, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTabla, panelForm);
        split.setDividerLocation(560);
        split.setDividerSize(5);
        panelPrincipal.add(split, BorderLayout.CENTER);

        // EVENTOS
        btnNuevo.addActionListener(e -> {
            limpiarFormulario();
            modoNuevo = true;
            lblMensaje.setForeground(new Color(0, 0, 128));
            lblMensaje.setText("Ingresá los datos del nuevo paciente y presioná Guardar.");
        });
        btnGuardar.addActionListener(e -> accionGuardar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnCancelar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> accionBuscar());
        tablaPacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarPacienteSeleccionado();
        });
    }

    private void cargarPacientes() {
        modeloTabla.setRowCount(0);
        try {
            listaPacientes = gestorPaciente.listarTodos();
            for (Paciente p : listaPacientes) {
                modeloTabla.addRow(new Object[]{
                        p.getIdPaciente(),
                        p.getApellido(),
                        p.getNombre(),
                        p.getDni(),
                        p.getTelefono(),
                        p.getObraSocial()
                });
            }
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void accionBuscar() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) { cargarPacientes(); return; }
        modeloTabla.setRowCount(0);
        try {
            listaPacientes = gestorPaciente.buscarPaciente(criterio);
            for (Paciente p : listaPacientes) {
                modeloTabla.addRow(new Object[]{
                        p.getIdPaciente(), p.getApellido(), p.getNombre(),
                        p.getDni(), p.getTelefono(), p.getObraSocial()
                });
            }
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void cargarPacienteSeleccionado() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila == -1 || listaPacientes == null) return;
        modoNuevo = false;
        lblMensaje.setText(" ");  // limpiar mensaje al seleccionar otro paciente
        Paciente p = listaPacientes.get(fila);
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        txtDni.setText(p.getDni());
        txtTelefono.setText(p.getTelefono() != null ? p.getTelefono() : "");
        txtEmail.setText(p.getEmail() != null ? p.getEmail() : "");
        txtFechaNac.setText(p.getFechaNacimiento() != null ? p.getFechaNacimiento() : "");
        txtObraSocial.setText(p.getObraSocial() != null ? p.getObraSocial() : "");
    }

    private void accionGuardar() {
        int fila = tablaPacientes.getSelectedRow();

        // Validar que haya un paciente seleccionado o que se haya presionado Nuevo
        if (fila == -1 && !modoNuevo) {
            mostrarError("Seleccioná un paciente de la tabla o presioná Nuevo para registrar uno.");
            return;
        }

        // Validar campos obligatorios
        String nombre    = txtNombre.getText().trim();
        String apellido  = txtApellido.getText().trim();
        String dni       = txtDni.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
            mostrarError("Nombre, apellido y DNI son obligatorios.");
            return;
        }

        try {
            if (modoNuevo) {
                // Alta nueva — verificar DNI duplicado
                Paciente existente = gestorPaciente.buscarPorDni(dni);
                if (existente != null) {
                    mostrarError("Ya existe un paciente registrado con el DNI " + dni + ".");
                    return;
                }
                gestorPaciente.registrarPaciente(
                        nombre, apellido, dni,
                        txtTelefono.getText().trim(),
                        txtFechaNac.getText().trim(),
                        txtEmail.getText().trim(),
                        txtObraSocial.getText().trim()
                );
                mostrarOk("Paciente registrado correctamente.");

            } else {
                // Modificación — verificar que el DNI no pertenezca a otro paciente
                Paciente p = listaPacientes.get(fila);
                Paciente existente = gestorPaciente.buscarPorDni(dni);
                if (existente != null && existente.getIdPaciente() != p.getIdPaciente()) {
                    mostrarError("El DNI " + dni + " ya pertenece a otro paciente registrado.");
                    return;
                }
                p.setNombre(nombre);
                p.setApellido(apellido);
                p.setDni(dni);
                p.setTelefono(txtTelefono.getText().trim());
                p.setEmail(txtEmail.getText().trim());
                p.setFechaNacimiento(txtFechaNac.getText().trim());
                p.setObraSocial(txtObraSocial.getText().trim());
                gestorPaciente.modificarPaciente(p);
                mostrarOk("Paciente modificado correctamente.");
            }
            cargarPacientes();
            limpiarFormulario();

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void accionEliminar() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila == -1 || modoNuevo) {
            mostrarError("Seleccioná un paciente de la tabla para eliminar.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "¿Confirma la eliminación del paciente?", "Eliminar",
                JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                gestorPaciente.eliminarPaciente(listaPacientes.get(fila).getIdPaciente());
                cargarPacientes();
                limpiarFormulario();
                mostrarOk("Paciente eliminado.");
            } catch (SQLException e) {
                Main.mostrarErrorConexion(e);
            }
        }
    }

    private void limpiarFormulario() {
        modoNuevo = false;
        tablaPacientes.clearSelection();
        txtNombre.setText(""); txtApellido.setText(""); txtDni.setText("");
        txtTelefono.setText(""); txtEmail.setText(""); txtFechaNac.setText("");
        txtObraSocial.setText(""); lblMensaje.setText(" ");
    }

    private void mostrarOk(String msg) {
        lblMensaje.setForeground(new Color(0, 100, 0));
        lblMensaje.setText(msg);
    }
    private void mostrarError(String msg) {
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setText(msg);
    }
}