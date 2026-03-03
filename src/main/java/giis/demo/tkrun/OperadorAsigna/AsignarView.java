package giis.demo.tkrun.OperadorAsigna;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
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

public class AsignarView {
    private JFrame frame;
    private JTable tablaIncidencias;
    private JTable tablaTecnicos;
    private JButton btnAsignar;

    public AsignarView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Asignar Incidencias");
        frame.setName("AsignarIncidencias");
        frame.setBounds(0, 0, 900, 520);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow][200px]", "[][grow][][]"));

        frame.getContentPane().add(new JLabel("Incidencias a asignar (Validada)"), "cell 0 0");
        frame.getContentPane().add(new JLabel("Técnicos disponibles"), "cell 1 0");

        tablaIncidencias = new JTable();
        tablaIncidencias.setName("tablaIncidenciasAsignar");
        tablaIncidencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaIncidencias.setDefaultEditor(Object.class, null);
        JScrollPane spInc = new JScrollPane(tablaIncidencias);
        frame.getContentPane().add(spInc, "cell 0 1,grow");

        tablaTecnicos = new JTable();
        tablaTecnicos.setName("tablaTecnicos");
        tablaTecnicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTecnicos.setDefaultEditor(Object.class, null);
        JScrollPane spTec = new JScrollPane(tablaTecnicos);
        frame.getContentPane().add(spTec, "cell 1 1,growy");

        btnAsignar = new JButton("Asignar");
        btnAsignar.setName("btnAsignar");
        btnAsignar.setEnabled(false);
        btnAsignar.setHorizontalTextPosition(SwingConstants.CENTER);
        frame.getContentPane().add(btnAsignar, "cell 0 2");

        // enable assign button only when both selections present
        tablaIncidencias.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean selInc = tablaIncidencias.getSelectedRow() != -1;
                boolean selTec = tablaTecnicos.getSelectedRow() != -1;
                btnAsignar.setEnabled(selInc && selTec);
            }
        });
        tablaTecnicos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean selInc = tablaIncidencias.getSelectedRow() != -1;
                boolean selTec = tablaTecnicos.getSelectedRow() != -1;
                btnAsignar.setEnabled(selInc && selTec);
            }
        });
    }

    public JFrame getFrame() { return frame; }
    public JButton getBtnAsignar() { return btnAsignar; }

    public int getSelectedIncidenciaId() {
        int r = tablaIncidencias.getSelectedRow();
        if (r == -1) return -1;
        Object v = tablaIncidencias.getValueAt(r, 0);
        if (v instanceof Number) return ((Number)v).intValue();
        try { return Integer.parseInt(v.toString()); } catch(Exception ex) { return -1; }
    }

    public int getSelectedTecnicoId() {
        int r = tablaTecnicos.getSelectedRow();
        if (r == -1) return -1;
        Object v = tablaTecnicos.getValueAt(r, 0);
        if (v instanceof Number) return ((Number)v).intValue();
        try { return Integer.parseInt(v.toString()); } catch(Exception ex) { return -1; }
    }

    public void populateIncidencias(List<IncidenciaDTO> incidencias) {
        String[] cols = new String[] { "Id", "Tipo", "Descripcion", "Fecha", "Estado" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (incidencias != null) {
            for (IncidenciaDTO d: incidencias) {
                Object estadoNombre = d.getEstadoNombre();
                LocalDateTime fh = d.getFechaHoraRegistro();
                String fhStr = (fh == null) ? "" : fh.format(dtf);
                m.addRow(new Object[] { d.getId(), d.getTipo(), d.getDescripcion(), fhStr, estadoNombre });
            }
        }
        tablaIncidencias.setModel(m);
        btnAsignar.setEnabled(false);
    }

    public void populateTecnicos(List<UsuarioDTO> tecnicos) {
        String[] cols = new String[] { "Id", "Nombre", "Email" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        if (tecnicos != null) {
            for (UsuarioDTO u: tecnicos) {
                m.addRow(new Object[] { u.getId(), u.getNombre(), u.getEmail() });
            }
        }
        tablaTecnicos.setModel(m);
        btnAsignar.setEnabled(false);
    }
}
