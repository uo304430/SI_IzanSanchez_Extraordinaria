package InformeEconomico;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;

public class InformeEconomicoView {
    private JFrame frame;
    private JTable tablaCostes;
    private DatePicker dateDesde, dateHasta;
    private JTextField txtPresupuesto;
    private JComboBox<String> cbTipo;
    private JButton btnGenerar;
    private JLabel lblPresupuestoTotal, lblImporteConsumido, lblPorcentaje;

    // New labels for selected type metrics
    private JLabel lblNumIncidenciasTipo;
    private JLabel lblCosteMedioTipo;
    private JLabel lblCosteTotalTipo;

    // Fallback fields if DatePicker fails
    private JTextField fallbackDesdeField;
    private JTextField fallbackHastaField;

    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;

    public InformeEconomicoView() {
        frame = new JFrame("Informe Económico");
        frame.setBounds(100, 100, 800, 500);
        frame.getContentPane().setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][][] [grow] []"));

        try {
            frame.getContentPane().add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), "cell 0 0");
            DatePickerSettings settingsDesde = new DatePickerSettings();
            settingsDesde.setFormatForDatesCommonEra("yyyy-MM-dd");
            dateDesde = new DatePicker(settingsDesde);
            dateDesde.setDate(LocalDate.parse("2010-01-01"));
            // Disable manual typing to force use of calendar
            dateDesde.getComponentDateTextField().setEditable(false);
            frame.getContentPane().add(dateDesde, "growx");

            frame.getContentPane().add(new JLabel("Fecha Fin (YYYY-MM-DD):"), "cell 2 0");
            DatePickerSettings settingsHasta = new DatePickerSettings();
            settingsHasta.setFormatForDatesCommonEra("yyyy-MM-dd");
            dateHasta = new DatePicker(settingsHasta);
            dateHasta.setDate(LocalDate.parse("2026-12-31"));
            // Disable manual typing to force use of calendar
            dateHasta.getComponentDateTextField().setEditable(false);
            frame.getContentPane().add(dateHasta, "growx");

            // Prevent selecting a 'hasta' date earlier than 'desde'
            DateVetoPolicy vetoPolicy = new DateVetoPolicy() {
                @Override
                public boolean isDateAllowed(LocalDate date) {
                    LocalDate desde = dateDesde.getDate();
                    if (desde == null) return true; // if no start date, allow any
                    return !date.isBefore(desde);
                }
            };
            dateHasta.getSettings().setVetoPolicy(vetoPolicy);

            // Update veto policy when start date changes
            dateDesde.addDateChangeListener(event -> {
                // reassign policy so it sees the new start date
                dateHasta.getSettings().setVetoPolicy(new DateVetoPolicy() {
                    @Override
                    public boolean isDateAllowed(LocalDate date) {
                        LocalDate desde = dateDesde.getDate();
                        if (desde == null) return true;
                        return !date.isBefore(desde);
                    }
                });
            });

        } catch (Throwable t) {
            // If DatePicker or related setup fails at runtime, fallback to simple text fields
            System.err.println("[WARN] DatePicker failed to initialize, falling back to text fields: " + t);
            t.printStackTrace();

            frame.getContentPane().add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), "cell 0 0");
            fallbackDesdeField = new JTextField("2010-01-01");
            frame.getContentPane().add(fallbackDesdeField, "growx");

            frame.getContentPane().add(new JLabel("Fecha Fin (YYYY-MM-DD):"), "cell 2 0");
            fallbackHastaField = new JTextField("2026-12-31");
            frame.getContentPane().add(fallbackHastaField, "growx");

            // ensure date pickers remain null
            dateDesde = null;
            dateHasta = null;
        }

        frame.getContentPane().add(new JLabel("Tipo (Todos):"), "cell 0 1");
        cbTipo = new JComboBox<>(new String[]{"Todos"});
        frame.getContentPane().add(cbTipo, "growx");

        frame.getContentPane().add(new JLabel("Presupuesto:"), "cell 1 1");
        txtPresupuesto = new JTextField("0");
        frame.getContentPane().add(txtPresupuesto, "growx");

        btnGenerar = new JButton("Generar Informe");
        frame.getContentPane().add(btnGenerar, "cell 3 1, growx");

        tablaCostes = new JTable();
        frame.getContentPane().add(new JScrollPane(tablaCostes), "cell 0 3 4 1, grow");

        // Presupuesto details
        frame.getContentPane().add(new JLabel("Presupuesto Total:"), "cell 0 4");
        lblPresupuestoTotal = new JLabel("0.00");
        frame.getContentPane().add(lblPresupuestoTotal, "cell 1 4");

        frame.getContentPane().add(new JLabel("Importe Consumido:"), "cell 2 4");
        lblImporteConsumido = new JLabel("0.00");
        frame.getContentPane().add(lblImporteConsumido, "cell 3 4");

        frame.getContentPane().add(new JLabel("Porcentaje Consumido:"), "cell 0 5");
        lblPorcentaje = new JLabel("0.00%");
        frame.getContentPane().add(lblPorcentaje, "cell 1 5");

        // Selected-type metrics (new)
        frame.getContentPane().add(new JLabel("Nº Incidencias (tipo seleccionado):"), "cell 0 6");
        lblNumIncidenciasTipo = new JLabel("0");
        frame.getContentPane().add(lblNumIncidenciasTipo, "cell 1 6");

        frame.getContentPane().add(new JLabel("Coste medio por incidencia:"), "cell 2 6");
        lblCosteMedioTipo = new JLabel("0.00");
        frame.getContentPane().add(lblCosteMedioTipo, "cell 3 6");

        frame.getContentPane().add(new JLabel("Coste total acumulado:"), "cell 0 7");
        lblCosteTotalTipo = new JLabel("0.00");
        frame.getContentPane().add(lblCosteTotalTipo, "cell 1 7");
    }

    public JFrame getFrame() { return frame; }
    public JTable getTablaCostes() { return tablaCostes; }
    public JButton getBtnGenerar() { return btnGenerar; }
    // Return formatted date strings for compatibility
    public String getDesde() {
        if (dateDesde != null && dateDesde.getDate() != null) return dateDesde.getDate().toString();
        if (fallbackDesdeField != null) return fallbackDesdeField.getText();
        return "";
    }
    public String getHasta() {
        if (dateHasta != null && dateHasta.getDate() != null) return dateHasta.getDate().toString();
        if (fallbackHastaField != null) return fallbackHastaField.getText();
        return "";
    }
    public LocalDate getDesdeLocalDate() {
        if (dateDesde != null) return dateDesde.getDate();
        if (fallbackDesdeField != null) {
            try { return LocalDate.parse(fallbackDesdeField.getText(), DF); } catch (DateTimeParseException e) { return null; }
        }
        return null;
    }
    public LocalDate getHastaLocalDate() {
        if (dateHasta != null) return dateHasta.getDate();
        if (fallbackHastaField != null) {
            try { return LocalDate.parse(fallbackHastaField.getText(), DF); } catch (DateTimeParseException e) { return null; }
        }
        return null;
    }
    public String getPresupuesto() { return txtPresupuesto.getText(); }
    public JComboBox<String> getCbTipo() { return cbTipo; }

    public void setPresupuestoTotal(String s) { lblPresupuestoTotal.setText(s); }
    public void setImporteConsumido(String s) { lblImporteConsumido.setText(s); }
    public void setPorcentaje(String s) { lblPorcentaje.setText(s + "%"); }

    // Setters for selected-type metrics
    public void setNumIncidenciasTipo(String s) { lblNumIncidenciasTipo.setText(s); }
    public void setCosteMedioTipo(String s) { lblCosteMedioTipo.setText(s); }
    public void setCosteTotalTipo(String s) { lblCosteTotalTipo.setText(s); }
}