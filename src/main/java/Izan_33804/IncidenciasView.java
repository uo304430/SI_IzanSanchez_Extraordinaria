package Izan_33804;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class IncidenciasView {
    private JFrame frame;
    private JTable tabIncidencias;
    private JButton btnCargar;
    private JButton btnPlanificar;
    private JTextField txtId, txtHoras, txtTrabajos;

    public IncidenciasView() {
        frame = new JFrame("Izan - Planificar Incidencia");
        frame.setBounds(100, 100, 500, 450);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][][][][][]"));

        tabIncidencias = new JTable();
        frame.getContentPane().add(new JScrollPane(tabIncidencias), "cell 0 1,grow");

        btnCargar = new JButton("Actualizar");
        frame.getContentPane().add(btnCargar, "cell 0 2");

        txtId = new JTextField(); txtId.setEditable(false);
        frame.getContentPane().add(new JLabel("ID:"), "cell 0 3,split 2");
        frame.getContentPane().add(txtId, "growx");

        txtHoras = new JTextField();
        frame.getContentPane().add(new JLabel("Horas:"), "cell 0 4,split 2");
        frame.getContentPane().add(txtHoras, "growx");

        txtTrabajos = new JTextField();
        frame.getContentPane().add(new JLabel("Trabajos:"), "cell 0 5");
        frame.getContentPane().add(txtTrabajos, "cell 0 6,growx");

        btnPlanificar = new JButton("Confirmar Planificación");
        frame.getContentPane().add(btnPlanificar, "cell 0 7,align center");
    }

    public JFrame getFrame() { return frame; }
    public JTable getTablaIncidencias() { return tabIncidencias; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnPlanificar() { return btnPlanificar; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtHoras() { return txtHoras; }
    public JTextField getTxtTrabajos() { return txtTrabajos; }
}
