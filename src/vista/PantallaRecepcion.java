package vista;

import sigmed.Main;
import controlador.GestorRecepcion;
import modelo.entidades.Turno;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PantallaRecepcion extends JDialog {

    private GestorRecepcion gestorRecepcion;
    private List<Turno>     turnosDelDia;

    private JTable            tablaTurnos;
    private DefaultTableModel modeloTabla;
    private JButton           btnRecepcionar;
    private JButton           btnRefrescar;
    private JLabel            lblEstado;

    public PantallaRecepcion(JFrame parent) throws SQLException {
        super(parent, "Recepción de Pacientes", true);
        this.gestorRecepcion = new GestorRecepcion();
        initComponents();
        configurarDialogo();
        cargarTurnosDelDia();
    }

    private void configurarDialogo() {
        setSize(720, 440);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void initComponents() {

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        setContentPane(panel);

        // TÍTULO
        JLabel lblTitulo = new JLabel(
                "Turnos del día – " + LocalDate.now());
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblTitulo.setForeground(new Color(0, 0, 128));
        lblTitulo.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // TABLA
        String[] columnas = {"Hora", "Paciente", "Médico", "Motivo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaTurnos = new JTable(modeloTabla);
        tablaTurnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTurnos.setRowHeight(22);
        tablaTurnos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaTurnos.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaTurnos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaTurnos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaTurnos.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Renderer de estados
        tablaTurnos.getColumnModel().getColumn(4).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object v,
                                                                   boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                        if (!sel) {
                            switch (v == null ? "" : v.toString()) {
                                case "Pendiente": setForeground(new Color(139, 69, 19)); break;
                                case "Presente":  setForeground(new Color(0, 100, 0));   break;
                                case "Ausente":   setForeground(new Color(180, 0, 0));   break;
                                case "Atendido":  setForeground(new Color(0, 0, 128));   break;
                                default:          setForeground(Color.BLACK);
                            }
                        }
                        return this;
                    }
                }
        );
        panel.add(new JScrollPane(tablaTurnos), BorderLayout.CENTER);

        // botones
        JPanel panelSur = new JPanel(new BorderLayout(6, 4));
        panelSur.setBorder(new EmptyBorder(6, 0, 0, 0));

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Tahoma", Font.PLAIN, 10));
        panelSur.add(lblEstado, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRefrescar   = new JButton("Refrescar");
        btnRecepcionar = new JButton("[OK] Marcar Presente");
        btnRecepcionar.setFont(new Font("Tahoma", Font.BOLD, 11));
        getRootPane().setDefaultButton(btnRecepcionar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnRecepcionar);
        panelSur.add(panelBotones, BorderLayout.EAST);
        panel.add(panelSur, BorderLayout.SOUTH);

        // EVENTOS
        btnRecepcionar.addActionListener(e -> accionRecepcionar());
        btnRefrescar.addActionListener(e   -> cargarTurnosDelDia());
    }

    private void cargarTurnosDelDia() {
        modeloTabla.setRowCount(0);
        try {
            turnosDelDia = gestorRecepcion.obtenerTurnosDelDia();
            for (Turno t : turnosDelDia) {
                modeloTabla.addRow(new Object[]{
                        t.getHora(),
                        t.getPaciente().getNombreCompleto(),
                        t.getMedico().getNombreCompleto(),
                        t.getMotivoConsulta() != null ? t.getMotivoConsulta() : "—",
                        t.getEstado()
                });
            }
            lblEstado.setText(" " + turnosDelDia.size() + " turno(s) registrados hoy.");
            lblEstado.setForeground(Color.GRAY);
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }

    private void accionRecepcionar() {
        int fila = tablaTurnos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un turno para recepcionar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Turno t = turnosDelDia.get(fila);
            gestorRecepcion.recepcionarPaciente(t.getIdTurno());
            cargarTurnosDelDia();
            lblEstado.setForeground(new Color(0, 100, 0));
            lblEstado.setText(" Paciente recepcionado. Estado actualizado a Presente.");
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            Main.mostrarErrorConexion(e);
        }
    }
}