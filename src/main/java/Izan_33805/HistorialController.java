package Izan_33805;

import java.util.List;
import giis.demo.util.SwingUtil;

public class HistorialController {
    private HistorialModel model;
    private HistorialView view;

    public HistorialController(HistorialModel m, HistorialView v) {
        this.model = m;
        this.view = v;
    }

    public void initController() {
        // 1. Cargar listado inicial
        cargarListado();

        // 2. Evento de selección en la tabla
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getTable().getSelectedRow();
                if (row != -1) {
                    // Obtenemos el ID de la primera columna
                    int id = Integer.parseInt(view.getTable().getValueAt(row, 0).toString());
                    cargarHistorialDetallado(id);
                }
            }
        });

        view.getFrame().setVisible(true);
    }

    private void cargarListado() {
        List<HistorialDTO> lista = model.getListaSeleccion();
        view.getTable().setModel(SwingUtil.getTableModelFromPojos(lista, new String[]{"id", "descripcion"}));
        view.getFrame().setTitle("Seleccione una incidencia para ver su historial");
    }

    private void cargarHistorialDetallado(int id) {
        List<HistorialDTO> detalles = model.getHistorialPorId(id);
        String[] cols = {"fecha", "id", "descripcion", "estado"};
        view.getTable().setModel(SwingUtil.getTableModelFromPojos(detalles, cols));
        view.getFrame().setTitle("Historial de Incidencia ID: " + id);
    }
}