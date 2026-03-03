package giis.demo.tkrun.OperadorValidaIncidencias;

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

public class ValidarView {
    private JFrame frame;
    private JTable tabla;
    private JButton btnValidar;
    private JButton btnRechazar;

    public ValidarView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Validar Incidencias");
        frame.setName("ValidarIncidencias");
        frame.setBounds(0, 0, 800, 480);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][]"));

        frame.getContentPane().add(new JLabel("Incidencias en estado 'Nueva'"), "cell 0 0");

        tabla = new JTable();
        tabla.setName("tablaIncidencias");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultEditor(Object.class, null);
        JScrollPane sp = new JScrollPane(tabla);
        frame.getContentPane().add(sp, "cell 0 1,grow");

        btnValidar = new JButton("Validar");
        btnValidar.setName("btnValidar");
        btnValidar.setEnabled(false);
        btnValidar.setHorizontalTextPosition(SwingConstants.CENTER);

        btnRechazar = new JButton("Rechazar");
        btnRechazar.setName("btnRechazar");
        btnRechazar.setEnabled(false);
        btnRechazar.setHorizontalTextPosition(SwingConstants.CENTER);

        frame.getContentPane().add(btnValidar, "split 2, flowx, cell 0 2");
        frame.getContentPane().add(btnRechazar, "cell 0 2");

        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean sel = tabla.getSelectedRow() != -1;
                btnValidar.setEnabled(sel);
                btnRechazar.setEnabled(sel);
            }
        });
    }

    public JFrame getFrame() { return frame; }
    public JButton getBtnValidar() { return btnValidar; }
    public JButton getBtnRechazar() { return btnRechazar; }

    public int getSelectedIncidenciaId() {
        int r = tabla.getSelectedRow();
        if (r == -1) return -1;
        Object v = tabla.getValueAt(r, 0);
        if (v instanceof Number) return ((Number)v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception ex) { return -1; }
    }

    public void populateTable(List<IncidenciaDTO> incidencias) {
        String[] cols = new String[] { "Id", "Tipo", "Descripcion", "Fecha y hora", "Estado", "Ciudadano" };
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (incidencias != null) {
            for (IncidenciaDTO d: incidencias) {
                Object estadoNombre = d.getEstadoNombre();
                LocalDateTime fh = d.getFechaHoraRegistro();
                String fhStr = (fh == null) ? "" : fh.format(dtf);
                UsuarioDTO ciudad = d.getCiudadano();
                String ciudadano = "";
                if (ciudad != null) {
                    if (ciudad.getEmail() != null && !ciudad.getEmail().isEmpty()) ciudadano = ciudad.getEmail();
                    else if (ciudad.getDni() != null) ciudadano = ciudad.getDni();
                }
                m.addRow(new Object[] { d.getId(), d.getTipo(), d.getDescripcion(), fhStr, estadoNombre, ciudadano });
            }
        }
        tabla.setModel(m);
        btnValidar.setEnabled(false);
        btnRechazar.setEnabled(false);
    }
}