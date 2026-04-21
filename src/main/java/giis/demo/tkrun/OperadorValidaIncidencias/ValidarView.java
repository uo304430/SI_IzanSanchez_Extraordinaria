package giis.demo.tkrun.OperadorValidaIncidencias;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.Entities.TipoIncidenciaEntity;

public class ValidarView {
    private JFrame frame;
    private JTable tabla;
    private JComboBox<TipoItem> cbTipos;
    private JButton btnValidar;
    private JButton btnRechazar;
    private List<IncidenciaDTO> incidenciasActuales;

    public ValidarView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Validar Incidencias");
        frame.setName("ValidarIncidencias");
        frame.setBounds(0, 0, 800, 480);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][][]"));

        frame.getContentPane().add(new JLabel("Incidencias en estado 'Nueva'"), "cell 0 0");

        tabla = new JTable();
        tabla.setName("tablaIncidencias");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultEditor(Object.class, null);
        JScrollPane sp = new JScrollPane(tabla);
        frame.getContentPane().add(sp, "cell 0 1,grow");

        frame.getContentPane().add(new JLabel("Tipo de incidencia:"), "cell 0 2");

        cbTipos = new JComboBox<>();
        cbTipos.setName("cbTiposIncidencia");
        cbTipos.setEnabled(false);
        frame.getContentPane().add(cbTipos, "cell 0 3,growx");

        btnValidar = new JButton("Validar");
        btnValidar.setName("btnValidar");
        btnValidar.setEnabled(false);
        btnValidar.setHorizontalTextPosition(SwingConstants.CENTER);

        btnRechazar = new JButton("Rechazar incidencia");
        btnRechazar.setName("btnRechazar");
        btnRechazar.setEnabled(false);
        btnRechazar.setHorizontalTextPosition(SwingConstants.CENTER);

        frame.getContentPane().add(btnValidar, "split 2, flowx, cell 0 4");
        frame.getContentPane().add(btnRechazar, "cell 0 4");

        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean sel = tabla.getSelectedRow() != -1;
                btnValidar.setEnabled(sel);
                btnRechazar.setEnabled(sel);
                cbTipos.setEnabled(sel && cbTipos.getItemCount() > 0);
                if (sel) {
                    syncSelectedTipoWithTable();
                }
            }
        });
    }

    public JFrame getFrame() { return frame; }
    public JButton getBtnValidar() { return btnValidar; }
    public JButton getBtnRechazar() { return btnRechazar; }
    public JComboBox<TipoItem> getCbTipos() { return cbTipos; }

    public int getSelectedIncidenciaId() {
        int r = tabla.getSelectedRow();
        if (r == -1) return -1;
        Object v = tabla.getValueAt(r, 0);
        if (v instanceof Number) return ((Number)v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception ex) { return -1; }
    }

    public void populateTipos(List<TipoIncidenciaEntity> tipos) {
        cbTipos.removeAllItems();
        if (tipos != null) {
            for (TipoIncidenciaEntity tipo : tipos) {
                cbTipos.addItem(new TipoItem(tipo.getId(), tipo.getNombre()));
            }
        }
        cbTipos.setEnabled(tabla.getSelectedRow() != -1 && cbTipos.getItemCount() > 0);
        syncSelectedTipoWithTable();
    }

    public int getSelectedTipoId() {
        Object selected = cbTipos.getSelectedItem();
        if (selected instanceof TipoItem) return ((TipoItem) selected).id;
        return -1;
    }

    public IncidenciaDTO getSelectedIncidencia() {
        int r = tabla.getSelectedRow();
        if (r < 0 || incidenciasActuales == null || r >= incidenciasActuales.size()) return null;
        return incidenciasActuales.get(r);
    }

    public void populateTable(List<IncidenciaDTO> incidencias) {
        incidenciasActuales = incidencias;
        String[] cols = new String[] { "Id", "Tipo", "Descripcion", "Fecha y hora", "Estado", "Ciudadano" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (incidencias != null) {
            for (IncidenciaDTO d: incidencias) {
                Object estadoNombre = d.getEstadoNombre();
                Object tipoNombre = d.getTipoNombre();
                LocalDateTime fh = d.getFechaHoraRegistro();
                String fhStr = (fh == null) ? "" : fh.format(dtf);
                UsuarioDTO ciudad = d.getCiudadano();
                String ciudadano = "";
                if (ciudad != null) {
                    if (ciudad.getEmail() != null && !ciudad.getEmail().isEmpty()) ciudadano = ciudad.getEmail();
                    else if (ciudad.getDni() != null) ciudadano = ciudad.getDni();
                }
                m.addRow(new Object[] { d.getId(), tipoNombre, d.getDescripcion(), fhStr, estadoNombre, ciudadano });
            }
        }
        tabla.setModel(m);
        btnValidar.setEnabled(false);
        btnRechazar.setEnabled(false);
        cbTipos.setEnabled(false);
        syncSelectedTipoWithTable();
    }

    private void syncSelectedTipoWithTable() {
        IncidenciaDTO seleccionada = getSelectedIncidencia();
        if (seleccionada == null) return;
        for (int i = 0; i < cbTipos.getItemCount(); i++) {
            TipoItem item = cbTipos.getItemAt(i);
            if (item.id == (seleccionada.getTipo() == null ? -1 : seleccionada.getTipo().intValue())) {
                cbTipos.setSelectedIndex(i);
                return;
            }
        }
    }

    static class TipoItem {
        private final int id;
        private final String name;

        TipoItem(Integer id, String name) {
            this.id = id == null ? -1 : id.intValue();
            this.name = name == null ? "" : name;
        }

        public String toString() {
            return name;
        }
    }
}

