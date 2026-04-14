package InformeEconomico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.*;
import java.time.LocalDate;
import java.io.PrintWriter;
import java.io.StringWriter;
import giis.demo.util.Database;

public class InformeEconomicoController {
    private InformeEconomicoModel model;
    private InformeEconomicoView view;
    private Database db = new Database();

    public InformeEconomicoController(InformeEconomicoModel model, InformeEconomicoView view) {
        this.model = model;
        this.view = view;
        try {
            db.createDatabase(true);
            db.loadDatabase();
            populateTipos();
            attachHandlers();
            SwingUtilities.invokeLater(() -> view.getFrame().setVisible(true));
        } catch (Exception ex) {
            showException("Error inicializando Informe Económico", ex);
        }
    }

    private void populateTipos() {
        // Load tipos from DB
        List<Map<String, Object>> res = db.executeQueryMap("SELECT nombre FROM Tipos");
        JComboBox<String> cb = view.getCbTipo();
        cb.addItem("Todos");
        for (Map<String, Object> fila : res) {
            Object v = fila.get("nombre");
            if (v != null) cb.addItem(v.toString());
        }
    }

    private void attachHandlers() {
        view.getBtnGenerar().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    generarInforme();
                } catch (Exception ex) {
                    showException("Error al generar informe económico", ex);
                }
            }
        });
    }

    private void generarInforme() {
        // Validate dates using DatePicker values from the view
        LocalDate desdeLD = view.getDesdeLocalDate();
        LocalDate hastaLD = view.getHastaLocalDate();
        if (desdeLD == null || hastaLD == null) {
            JOptionPane.showMessageDialog(view.getFrame(), "Debe seleccionar ambas fechas (inicio y fin).", "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!desdeLD.isBefore(hastaLD)) {
            JOptionPane.showMessageDialog(view.getFrame(), "La fecha de inicio debe ser anterior a la fecha de fin.", "Rango de fechas inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String desde = desdeLD.toString();
        String hasta = hastaLD.toString();
        String tipoSeleccionado = view.getCbTipo().getSelectedItem().toString();
        String presupuesto = view.getPresupuesto();

        List<InformeEconomicoDTO> lista = model.getCostesPorTipo(desde, hasta);
        // Si se ha seleccionado un tipo concreto, filtrar la lista
        if (tipoSeleccionado != null && !tipoSeleccionado.equals("Todos")) {
            List<InformeEconomicoDTO> filtered = new ArrayList<>();
            for (InformeEconomicoDTO dto : lista) {
                if (tipoSeleccionado.equals(dto.getTipo())) filtered.add(dto);
            }
            lista = filtered;
        }

        // Actualizar tabla
        DefaultTableModel tm = new DefaultTableModel();
        tm.addColumn("Tipo");
        tm.addColumn("Nº Incidencias");
        tm.addColumn("Coste Medio");
        tm.addColumn("Coste Total");
        for (InformeEconomicoDTO dto : lista) {
            tm.addRow(new Object[]{
                dto.getTipo(),
                dto.getNumeroIncidencias(),
                dto.getCosteMedio(),
                dto.getCosteTotal()
            });
        }
        view.getTablaCostes().setModel(tm);

        // If a single type selected, show its metrics in the dedicated labels
        if (tipoSeleccionado != null && !tipoSeleccionado.equals("Todos") && lista.size() > 0) {
            InformeEconomicoDTO d = lista.get(0);
            view.setNumIncidenciasTipo(d.getNumeroIncidencias());
            view.setCosteMedioTipo(d.getCosteMedio());
            view.setCosteTotalTipo(d.getCosteTotal());
        } else {
            // Clear metrics when multiple types or none
            view.setNumIncidenciasTipo("0");
            view.setCosteMedioTipo("0.00");
            view.setCosteTotalTipo("0.00");
        }

        // Actualizar detalles presupuesto
        Map<String, String> detalles = model.getDetallesPresupuesto(presupuesto);
        view.setPresupuestoTotal(detalles.getOrDefault("presupuestoTotal", "0.00"));
        view.setImporteConsumido(detalles.getOrDefault("importeConsumido", "0.00"));
        view.setPorcentaje(detalles.getOrDefault("porcentajeConsumido", "0.00"));
    }

    private void showException(String title, Exception ex) {
        // Log error to console instead of showing a dialog
        System.err.println(title + ": " + ex.toString());
        ex.printStackTrace();
    }

    // Small main to run the view standalone for quick testing
    public static void main(String[] args) {
        InformeEconomicoModel m = new InformeEconomicoModel();
        InformeEconomicoView v = new InformeEconomicoView();
        new InformeEconomicoController(m, v);
    }
}