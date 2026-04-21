package giis.demo.tkrun.CiudadanoConsulataIncidencias;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import giis.demo.tkrun.DTOs.IncidenciaDTO;

/**
 * Vista para consultar incidencias propias.
 */
public class ConsultaView {
    private JFrame frame;
    private JButton btnConsultar;
    private JButton btnReabrir;
    private JComboBox<Object> cbEstados;
    private JTable tabla;
    private JTextArea txtMotivoReapertura;
    private List<IncidenciaDTO> incidenciasActuales;

    public ConsultaView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Consulta de Incidencias");
        frame.setName("ConsultaIncidencias");
        frame.setBounds(0, 0, 860, 560);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][grow][][]"));

        frame.getContentPane().add(new JLabel("Filtrar por estado:"), "cell 0 0");
        cbEstados = new JComboBox<>(); cbEstados.setName("cbEstados");
        frame.getContentPane().add(cbEstados, "cell 0 1,growx");

        btnConsultar = new JButton("Consultar incidencias");
        btnConsultar.setHorizontalTextPosition(SwingConstants.CENTER);
        frame.getContentPane().add(btnConsultar, "cell 0 2");

        tabla = new JTable();
        tabla.setName("tablaIncidencias");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultEditor(Object.class, null);
        JScrollPane sp = new JScrollPane(tabla);
        frame.getContentPane().add(sp, "cell 0 3,grow");

        frame.getContentPane().add(new JLabel("Motivo de la reapertura:"), "cell 0 4");
        txtMotivoReapertura = new JTextArea(4, 40);
        txtMotivoReapertura.setName("txtMotivoReapertura");
        txtMotivoReapertura.setLineWrap(true);
        txtMotivoReapertura.setWrapStyleWord(true);
        JScrollPane spMotivo = new JScrollPane(txtMotivoReapertura);
        spMotivo.setPreferredSize(new Dimension(200, 90));
        frame.getContentPane().add(spMotivo, "cell 0 5,growx");

        btnReabrir = new JButton("Reabrir incidencia seleccionada");
        btnReabrir.setName("btnReabrirIncidencia");
        btnReabrir.setEnabled(false);
        frame.getContentPane().add(btnReabrir, "cell 0 6,alignx center");
    }

    public JFrame getFrame() { return frame; }
    public JButton getBtnConsultar() { return btnConsultar; }
    public JButton getBtnReabrir() { return btnReabrir; }

    public void populateEstados(List<Object[]> estados) {
        cbEstados.removeAllItems();
        cbEstados.addItem(new EstadoItem(-1, "Todos"));
        if (estados == null) return;
        for (Object[] r: estados) {
            Integer id = r[0]==null? -1: ((Number)r[0]).intValue();
            String name = r[1]==null? "": r[1].toString();
            cbEstados.addItem(new EstadoItem(id, name));
        }
    }

    public int getSelectedEstadoId() {
        Object o = cbEstados.getSelectedItem();
        if (o instanceof EstadoItem) return ((EstadoItem)o).id;
        return -1;
    }

    public void populateTable(List<IncidenciaDTO> incidencias, Map<Integer, String> motivosFinales) {
        incidenciasActuales = incidencias;
        String[] cols = new String[] { "Id", "Tipo", "Descripcion", "Fecha y hora", "Estado", "Motivo final" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (incidencias != null) {
            for (IncidenciaDTO d: incidencias) {
                Object estadoNombre = d.getEstadoNombre();
                Object tipoNombre = d.getTipoNombre();
                LocalDateTime fh = d.getFechaHoraRegistro();
                String fhStr = (fh == null) ? "" : fh.format(dtf);
                String motivoFinal = "";
                if (motivosFinales != null && d.getId() != null && motivosFinales.containsKey(d.getId())) {
                    motivoFinal = motivosFinales.get(d.getId());
                }
                m.addRow(new Object[] { d.getId(), tipoNombre, d.getDescripcion(), fhStr, estadoNombre, motivoFinal });
            }
        }
        tabla.setModel(m);
        if (tabla.getColumnModel().getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(120);
            tabla.getColumnModel().getColumn(2).setPreferredWidth(220);
            tabla.getColumnModel().getColumn(3).setPreferredWidth(140);
            tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(5).setPreferredWidth(240);
        }
        updateReabrirButton(false);
    }

    public void addSelectionListener(ListSelectionListener listener) {
        tabla.getSelectionModel().addListSelectionListener(listener);
    }

    public IncidenciaDTO getSelectedIncidencia() {
        int row = tabla.getSelectedRow();
        if (row < 0 || incidenciasActuales == null || row >= incidenciasActuales.size()) {
            return null;
        }
        return incidenciasActuales.get(row);
    }

    public String getMotivoReapertura() {
        return txtMotivoReapertura.getText();
    }

    public void clearMotivoReapertura() {
        txtMotivoReapertura.setText("");
    }

    public void updateReabrirButton(boolean enabled) {
        btnReabrir.setEnabled(enabled);
    }

    private static class EstadoItem { private final int id; private final String name; EstadoItem(int id, String name){this.id=id;this.name=name;} public String toString(){return name;} }
}
