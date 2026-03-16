package giis.demo.tkrun.CiudadanoConsulataIncidencias;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
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
    private JTextField txtEmail;
    private JTextField txtDni;
    private JRadioButton rbEmail;
    private JRadioButton rbDni;
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
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][][grow]") );

        frame.getContentPane().add(new JLabel("Identificación ciudadano:"), "cell 0 0");

        rbEmail = new JRadioButton("Email");
        rbDni = new JRadioButton("DNI");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbEmail); bg.add(rbDni);
        rbEmail.setSelected(true);

        txtEmail = new JTextField(); txtEmail.setName("txtEmail");
        txtDni = new JTextField(); txtDni.setName("txtDni"); txtDni.setEnabled(false);
        txtDni.setText("12345678A");

        frame.getContentPane().add(rbEmail, "cell 0 1");
        frame.getContentPane().add(txtEmail, "cell 0 1,growx");
        frame.getContentPane().add(rbDni, "cell 0 1");
        frame.getContentPane().add(txtDni, "cell 0 1,growx");

        rbEmail.addActionListener(e -> { txtEmail.setEnabled(true); txtDni.setEnabled(false); });
        rbDni.addActionListener(e -> { txtEmail.setEnabled(false); txtDni.setEnabled(true); });

        frame.getContentPane().add(new JLabel("Filtrar por estado:"), "cell 0 2");
        cbEstados = new JComboBox<>(); cbEstados.setName("cbEstados");
        frame.getContentPane().add(cbEstados, "cell 0 3,growx");

        btnConsultar = new JButton("Consultar incidencias");
        btnConsultar.setHorizontalTextPosition(SwingConstants.CENTER);
        frame.getContentPane().add(btnConsultar, "cell 0 3");

        tabla = new JTable();
        tabla.setName("tablaIncidencias");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultEditor(Object.class, null);
        JScrollPane sp = new JScrollPane(tabla);
        frame.getContentPane().add(sp, "cell 0 4,grow");
    }

    public JFrame getFrame() { return frame; }
    public String getEmail() { return txtEmail.getText(); }
    public String getDni() { return txtDni.getText(); }
    public boolean isEmailSelected() { return rbEmail.isSelected(); }
    public boolean isDniSelected() { return rbDni.isSelected(); }
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
