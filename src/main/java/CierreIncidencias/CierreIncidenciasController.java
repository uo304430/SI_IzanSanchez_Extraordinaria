package CierreIncidencias;

import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import giis.demo.util.SwingUtil;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import java.util.List;
import java.util.Map;

public class CierreIncidenciasController {
    private CierreIncidenciasModel model;
    private CierreIncidenciasView view;
    private String identificacion; // usuario que realiza la acción (dni/email/nombre)

    public CierreIncidenciasController(CierreIncidenciasModel m, CierreIncidenciasView v, String identificacion) {
        this.model = m;
        this.view = v;
        this.identificacion = identificacion;
        attachHandlers();
        view.getFrame().setVisible(true);
    }

    private void attachHandlers() {
        view.getBtnCargar().addActionListener(e -> cargarDatos());
        view.getBtnCerrar().addActionListener(e -> ejecutarCierre());
        view.getTabla().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarDetalleSeleccion();
        });
    }

    public void cargarDatos() {
        try {
            List<Object[]> lista = model.getIncidenciasParaCierre();
            String[] cols = new String[]{"id","tipo","descripcion","fecha","coste","tecnico","estado"};
            if (lista == null || lista.isEmpty()) {
                view.getTabla().setModel(new DefaultTableModel(cols, 0));
                clearDetalle();
                return;
            }
            DefaultTableModel tm = new DefaultTableModel(cols, lista.size());
            for (int i = 0; i < lista.size(); i++) {
                Object[] row = lista.get(i);
                for (int j = 0; j < cols.length && j < row.length; j++) {
                    tm.setValueAt(row[j], i, j);
                }
            }
            view.getTabla().setModel(tm);
            clearDetalle();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar incidencias para cierre: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearDetalle() {
        view.getTxtCoste().setText("");
        view.getTxtPresupuesto().setText("");
        view.getTxtConsumido().setText("");
        view.getTxtDisponible().setText("");
    }

    private void actualizarDetalleSeleccion() {
        int fila = view.getTabla().getSelectedRow();
        if (fila == -1) { clearDetalle(); return; }
        try {
            int id = Integer.parseInt(view.getTabla().getValueAt(fila, 0).toString());
            Map<String,Object> datos = model.getIncidenciaTipoCoste(id);
            int tipo = datos != null && datos.get("tipo") != null ? Integer.parseInt(datos.get("tipo").toString()) : -1;
            String coste = datos != null && datos.get("Coste") != null ? datos.get("Coste").toString() : "0";
            view.getTxtCoste().setText(coste);

            Map<String,Object> presupuesto = model.getPresupuestoVigentePorTipo(tipo);
            if (presupuesto == null) {
                view.getTxtPresupuesto().setText("N/A");
                view.getTxtConsumido().setText("N/A");
                view.getTxtDisponible().setText("N/A");
            } else {
                String pres = presupuesto.get("presupuesto")!=null?presupuesto.get("presupuesto").toString():"0";
                String cons = presupuesto.get("consumido")!=null?presupuesto.get("consumido").toString():"0";
                double disponible = 0.0;
                try { disponible = Double.parseDouble(pres) - Double.parseDouble(cons); } catch(Exception e) { disponible = 0.0; }
                view.getTxtPresupuesto().setText(pres);
                view.getTxtConsumido().setText(cons);
                view.getTxtDisponible().setText(String.format("%.2f", disponible));
            }
        } catch(Exception e) {
            clearDetalle();
            e.printStackTrace();
        }
    }

    public void ejecutarCierre() {
        int fila = view.getTabla().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Debes seleccionar una incidencia para cerrar.");
            return;
        }
        int id = Integer.parseInt(view.getTabla().getValueAt(fila, 0).toString());

        // Antes de intentar cerrar, mostramos un diálogo de confirmación con datos clave
        try {
            Map<String,Object> datos = model.getIncidenciaTipoCoste(id);
            int tipo = datos != null && datos.get("tipo")!=null ? Integer.parseInt(datos.get("tipo").toString()) : -1;
            String coste = datos != null && datos.get("Coste")!=null ? datos.get("Coste").toString() : "0";

            Map<String,Object> presup = model.getPresupuestoVigentePorTipo(tipo);
            String presText = "N/A";
            String consText = "N/A";
            String dispText = "N/A";
            if (presup != null) {
                presText = presup.get("presupuesto")!=null?presup.get("presupuesto").toString():"0";
                consText = presup.get("consumido")!=null?presup.get("consumido").toString():"0";
                try { double pres = Double.parseDouble(presText); double cons = Double.parseDouble(consText); dispText = String.format("%.2f", pres - cons); } catch(Exception e) { dispText = "N/A"; }
            }

            String message = "Vas a cerrar la incidencia " + id + "\n" +
                             "Coste imputado: " + coste + "\n" +
                             "Presupuesto vigente: " + presText + "\n" +
                             "Importe consumido: " + consText + "\n" +
                             "Disponible: " + dispText + "\n\n" +
                             "¿Confirmas el cierre de la incidencia?";

            int opt = JOptionPane.showConfirmDialog(view.getFrame(), message, "Confirmar cierre de incidencia", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt != JOptionPane.YES_OPTION) {
                // el técnico canceló la operación
                return;
            }
        } catch(Exception e) {
            // Si algo falla al preparar la confirmación, seguimos con el flujo normal
            e.printStackTrace();
        }

        // Intentar cerrar
        boolean ok = model.cerrarIncidencia(id, this.identificacion);
        if (ok) {
            JOptionPane.showMessageDialog(view.getFrame(), "Incidencia " + id + " cerrada con éxito.");
            cargarDatos();
        } else {
            // obtener datos para mensaje detallado: tipo, coste, presupuesto y disponible
            try {
                java.util.Map<String,Object> datos = model.getIncidenciaTipoCoste(id);
                int tipo = datos != null && datos.get("tipo")!=null ? Integer.parseInt(datos.get("tipo").toString()) : -1;
                String coste = datos != null && datos.get("Coste")!=null ? datos.get("Coste").toString() : "0";
                Map<String,Object> presup = model.getPresupuestoVigentePorTipo(tipo);
                if (presup == null) {
                    JOptionPane.showMessageDialog(view.getFrame(), "No existe presupuesto vigente para esta tipología o hay conflicto; no se puede cerrar.");
                } else {
                    double presupuesto = presup.get("presupuesto") instanceof Number ? ((Number)presup.get("presupuesto")).doubleValue() : Double.parseDouble(presup.get("presupuesto").toString());
                    double consumido = presup.get("consumido") instanceof Number ? ((Number)presup.get("consumido")).doubleValue() : Double.parseDouble(presup.get("consumido").toString());
                    double disponible = presupuesto - consumido;
                    JOptionPane.showMessageDialog(view.getFrame(), "No se puede cerrar la incidencia. Coste ("+coste+") excede el disponible: " + String.format("%.2f", disponible));
                }
            } catch(Exception e) {
                JOptionPane.showMessageDialog(view.getFrame(), "No se pudo validar el presupuesto: " + e.getMessage());
            }
        }
    }
}