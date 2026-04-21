package giis.demo.tkrun.GestorEconomicoDefinePresupuesto;

import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import giis.demo.tkrun.Entities.TipoIncidenciaEntity;
import net.miginfocom.swing.MigLayout;

public class DefinePresupuestoView {
    private static final String[] PRESUPUESTO_COLUMNS =
            new String[] {"Id", "Tipo", "Presupuesto", "Consumido", "Fecha inicio", "Fecha fin"};
    private JFrame frame;
    private JTable tablaPresupuestos;
    private JComboBox<TipoItem> cbTipo;
    private JTextField txtImporte;
    private JRadioButton rbAnual;
    private JRadioButton rbFechas;
    private JTextField txtAnio;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnGuardar;

    public DefinePresupuestoView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Definir Presupuesto");
        frame.setName("DefinirPresupuesto");
        frame.setBounds(0, 0, 980, 560);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][][][][][][grow][]"));

        frame.getContentPane().add(new JLabel("Tipo de incidencia:"), "cell 0 0");
        cbTipo = new JComboBox<>();
        cbTipo.setName("cbTipoPresupuesto");
        frame.getContentPane().add(cbTipo, "cell 1 0,growx");

        frame.getContentPane().add(new JLabel("Importe máximo:"), "cell 0 1");
        txtImporte = new JTextField();
        txtImporte.setName("txtImportePresupuesto");
        frame.getContentPane().add(txtImporte, "cell 1 1,growx");

        frame.getContentPane().add(new JLabel("Vigencia:"), "cell 0 2");
        rbAnual = new JRadioButton("Anual");
        rbAnual.setName("rbVigenciaAnual");
        rbFechas = new JRadioButton("Por fechas");
        rbFechas.setName("rbVigenciaFechas");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAnual);
        bg.add(rbFechas);
        rbAnual.setSelected(true);
        frame.getContentPane().add(rbAnual, "split 2, cell 1 2");
        frame.getContentPane().add(rbFechas, "cell 1 2");

        frame.getContentPane().add(new JLabel("Año de vigencia:"), "cell 0 3");
        txtAnio = new JTextField(String.valueOf(java.time.LocalDate.now().getYear()));
        txtAnio.setName("txtAnioPresupuesto");
        frame.getContentPane().add(txtAnio, "cell 1 3,growx");

        frame.getContentPane().add(new JLabel("Fecha inicio (YYYY-MM-DD):"), "cell 0 4");
        txtFechaInicio = new JTextField();
        txtFechaInicio.setName("txtFechaInicioPresupuesto");
        frame.getContentPane().add(txtFechaInicio, "cell 1 4,growx");

        frame.getContentPane().add(new JLabel("Fecha fin (YYYY-MM-DD):"), "cell 0 5");
        txtFechaFin = new JTextField();
        txtFechaFin.setName("txtFechaFinPresupuesto");
        frame.getContentPane().add(txtFechaFin, "cell 1 5,growx");

        frame.getContentPane().add(new JLabel("Presupuestos definidos"), "cell 0 6 2 1");
        tablaPresupuestos = new JTable();
        tablaPresupuestos.setName("tablaPresupuestos");
        tablaPresupuestos.setDefaultEditor(Object.class, null);
        frame.getContentPane().add(new JScrollPane(tablaPresupuestos), "cell 0 7 2 1,grow");

        btnGuardar = new JButton("Definir presupuesto");
        btnGuardar.setName("btnDefinirPresupuesto");
        frame.getContentPane().add(btnGuardar, "cell 0 8 2 1,alignx center");

        updateVigenciaMode();
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JRadioButton getRbAnual() {
        return rbAnual;
    }

    public JRadioButton getRbFechas() {
        return rbFechas;
    }

    public void populateTipos(List<TipoIncidenciaEntity> tipos) {
        cbTipo.removeAllItems();
        if (tipos == null) return;
        for (TipoIncidenciaEntity tipo : tipos) {
            cbTipo.addItem(new TipoItem(tipo.getId(), tipo.getNombre()));
        }
    }

    public void populatePresupuestos(List<Object[]> presupuestos) {
        DefaultTableModel model = new DefaultTableModel(PRESUPUESTO_COLUMNS, 0);
        if (presupuestos != null) {
            for (Object[] row : presupuestos) {
                model.addRow(new Object[] {
                        row.length > 0 ? row[0] : null,
                        row.length > 1 ? row[1] : null,
                        row.length > 2 ? row[2] : null,
                        row.length > 3 ? row[3] : null,
                        row.length > 4 ? row[4] : null,
                        row.length > 5 ? row[5] : null
                });
            }
        }
        tablaPresupuestos.setModel(model);
    }

    public int getSelectedTipoId() {
        Object selected = cbTipo.getSelectedItem();
        if (selected instanceof TipoItem) return ((TipoItem) selected).id;
        return -1;
    }

    public String getImporte() {
        return txtImporte.getText();
    }

    public boolean isVigenciaAnual() {
        return rbAnual.isSelected();
    }

    public String getAnio() {
        return txtAnio.getText();
    }

    public String getFechaInicio() {
        return txtFechaInicio.getText();
    }

    public String getFechaFin() {
        return txtFechaFin.getText();
    }

    public void updateVigenciaMode() {
        boolean anual = rbAnual.isSelected();
        txtAnio.setEnabled(anual);
        txtFechaInicio.setEnabled(!anual);
        txtFechaFin.setEnabled(!anual);
    }

    public void clearForm() {
        txtImporte.setText("");
        txtAnio.setText(String.valueOf(java.time.LocalDate.now().getYear()));
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        rbAnual.setSelected(true);
        updateVigenciaMode();
    }

    private static class TipoItem {
        private final int id;
        private final String nombre;

        TipoItem(Integer id, String nombre) {
            this.id = id == null ? -1 : id.intValue();
            this.nombre = nombre == null ? "" : nombre;
        }

        public String toString() {
            return nombre;
        }
    }
}
