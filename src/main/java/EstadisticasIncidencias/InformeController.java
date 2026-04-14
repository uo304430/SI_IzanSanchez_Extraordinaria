package EstadisticasIncidencias;

import giis.demo.util.SwingUtil;
import java.util.List;
import java.time.LocalDate;
import javax.swing.JOptionPane;

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
            // Validar fechas
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

            // Obtenemos los datos filtrados
            List<IncidenciaReporteDTO> lista = model.getInforme(
                view.getDesde(), 
                view.getHasta(),
                view.getCbTipo().getSelectedItem().toString(),
                view.getCbEstado().getSelectedItem().toString(),
                view.getZona() // Ahora obtiene el String del JComboBox de la vista
            );
            
            // 3. Actualizamos la tabla añadiendo la columna "tipo" y la columna "zona" al final
            view.getTabla().setModel(SwingUtil.getTableModelFromPojos(lista, 
                new String[]{"id", "fecha", "descripcion", "tipo", "estado", "zona"}));
        });
        
        view.getFrame().setVisible(true);
    }
}