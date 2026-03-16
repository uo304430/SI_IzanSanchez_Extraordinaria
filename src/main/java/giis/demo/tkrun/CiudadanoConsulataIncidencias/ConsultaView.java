package giis.demo.tkrun.CiudadanoConsulataIncidencias;

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

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import giis.demo.tkrun.DTOs.IncidenciaDTO;

/**
 * Vista para consultar incidencias propias.
 */
public class ConsultaView {
    private JFrame frame;
    private JButton btnConsultar;
    private JComboBox<Object> cbEstados;
    private JTable tabla;

    public ConsultaView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Consulta de Incidencias");
        frame.setName("ConsultaIncidencias");
        frame.setBounds(0, 0, 700, 480);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][grow]") );

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
    }

    public JFrame getFrame() { return frame; }
    public JButton getBtnConsultar() { return btnConsultar; }

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

    public void populateTable(List<IncidenciaDTO> incidencias) {
        String[] cols = new String[] { "Id", "Tipo", "Descripcion", "Fecha y hora", "Estado" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (incidencias != null) {
            for (IncidenciaDTO d: incidencias) {
                Object estadoNombre = d.getEstadoNombre();
                Object tipoNombre = d.getTipoNombre();
                LocalDateTime fh = d.getFechaHoraRegistro();
                String fhStr = (fh == null) ? "" : fh.format(dtf);
                m.addRow(new Object[] { d.getId(), tipoNombre, d.getDescripcion(), fhStr, estadoNombre });
            }
        }
        tabla.setModel(m);
    }

    private static class EstadoItem { private final int id; private final String name; EstadoItem(int id, String name){this.id=id;this.name=name;} public String toString(){return name;} }
}
