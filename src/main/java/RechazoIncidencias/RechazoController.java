package RechazoIncidencias;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import giis.demo.util.SwingUtil;
import java.util.List;

public class RechazoController {
    private RechazoModel model;
    private RechazoView view;
    private String identificacion; // nuevo: identificador del usuario que realiza la acción

    // Constructor con 2 parámetros (retrocompatible)
    public RechazoController(RechazoModel m, RechazoView v) {
        this(m, v, null);
    }

    // Nuevo constructor que recibe la identificación (email o dni)
    public RechazoController(RechazoModel m, RechazoView v, String identificacion) {
        this.model = m;
        this.view = v;
        this.identificacion = identificacion;
    }

    public void cargarDatos() {
        try {
            List<Object[]> lista = model.getListaPendientes();

            // Si la lista contiene arrays de objetos, construimos un DefaultTableModel manualmente
            String[] cols = new String[]{"id", "fecha", "descripcion", "estado"};
            if (lista == null || lista.isEmpty()) {
                view.getTabla().setModel(new DefaultTableModel(cols, 0));
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

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void ejecutarRechazo() {
        int fila = view.getTabla().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Debes seleccionar una incidencia de la tabla.");
            return;
        }

        String id = view.getTabla().getValueAt(fila, 0).toString();

        // Read motivo from inline text area instead of popup
        String motivo = view.getMotivoText();

        if (motivo != null && motivo.trim().length() >= 10) {
            // Usamos la versión del modelo que acepta la identificación del usuario
            model.actualizarRechazo(new RechazoDTO(id, motivo), this.identificacion);
            JOptionPane.showMessageDialog(null, "Incidencia " + id + " rechazada con éxito.");
            view.clearMotivo(); // Limpiar el motivo tras el rechazo
            cargarDatos(); // Recargamos para que desaparezca de la lista de pendientes
        } else {
            JOptionPane.showMessageDialog(null, "Error: El motivo debe ser más descriptivo (min. 10 carac.)");
        }
    }
}