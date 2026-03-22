package EstadisticasIncidencias;

import giis.demo.util.SwingUtil;
import java.util.List;

public class InformeController {
    private InformeModel model;
    private InformeView view;

    public InformeController(InformeModel m, InformeView v) {
        this.model = m;
        this.view = v;
    }

    public void initController() {
        // 1. Poblamos los desplegables desde la DB al arrancar
        model.getFiltros("Tipos").forEach(t -> view.getCbTipo().addItem(t));
        model.getFiltros("Estados").forEach(e -> view.getCbEstado().addItem(e));
        
        // Nuevo: Cargar el desplegable de Zonas usando el método del modelo
     // Usamos el método específico que creamos para las zonas
        model.getListaZonas().forEach(z -> view.getCbZona().addItem(z));

        // 2. Definimos la acción del botón Generar
        view.getBtnGenerar().addActionListener(e -> {
            // Obtenemos los datos filtrados
            List<IncidenciaReporteDTO> lista = model.getInforme(
                view.getDesde(), 
                view.getHasta(),
                view.getCbTipo().getSelectedItem().toString(),
                view.getCbEstado().getSelectedItem().toString(),
                view.getZona() // Ahora obtiene el String del JComboBox de la vista
            );
            
            // 3. Actualizamos la tabla añadiendo la columna "zona" al final
            view.getTabla().setModel(SwingUtil.getTableModelFromPojos(lista, 
                new String[]{"id", "fecha", "descripcion", "estado", "zona"}));
        });
        
        view.getFrame().setVisible(true);
    }
}
