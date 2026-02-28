package giis.demo.tkrun.usr_registra_incidencia;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javax.swing.SwingConstants;

import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.Entities.TipoIncidenciaEntity;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Vista para registrar incidencias y mostrar la lista existente.
 * Diseño sencillo inspirado en CarrerasView.
 */
public class IncidenciasView {
    private JFrame frame;
    private JTextField txtEmail;
    private JTextField txtDni;
    private JComboBox txtTipo;
    private JTextField txtDescripcion;
    private JTextField txtLocalizacion;
    private JButton btnRegistrar;
    private JRadioButton rbEmail; // nueva
    private JRadioButton rbDni;   // nueva
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
        frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][][][][][][][]") );

        frame.getContentPane().add(new JLabel("Identificación ciudadano:"), "cell 0 0");

        // radio buttons + fields: only one identifier active at a time
        rbEmail = new JRadioButton("Email");
        rbDni = new JRadioButton("DNI");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbEmail);
        bg.add(rbDni);
        rbEmail.setSelected(true);

        txtEmail = new JTextField();
        txtEmail.setName("txtEmail");
        txtDni = new JTextField();
        txtDni.setText("12345678A");
        txtDni.setName("txtDni");
        // Default: email enabled, dni disabled
        txtDni.setEnabled(false);

        // Place radios and fields in same row
        frame.getContentPane().add(rbEmail, "cell 0 1");
        frame.getContentPane().add(txtEmail, "cell 0 1,growx");
        frame.getContentPane().add(rbDni, "cell 0 1");
        frame.getContentPane().add(txtDni, "cell 0 1,growx");

        // Listeners to toggle field enablement
        rbEmail.addActionListener(e -> {
            txtEmail.setEnabled(true);
            txtDni.setEnabled(false);
        });
        rbDni.addActionListener(e -> {
            txtEmail.setEnabled(false);
            txtDni.setEnabled(true);
        });

        frame.getContentPane().add(new JLabel("Tipo incidencia:"), "cell 0 2");

        txtTipo = new JComboBox();
        txtTipo.setName("txtTipo");
        frame.getContentPane().add(txtTipo, "cell 0 3,growx");
        
        lblDescripcion = new JLabel("Descripcion:");
        frame.getContentPane().add(lblDescripcion, "cell 0 4");

        txtDescripcion = new JTextField();
        txtDescripcion.setName("txtDescripcion");
        frame.getContentPane().add(txtDescripcion, "cell 0 5,growx");
        
        lblLocalizacion = new JLabel("Localizacion:");
        frame.getContentPane().add(lblLocalizacion, "cell 0 6");

        txtLocalizacion = new JTextField();
        txtLocalizacion.setName("txtLocalizacion");
        frame.getContentPane().add(txtLocalizacion, "cell 0 7,growx");

        btnRegistrar = new JButton("Registrar incidencia");
        btnRegistrar.setHorizontalTextPosition(SwingConstants.CENTER);
        frame.getContentPane().add(btnRegistrar, "cell 0 8");

        // Tabla eliminada: la vista ahora solo contiene el formulario de registro
    }

    public JFrame getFrame() { return frame; }
    public String getEmail() { return txtEmail.getText(); }
    public String getDni() { return txtDni.getText(); }
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
        txtLocalizacion.setText("");
        txtEmail.setText("");
        txtDni.setText("");
        txtDni.setEnabled(false);
        txtEmail.setEnabled(true);
        rbEmail.setSelected(true);
    }
    public String getDescripcion() { return txtDescripcion.getText(); }
    public String getLocalizacion() { return txtLocalizacion.getText(); }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public boolean isEmailSelected() { return rbEmail.isSelected(); }
    public boolean isDniSelected() { return rbDni.isSelected(); }

    // Helper class to hold id+name in the combobox
    private static class TipoItem {
        private final int id;
        private final String name;
        TipoItem(Integer id, String name) { this.id = id==null? -1: id.intValue(); this.name = name; }
        public String toString() { return name; }
    }

}