package vista;

import modelo.entidades.NotaClinica;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PantallaDetalleNota extends JFrame {

    public PantallaDetalleNota(NotaClinica nota) {
        initComponents(nota);
        configurarVentana(nota);
    }

    private void configurarVentana(NotaClinica nota) {
        setTitle("Nota clínica – " + nota.getFecha());
        setSize(480, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents(NotaClinica nota) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));
        setContentPane(panel);

        // título
        JLabel lblTitulo = new JLabel("Nota clínica del " + nota.getFecha());
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(0, 0, 128));
        lblTitulo.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // campos
        JPanel panelCampos = new JPanel(new GridLayout(3, 1, 6, 10));
        panelCampos.setBorder(new EmptyBorder(10, 0, 10, 0));

        panelCampos.add(crearCampo("Diagnóstico:",
            nota.getDiagnostico()));
        panelCampos.add(crearCampo("Tratamiento:",
            nota.getTratamiento() != null && !nota.getTratamiento().isEmpty()
                ? nota.getTratamiento() : "—"));
        panelCampos.add(crearCampo("Observaciones:",
            nota.getObservaciones() != null && !nota.getObservaciones().isEmpty()
                ? nota.getObservaciones() : "—"));

        panel.add(panelCampos, BorderLayout.CENTER);

        // botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnCerrar);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.add(btnCerrar);
        panel.add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearCampo(String etiqueta, String valor) {
        JPanel p = new JPanel(new BorderLayout(4, 2));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
        p.add(lbl, BorderLayout.NORTH);

        JTextArea txt = new JTextArea(valor);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        txt.setBackground(new Color(245, 245, 245));
        txt.setFont(new Font("Tahoma", Font.PLAIN, 11));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(4, 6, 4, 6)
        ));
        p.add(new JScrollPane(txt), BorderLayout.CENTER);

        return p;
    }
}
