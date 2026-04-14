package RechazoIncidencias;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class RechazoView {
    private JFrame frame;
    private JTable tabla;
    private JButton btnCargar, btnRechazar;

    // New components for in-line rejection reason
    private JLabel lblMotivo;
    private JTextArea txtMotivo;

    public RechazoView() {
        frame = new JFrame("Gestión de Rechazos - HU 33801");
        frame.setBounds(100, 100, 700, 450);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][]"));

        btnCargar = new JButton("Ver mis tareas pendientes");
        frame.getContentPane().add(btnCargar, "split 2");

        btnRechazar = new JButton("Rechazar incidencia seleccionada");
        frame.getContentPane().add(btnRechazar, "wrap");

        tabla = new JTable();
        frame.getContentPane().add(new JScrollPane(tabla), "grow, span 1 1");

        // Inline motivo label + multi-line text area
        lblMotivo = new JLabel("Motivo del rechazo (mínimo 10 caracteres):");
        frame.getContentPane().add(lblMotivo, "cell 0 2");

        txtMotivo = new JTextArea(4, 50);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane spMotivo = new JScrollPane(txtMotivo);
        frame.getContentPane().add(spMotivo, "growx, wrap");
    }

    // Getters necesarios para el Main y el Controller
    public JFrame getFrame() { return frame; }
    public JTable getTabla() { return tabla; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnRechazar() { return btnRechazar; }

    // New getter for the inline motivo
    public String getMotivoText() { return txtMotivo.getText(); }

    // Optionally allow controller to clear motivo field after successful rejection
    public void clearMotivo() { txtMotivo.setText(""); }
}