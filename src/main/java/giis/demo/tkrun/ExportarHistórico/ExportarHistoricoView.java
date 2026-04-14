package giis.demo.tkrun.ExportarHistórico;

import java.io.File;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import com.github.lgooddatepicker.components.DatePicker;

import net.miginfocom.swing.MigLayout;

public class ExportarHistoricoView {
    private JFrame frame;
    private DatePicker dateFromPicker;
    private DatePicker dateToPicker;
    private JCheckBox chkDate;
    private JCheckBox chkTipo;
    private JCheckBox chkZona;
    private JComboBox<ComboItem> comboTipo;
    private JComboBox<ComboItem> comboZona;
    private JTextField txtPath;
    private JButton btnChoose;
    private JButton btnExport;

    public ExportarHistoricoView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Exportar historial a JSON");
        frame.setName("ExportarHistorial");
        frame.setBounds(100, 100, 600, 200);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[][grow][]", "[][][][]"));

        frame.getContentPane().add(new JLabel("Fecha desde (YYYY-MM-DD):"), "cell 0 0,alignx trailing");
        dateFromPicker = new DatePicker();
        frame.getContentPane().add(dateFromPicker, "cell 1 0,growx");
        chkDate = new JCheckBox("Activar filtro");
        frame.getContentPane().add(chkDate, "cell 2 0,alignx left");

        frame.getContentPane().add(new JLabel("Fecha hasta (YYYY-MM-DD):"), "cell 0 1,alignx trailing");
        dateToPicker = new DatePicker();
        frame.getContentPane().add(dateToPicker, "cell 1 1,growx");

        // inicialmente deshabilitados hasta que se active el checkbox
        dateFromPicker.setEnabled(false);
        dateToPicker.setEnabled(false);

        frame.getContentPane().add(new JLabel("Tipo (id):"), "cell 0 2,alignx trailing");
        comboTipo = new JComboBox<>();
        frame.getContentPane().add(comboTipo, "cell 1 2,growx");
        chkTipo = new JCheckBox("Activar filtro");
        frame.getContentPane().add(chkTipo, "cell 2 2,alignx left");

        comboTipo.setEnabled(false);

        frame.getContentPane().add(new JLabel("Zona (id):"), "cell 0 3,alignx trailing");
        comboZona = new JComboBox<>();
        frame.getContentPane().add(comboZona, "cell 1 3,growx");
        chkZona = new JCheckBox("Activar filtro");
        frame.getContentPane().add(chkZona, "cell 2 3,alignx left");

        comboZona.setEnabled(false);

        txtPath = new JTextField();
        txtPath.setEditable(false);
        frame.getContentPane().add(txtPath, "cell 0 4 2 1,growx");

        btnChoose = new JButton("Seleccionar destino");
        frame.getContentPane().add(btnChoose, "cell 2 4");

        btnExport = new JButton("Exportar a JSON");
        frame.getContentPane().add(btnExport, "cell 1 5,alignx center");

        // default path shown
        String defaultDir = System.getProperty("user.home");
        txtPath.setText(defaultDir);

        // listeners: habilitar/deshabilitar controles asociados según checkbox
        chkDate.addActionListener(e -> {
            boolean on = chkDate.isSelected();
            dateFromPicker.setEnabled(on);
            dateToPicker.setEnabled(on);
        });
        chkTipo.addActionListener(e -> comboTipo.setEnabled(chkTipo.isSelected()));
        chkZona.addActionListener(e -> comboZona.setEnabled(chkZona.isSelected()));

        // chooser behaviour wired by controller via getBtnChoose()
    }

    public JFrame getFrame() { return frame; }
    public String getFromDate() {
        try {
            java.time.LocalDate d = dateFromPicker.getDate();
            return d == null ? "" : d.toString();
        } catch (Exception e) { return ""; }
    }

    public String getToDate() {
        try {
            java.time.LocalDate d = dateToPicker.getDate();
            return d == null ? "" : d.toString();
        } catch (Exception e) { return ""; }
    }

    public boolean isDateChecked() { return chkDate != null && chkDate.isSelected(); }
    public boolean isTipoChecked() { return chkTipo != null && chkTipo.isSelected(); }
    public boolean isZonaChecked() { return chkZona != null && chkZona.isSelected(); }

    public Integer getTipo() {
        try {
            ComboItem it = (ComboItem) comboTipo.getSelectedItem();
            return it == null ? null : it.getId();
        } catch (Exception e) { return null; }
    }

    public Integer getZona() {
        try {
            ComboItem it = (ComboItem) comboZona.getSelectedItem();
            return it == null ? null : it.getId();
        } catch (Exception e) { return null; }
    }
    public String getPathText() { return txtPath.getText().trim(); }
    public void setPathText(String p) { txtPath.setText(p); }
    public JButton getBtnChoose() { return btnChoose; }
    public JButton getBtnExport() { return btnExport; }

    public void setTipoOptions(Map<Integer,String> options) {
        DefaultComboBoxModel<ComboItem> m = new DefaultComboBoxModel<>();
        if (options!=null) for (Map.Entry<Integer,String> e: options.entrySet()) m.addElement(new ComboItem(e.getKey(), e.getValue()));
        comboTipo.setModel(m);
    }

    public void setZonaOptions(Map<Integer,String> options) {
        DefaultComboBoxModel<ComboItem> m = new DefaultComboBoxModel<>();
        if (options!=null) for (Map.Entry<Integer,String> e: options.entrySet()) m.addElement(new ComboItem(e.getKey(), e.getValue()));
        comboZona.setModel(m);
    }

    public File showFileChooserWithDefault(String defaultFileName) {
        JFileChooser chooser = new JFileChooser(getPathText());
        chooser.setDialogTitle("Seleccionar fichero destino (se creará)");
        chooser.setSelectedFile(new File(getPathText(), defaultFileName));
        int r = chooser.showSaveDialog(frame);
        if (r == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
        else return null;
    }

    private static class ComboItem {
        private final Integer id;
        private final String label;
        ComboItem(Integer id, String label) { this.id = id; this.label = label; }
        public Integer getId() { return id; }
        @Override public String toString() { return label == null ? "" : label; }
    }
}
