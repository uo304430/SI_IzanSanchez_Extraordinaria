package Izan_33804;

import giis.demo.util.SwingUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CambioEstadoController {
    private CambioEstadoModel model;
    private CambioEstadoView view;
    private String email;

    public CambioEstadoController(CambioEstadoModel m, CambioEstadoView v, String email) {
        this.model = m;
        this.view = v;
        this.email = email;
    }

    public void initController() {
        // 1. Botón para cargar/refrescar la lista
        view.getBtnCargar().addActionListener(e -> listar());

        // 2. Botón para confirmar la planificación 
        view.getBtnPlanificar().addActionListener(e -> {
            System.out.println("DEBUG: Intentando confirmar planificación...");
            planificar();
        });

        // 3. Listener del ratón para capturar el ID de la fila seleccionada
        view.getTablaIncidencias().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // SwingUtil obtiene la clave (ID) de la fila pinchada
                String id = giis.demo.util.SwingUtil.getSelectedKey(view.getTablaIncidencias());
                System.out.println("DEBUG: Fila seleccionada con ID: " + id);
                
                // Pasamos el ID al cuadro de texto para que el controlador lo use
                if (id != null) {
                    view.getTxtId().setText(id);
                }
            }
        });

        
        view.getFrame().setVisible(true);
        listar(); 
    }

    private void listar() {
        var lista = model.getIncidenciasAsignadas(email);
        view.getTablaIncidencias().setModel(SwingUtil.getTableModelFromPojos(lista, new String[]{"id", "descripcion", "estado"}));
    }

    private void planificar() {
        try {
            String idText = view.getTxtId().getText();
            String horas = view.getTxtHoras().getText();
            String trabajos = view.getTxtTrabajos().getText();

            // Verificamos que los campos no estén vacíos en la consola
            System.out.println("DEBUG: Procesando ID=" + idText + ", Horas=" + horas);

            if (idText.isEmpty() || horas.isEmpty()) {
                giis.demo.util.SwingUtil.showMessage("ID y Horas son obligatorios", "Aviso", 2);
                return;
            }

            int id = Integer.parseInt(idText);
            
            // Llamada al modelo
            model.planificarIncidencia(id, horas, trabajos);
            
           
            giis.demo.util.SwingUtil.showMessage("¡Planificación Guardada! Estado actualizado a 'En proceso'.", "Éxito", 1);
            
            listar(); 
            
        } catch (NumberFormatException nfe) {
            giis.demo.util.SwingUtil.showMessage("El ID debe ser un número válido", "Error", 0);
        } catch (Exception ex) {
            ex.printStackTrace(); // Crucial: mira la consola de Eclipse para ver el error real
            giis.demo.util.SwingUtil.showMessage("Error SQL: " + ex.getMessage(), "Error", 0);
        }
    }
}