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
        // Permite seleccionar varios técnicos a la vez:
        // - Ctrl+Click para selección discontigua
        // - Shift+Click para selección por rango
        // Límite operativo: si un técnico tiene 3 o más incidencias asignadas
        // se mostrará como "(completo)" y no será seleccionable/assignable.
        tablaTecnicos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaTecnicos.setDefaultEditor(Object.class, null);
        JScrollPane spTec = new JScrollPane(tablaTecnicos);
        frame.getContentPane().add(spTec, "cell 1 1,growy");

        btnAsignar = new JButton("Asignar");
        btnAsignar.setName("btnAsignar");
        btnAsignar.setEnabled(false);
        btnAsignar.setHorizontalTextPosition(SwingConstants.CENTER);
        frame.getContentPane().add(btnAsignar, "cell 0 2");

        // Visible instructions for the user about multi-selection and the 3-assignment limit
        JLabel instrucciones = new JLabel("<html>Selecciona varios técnicos: <b>Ctrl+Click</b> (discontigua), <b>Shift+Click</b> (rango).<br/>" +
            "Técnicos con 3 o más incidencias aparecen pero no pueden asignarse.</html>");
        instrucciones.setName("lblInstruccionesAsignar");
        frame.getContentPane().add(instrucciones, "cell 0 3, span 2");

        // enable assign button only when both selections present
        tablaIncidencias.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean selInc = tablaIncidencias.getSelectedRow() != -1;
                boolean selTec = getSelectedTecnicoIds().size() > 0;
                btnAsignar.setEnabled(selInc && selTec);
            }
        });
        tablaTecnicos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean selInc = tablaIncidencias.getSelectedRow() != -1;
                boolean selTec = getSelectedTecnicoIds().size() > 0;
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

    public Integer getSelectedIncidenciaTipo() {
        int r = tablaIncidencias.getSelectedRow();
        if (r == -1) return null;
        Object v = tablaIncidencias.getValueAt(r, 1);
        if (v == null) return null;
        if (v instanceof Number) return ((Number)v).intValue();
        try { return Integer.parseInt(v.toString()); } catch(Exception ex) { return null; }
    }

    public int getSelectedTecnicoId() {
        int r = tablaTecnicos.getSelectedRow();
        if (r == -1) return -1;
        Object v = tablaTecnicos.getValueAt(r, 0);
        if (v instanceof Number) return ((Number)v).intValue();
        try { return Integer.parseInt(v.toString()); } catch(Exception ex) { return -1; }
    }

    /**
     * Devuelve los ids de los técnicos seleccionados (puede ser varios).
     * Ignora técnicos marcados como "(completo)" (carga >= 3) para que no
     * puedan ser asignados.
     * Controles de selección múltiple: Ctrl+Click (discontigua), Shift+Click (rango).
     */
    public java.util.List<Integer> getSelectedTecnicoIds() {
        int[] rows = tablaTecnicos.getSelectedRows();
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (int r : rows) {
            Object v = tablaTecnicos.getValueAt(r, 0);
            // carga is in column index 3; skip if carga >= 3
            Object cargaObj = tablaTecnicos.getValueAt(r, 3);
            int intCarga = 0;
            if (cargaObj instanceof Number) intCarga = ((Number)cargaObj).intValue();
            else if (cargaObj != null) {
                try { intCarga = Integer.parseInt(cargaObj.toString()); } catch(Exception ex) { intCarga = 0; }
            }
            if (intCarga >= 3) continue;
            if (v instanceof Number) ids.add(((Number)v).intValue());
            else {
                try { ids.add(Integer.valueOf(v.toString())); } catch(Exception ex) { }
            }
        }
        return ids;
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

    /**
     * Popula la tabla de técnicos con las filas devueltas por la query: id, nombre, email, carga
     */
    public void populateTecnicosFromQuery(List<Object[]> tecnicos) {
        String[] cols = new String[] { "Id", "Nombre", "Email", "Carga" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        if (tecnicos != null) {
            for (Object[] r: tecnicos) {
                Object id = r.length>0? r[0] : null;
                Object nombre = r.length>1? r[1] : null;
                Object email = r.length>2? r[2] : null;
                Object carga = r.length>5? r[5] : Integer.valueOf(0);
                int intCarga = 0;
                if (carga instanceof Number) intCarga = ((Number)carga).intValue();
                else {
                    try { intCarga = Integer.parseInt(carga.toString()); } catch(Exception ex) { intCarga = 0; }
                }
                System.out.println("Técnico: " + nombre + ", Carga: " + intCarga);
                // show technicians even if they have carga >= 3, but mark them as completo
                Object displayNombre = nombre;
                Object displayCarga = Integer.valueOf(intCarga);
                if (intCarga >= 3) {
                    displayNombre = (nombre == null ? "(sin nombre)" : nombre.toString() + " (completo)");
                    displayCarga = Integer.valueOf(intCarga);
                }
                m.addRow(new Object[] { id, displayNombre, email, displayCarga });
            }
        }
        tablaTecnicos.setModel(m);
        btnAsignar.setEnabled(false);
    }

    // helper to allow controller to listen incidencia selection changes
    public void addIncidenciaSelectionListener(ListSelectionListener l) {
        tablaIncidencias.getSelectionModel().addListSelectionListener(l);
    }
}
