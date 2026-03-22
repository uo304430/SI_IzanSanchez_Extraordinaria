package RechazoIncidencias;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class RechazoView {
    private JFrame frame;
    private JTable tabla;
    private JButton btnCargar, btnRechazar;

    public RechazoView() {
        frame = new JFrame("Gestión de Rechazos - HU 33801");
        frame.setBounds(100, 100, 700, 450);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));

        btnCargar = new JButton("Ver mis tareas pendientes");
        frame.getContentPane().add(btnCargar, "split 2");

        btnRechazar = new JButton("Rechazar incidencia seleccionada");
        frame.getContentPane().add(btnRechazar, "wrap");

        tabla = new JTable();
        frame.getContentPane().add(new JScrollPane(tabla), "grow");
    }

    // Getters necesarios para el Main y el Controller
    public JFrame getFrame() { return frame; }
    public JTable getTabla() { return tabla; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnRechazar() { return btnRechazar; }
}