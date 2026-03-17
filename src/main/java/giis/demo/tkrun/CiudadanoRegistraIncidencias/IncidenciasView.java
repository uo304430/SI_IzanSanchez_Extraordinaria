package giis.demo.tkrun.CiudadanoRegistraIncidencias;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javax.swing.SwingConstants;

import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.Entities.TipoIncidenciaEntity;
import giis.demo.tkrun.Entities.ZonaEntity;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Vista para registrar incidencias y mostrar la lista existente.
 * Diseño sencillo inspirado en CarrerasView.
 */
public class IncidenciasView {
    private JFrame frame;
    private JComboBox txtTipo;
    private JTextArea txtDescripcion;
    private JComboBox cmbLocalizacion;
    private JButton btnRegistrar;
    private JLabel lblDescripcion;
    private JLabel lblLocalizacion;

    public IncidenciasView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Registro de Incidencias");
        frame.setName("Incidencias");
        frame.setBounds(0, 0, 640, 480);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][][][]") );

        frame.getContentPane().add(new JLabel("Tipo incidencia:"), "cell 0 0");

        txtTipo = new JComboBox();
        txtTipo.setName("txtTipo");
        frame.getContentPane().add(txtTipo, "cell 0 1,growx");
        
        lblDescripcion = new JLabel("Descripcion:");
        frame.getContentPane().add(lblDescripcion, "cell 0 2");

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setName("txtDescripcion");
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        frame.getContentPane().add(scrollDescripcion, "cell 0 3,growx");
        
        lblLocalizacion = new JLabel("Localizacion:");
        frame.getContentPane().add(lblLocalizacion, "cell 0 4");

        cmbLocalizacion = new JComboBox();
        cmbLocalizacion.setName("cmbLocalizacion");
        frame.getContentPane().add(cmbLocalizacion, "cell 0 5,growx");

        btnRegistrar = new JButton("Registrar incidencia");
        btnRegistrar.setHorizontalTextPosition(SwingConstants.CENTER);
        frame.getContentPane().add(btnRegistrar, "cell 0 6");

        // Tabla eliminada: la vista ahora solo contiene el formulario de registro
    }

    public JFrame getFrame() { return frame; }
    /** Devuelve el nombre del tipo seleccionado (o null si no hay ninguno) */
    public String getTipo() {
        Object it = txtTipo.getSelectedItem();
        return it==null?null:it.toString();
    }
    /** Llena el combobox con la lista de tipos obtenida desde el modelo. */
    public void populateTipos(List<TipoIncidenciaEntity> tipos) {
        txtTipo.removeAllItems();
        if (tipos==null) return;
        for (TipoIncidenciaEntity t: tipos){
            txtTipo.addItem(new TipoItem(t.getId(), t.getNombre()));
        }
    }

    /** Llena el combobox de zonas con la lista obtenida desde el modelo. */
    public void populateZonas(List<ZonaEntity> zonas) {
        cmbLocalizacion.removeAllItems();
        // opción inicial nula
        cmbLocalizacion.addItem(new ZonaItem(0, "Null"));
        if (zonas==null) return;
        for (ZonaEntity z : zonas) {
            cmbLocalizacion.addItem(new ZonaItem(z.getId(), z.getDescripcion()));
        }
        cmbLocalizacion.setSelectedIndex(0);
    }

    /** Devuelve el id del tipo seleccionado o -1 si no hay ninguno */
    public int getSelectedTipoId() {
        Object o = txtTipo.getSelectedItem();
        if (o instanceof TipoItem) return ((TipoItem)o).id;
        return -1;
    }

    /** Mostrar una confirmación al usuario con los datos de la incidencia registrada. */
    public void showConfirmation(IncidenciaDTO dto) {
        if (dto==null) return;
        // Use the actual getter name for the timestamp and format it safely
        LocalDateTime fecha = dto.getFechaHoraRegistro();
        String fechaStr = "";
        if (fecha!=null) {
            fechaStr = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        String msg = "Incidencia registrada:\nId: "+dto.getId()+"\nTipo: "+getTipo()+"\nDescripcion: "+dto.getDescripcion()+"\nLocalizacion: "+dto.getLocalizacion()+"\nFecha: "+fechaStr;
        JOptionPane.showMessageDialog(frame, msg, "Confirmación", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Limpia campos del formulario (sin tocar lista de incidencias) */
    public void clearForm() {
        txtDescripcion.setText("");
        if (cmbLocalizacion.getItemCount()>0) {
            cmbLocalizacion.setSelectedIndex(0);
        }
    }
    public String getDescripcion() { return txtDescripcion.getText().trim(); }
    public String getLocalizacion() {
        Object o = cmbLocalizacion.getSelectedItem();
        if (o instanceof ZonaItem) return ((ZonaItem)o).descripcion;
        return o==null?"":o.toString();
    }
    public int getSelectedZonaId() {
        Object o = cmbLocalizacion.getSelectedItem();
        if (o instanceof ZonaItem) return ((ZonaItem)o).id;
        return -1;
    }
    public JButton getBtnRegistrar() { return btnRegistrar; }

    // Helper class to hold id+name in the combobox
    private static class TipoItem {
        private final int id;
        private final String name;
        TipoItem(Integer id, String name) { this.id = id==null? -1: id.intValue(); this.name = name; }
        public String toString() { return name; }
    }

    // Helper class to hold id+description in the combobox de zonas
    private static class ZonaItem {
        private final int id;
        private final String descripcion;
        ZonaItem(Integer id, String descripcion) { this.id = id==null? -1: id.intValue(); this.descripcion = descripcion; }
        public String toString() { return descripcion; }
    }

}