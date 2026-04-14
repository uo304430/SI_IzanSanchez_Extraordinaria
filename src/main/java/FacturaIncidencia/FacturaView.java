package FacturaIncidencia;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class FacturaView {
    private JFrame frame;
    private JTable tabla;
    private JButton btnGenerar;
    private JButton btnVer;
    private JTextArea areaFactura;

    public FacturaView() {
        frame = new JFrame("Gestor de Facturas - Incidencias");
        frame.setBounds(100, 100, 800, 600);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][]"));

        tabla = new JTable();
        frame.getContentPane().add(new JScrollPane(tabla), "grow, wrap");

        btnGenerar = new JButton("Generar factura");
        btnVer = new JButton("Ver factura");
        JPanel p = new JPanel();
        p.add(btnGenerar);
        p.add(btnVer);
        frame.getContentPane().add(p, "wrap");

        areaFactura = new JTextArea(10, 60);
        areaFactura.setEditable(false);
        frame.getContentPane().add(new JScrollPane(areaFactura), "grow");
    }

    public JFrame getFrame() { return frame; }
    public JTable getTabla() { return tabla; }
    public JButton getBtnGenerar() { return btnGenerar; }
    public JButton getBtnVer() { return btnVer; }
    public JTextArea getAreaFactura() { return areaFactura; }
}