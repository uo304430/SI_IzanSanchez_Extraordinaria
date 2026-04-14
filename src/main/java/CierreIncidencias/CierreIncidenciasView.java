package CierreIncidencias;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import javax.swing.table.DefaultTableModel;

public class CierreIncidenciasView {
    private JFrame frame;
    private JTable tabla;
    private JButton btnCargar;
    private JButton btnCerrar;

    // Campos para mostrar detalle de presupuesto
    private JTextField txtCoste;
    private JTextField txtPresupuesto;
    private JTextField txtConsumido;
    private JTextField txtDisponible;

    public CierreIncidenciasView() {
        frame = new JFrame("Cierre de Incidencias");
        frame.setBounds(100, 100, 800, 500);
        frame.getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][grow][][pref!]"));

        btnCargar = new JButton("Cargar incidencias para cierre");
        frame.getContentPane().add(btnCargar, "span 2, wrap");

        tabla = new JTable();
        frame.getContentPane().add(new JScrollPane(tabla), "span 2, grow, wrap");

        // Panel de detalle a la derecha/bajo la tabla
        frame.getContentPane().add(new JLabel("Coste imputado:"), "split 4");
        txtCoste = new JTextField(); txtCoste.setEditable(false);
        frame.getContentPane().add(txtCoste, "growx");

        frame.getContentPane().add(new JLabel("Presupuesto vigente:"));
        txtPresupuesto = new JTextField(); txtPresupuesto.setEditable(false);
        frame.getContentPane().add(txtPresupuesto, "growx, wrap");

        frame.getContentPane().add(new JLabel("Importe consumido:"), "split 4");
        txtConsumido = new JTextField(); txtConsumido.setEditable(false);
        frame.getContentPane().add(txtConsumido, "growx");

        frame.getContentPane().add(new JLabel("Disponible:"));
        txtDisponible = new JTextField(); txtDisponible.setEditable(false);
        frame.getContentPane().add(txtDisponible, "growx, wrap");

        btnCerrar = new JButton("Cerrar incidencia");
        frame.getContentPane().add(btnCerrar, "span 2, split 2, right");

        // Añadimos un botón de refresco pequeño por si se quiere
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> { /* placeholder */ });
        frame.getContentPane().add(btnRefrescar);
    }

    public JFrame getFrame() { return frame; }
    public JTable getTabla() { return tabla; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnCerrar() { return btnCerrar; }

    public JTextField getTxtCoste() { return txtCoste; }
    public JTextField getTxtPresupuesto() { return txtPresupuesto; }
    public JTextField getTxtConsumido() { return txtConsumido; }
    public JTextField getTxtDisponible() { return txtDisponible; }
}