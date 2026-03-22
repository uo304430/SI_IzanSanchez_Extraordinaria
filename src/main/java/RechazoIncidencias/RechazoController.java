package RechazoIncidencias;

import javax.swing.JOptionPane;
import giis.demo.util.SwingUtil;

public class RechazoController {
    private RechazoModel model;
    private RechazoView view;

    // Constructor con SOLO 2 parámetros (esto arreglará tu error del Main)
    public RechazoController(RechazoModel m, RechazoView v) {
        this.model = m;
        this.view = v;
    }

    public void cargarDatos() {
        try {
            var lista = model.getListaPendientes();
            // Usamos SwingUtil para pintar la tabla fácilmente
            view.getTabla().setModel(SwingUtil.getTableModelFromPojos(lista, 
                new String[]{"id", "fecha", "descripcion", "estado"}));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos: " + e.getMessage());
        }
    }

    public void ejecutarRechazo() {
        int fila = view.getTabla().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Debes seleccionar una incidencia de la tabla.");
            return;
        }

        String id = view.getTabla().getValueAt(fila, 0).toString();
        
        // Ventana emergente para el motivo (Criterio de aceptación HU)
        String motivo = JOptionPane.showInputDialog(view.getFrame(), 
            "Escriba el motivo del rechazo (Mínimo 10 caracteres):", 
            "Justificación Obligatoria", 
            JOptionPane.WARNING_MESSAGE);

        if (motivo != null && motivo.trim().length() >= 10) {
            model.actualizarRechazo(new RechazoDTO(id, motivo));
            JOptionPane.showMessageDialog(null, "Incidencia " + id + " rechazada con éxito.");
            cargarDatos(); // Recargamos para que desaparezca de la lista de pendientes
        } else if (motivo != null) {
            JOptionPane.showMessageDialog(null, "Error: El motivo debe ser más descriptivo (min. 10 carac.)");
        }
    }
}