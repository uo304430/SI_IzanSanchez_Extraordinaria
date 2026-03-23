package giis.demo.tkrun.ResolverCostes;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;

public class ResolverCostesSelectorView {
    private JFrame frame;
    private JTable tabla;
    private JButton btnActualizar;
    private JButton btnResolver;
    private JButton btnCerrar;

    public ResolverCostesSelectorView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Incidencias asignadas");
        frame.setName("SelectorResolverCostes");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][]"));

        btnActualizar = new JButton("Actualizar");
        frame.getContentPane().add(btnActualizar, "cell 0 0");

        tabla = new JTable();
        frame.getContentPane().add(new JScrollPane(tabla), "cell 0 1,grow");

        btnResolver = new JButton("Resolver seleccionada");
        btnCerrar = new JButton("Cerrar");
        frame.getContentPane().add(btnResolver, "split 2, flowx, cell 0 2");
        frame.getContentPane().add(btnCerrar, "cell 0 2");
    }

    public JFrame getFrame() { return frame; }
    public JTable getTabla() { return tabla; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnResolver() { return btnResolver; }
    public JButton getBtnCerrar() { return btnCerrar; }
}
