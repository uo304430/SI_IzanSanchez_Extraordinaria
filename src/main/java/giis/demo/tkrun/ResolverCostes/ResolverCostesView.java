package giis.demo.tkrun.ResolverCostes;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ResolverCostesView {
    private JFrame frame;
    private JTextField txtId;
    private JTextField txtHoras;
    private JTextField txtCosteHora;
    private JTextField txtMatNombre;
    private JTextField txtMatCoste;
    private JButton btnAddMaterial;
    private JButton btnRemoveMaterial;
    private JButton btnSiguiente;
    private JButton btnAtras;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private JTable tablaMateriales;
    private JLabel lblTotal;

    public ResolverCostesView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Resolver Incidencia");
        frame.setName("ResolverIncidencia");
        frame.setBounds(100, 100, 600, 420);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // cuatro columnas para alinear correctamente campos y botones
        frame.getContentPane().setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][][][grow][]"));

        frame.getContentPane().add(new JLabel("ID Incidencia:"), "cell 0 0, split 2");
        txtId = new JTextField(); txtId.setEditable(false);
        frame.getContentPane().add(txtId, "cell 1 0,growx");

        frame.getContentPane().add(new JLabel("Horas:"), "cell 0 1");
        txtHoras = new JTextField("0");
        frame.getContentPane().add(txtHoras, "cell 1 1,growx");
        frame.getContentPane().add(new JLabel("Coste/hora:"), "cell 2 1");
        txtCosteHora = new JTextField("20");
        frame.getContentPane().add(txtCosteHora, "cell 3 1,growx");

        // material inputs en columnas separadas
        frame.getContentPane().add(new JLabel("Material (nombre):"), "cell 0 2");
        txtMatNombre = new JTextField();
        frame.getContentPane().add(txtMatNombre, "cell 1 2,growx");
        frame.getContentPane().add(new JLabel("Coste:"), "cell 2 2");
        txtMatCoste = new JTextField();
        frame.getContentPane().add(txtMatCoste, "cell 3 2,growx");

        btnAddMaterial = new JButton("Añadir material");
        btnRemoveMaterial = new JButton("Eliminar material");
        frame.getContentPane().add(btnAddMaterial, "cell 1 4");
        frame.getContentPane().add(btnRemoveMaterial, "cell 2 4");

        // navegación por pasos: primero horas, luego materiales
        btnAtras = new JButton("Atrás");
        btnSiguiente = new JButton("Siguiente: Materiales");
        frame.getContentPane().add(btnAtras, "cell 0 5");
        frame.getContentPane().add(btnSiguiente, "cell 3 5");

        tablaMateriales = new JTable();
        tablaMateriales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMateriales.setModel(new DefaultTableModel(new String[] {"Nombre","Coste"}, 0));
        // la tabla ocupa las 4 columnas
        frame.getContentPane().add(new JScrollPane(tablaMateriales), "cell 0 3, span 4, grow");

        lblTotal = new JLabel("Total: 0.0");
        lblTotal.setHorizontalTextPosition(SwingConstants.RIGHT);
        frame.getContentPane().add(lblTotal, "cell 3 5,align right");

        btnConfirmar = new JButton("Confirmar resolución");
        btnCancelar = new JButton("Cancelar");
        frame.getContentPane().add(btnConfirmar, "cell 1 5");
        frame.getContentPane().add(btnCancelar, "cell 2 5");

        // estado inicial: solo paso 1 (horas)
        setMaterialsEnabled(false);
        btnAtras.setEnabled(false);
        btnConfirmar.setEnabled(false);

        tablaMateriales.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean sel = tablaMateriales.getSelectedRow() != -1;
                btnRemoveMaterial.setEnabled(sel);
            }
        });
        btnRemoveMaterial.setEnabled(false);
    }

    public JFrame getFrame() { return frame; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtHoras() { return txtHoras; }
    public JTextField getTxtCosteHora() { return txtCosteHora; }
    public JTextField getTxtMatNombre() { return txtMatNombre; }
    public JTextField getTxtMatCoste() { return txtMatCoste; }
    public JButton getBtnAddMaterial() { return btnAddMaterial; }
    public JButton getBtnRemoveMaterial() { return btnRemoveMaterial; }
    public JButton getBtnSiguiente() { return btnSiguiente; }
    public JButton getBtnAtras() { return btnAtras; }
    public JButton getBtnConfirmar() { return btnConfirmar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JTable getTablaMateriales() { return tablaMateriales; }
    public JLabel getLblTotal() { return lblTotal; }

    public void addMaterial(String nombre, double coste) {
        DefaultTableModel m = (DefaultTableModel) tablaMateriales.getModel();
        m.addRow(new Object[] { nombre, coste });
        updateTotal();
    }

    public void removeSelectedMaterial() {
        int r = tablaMateriales.getSelectedRow();
        if (r != -1) {
            DefaultTableModel m = (DefaultTableModel) tablaMateriales.getModel();
            m.removeRow(r);
            updateTotal();
        }
    }

    public void setMaterialsEnabled(boolean enabled) {
        txtMatNombre.setEnabled(enabled);
        txtMatCoste.setEnabled(enabled);
        btnAddMaterial.setEnabled(enabled);
        // mantener estado del botón eliminar según selección y habilitación
        btnRemoveMaterial.setEnabled(enabled && tablaMateriales.getSelectedRow() != -1);
        tablaMateriales.setEnabled(enabled);
    }

    public List<ResolverCostesModel.Material> getMateriales() {
        List<ResolverCostesModel.Material> list = new ArrayList<>();
        DefaultTableModel m = (DefaultTableModel) tablaMateriales.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            Object n = m.getValueAt(i, 0);
            Object c = m.getValueAt(i, 1);
            String nombre = n == null ? "" : n.toString();
            double coste = 0.0;
            try { coste = Double.parseDouble(c.toString()); } catch (Exception ex) { coste = 0.0; }
            list.add(new ResolverCostesModel.Material(nombre, coste));
        }
        return list;
    }

    public void updateTotal() {
        double totalMat = 0.0;
        DefaultTableModel m = (DefaultTableModel) tablaMateriales.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            Object c = m.getValueAt(i, 1);
            try { totalMat += Double.parseDouble(c.toString()); } catch (Exception ex) { }
        }
        double horas = 0.0;
        double costeHora = 0.0;
        try { horas = Double.parseDouble(txtHoras.getText()); } catch (Exception ex) { horas = 0.0; }
        try { costeHora = Double.parseDouble(txtCosteHora.getText()); } catch (Exception ex) { costeHora = 0.0; }
        double total = totalMat + horas * costeHora;
        lblTotal.setText("Total: " + total);
    }
}
