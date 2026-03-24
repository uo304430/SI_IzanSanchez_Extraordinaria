package Izan_33804;

import giis.demo.util.SwingUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import RechazoIncidencias.RechazoController;
import RechazoIncidencias.RechazoModel;
import RechazoIncidencias.RechazoView;

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

        // 4. Nuevo: botón para abrir la ventana de Rechazo desde la vista de Planificar
        view.getBtnRechazar().addActionListener(e -> {
            System.out.println("DEBUG: Abriendo gestor de Rechazos desde Planificar (identificador: " + email + ")");
            RechazoModel rm = new RechazoModel();
            RechazoView rv = new RechazoView();
            RechazoController rc = new RechazoController(rm, rv, this.email);

            // Conectamos los botones de la vista de Rechazo con el controlador
            rv.getBtnCargar().addActionListener(ev -> rc.cargarDatos());
            rv.getBtnRechazar().addActionListener(ev -> rc.ejecutarRechazo());

            rv.getFrame().setVisible(true);
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

            System.out.println("DEBUG: Procesando ID=" + idText + ", Horas=" + horas);

            if (idText.isEmpty() || horas.isEmpty()) {
                giis.demo.util.SwingUtil.showMessage("ID y Horas son obligatorios", "Aviso", 2);
                return;
            }

            int id = Integer.parseInt(idText);
            
            // LA CORRECCIÓN ESTÁ AQUÍ:
            // Añadimos 'this.email' como cuarto parámetro para que coincida con el Modelo
            model.planificarIncidencia(id, horas, trabajos, this.email);
            
            giis.demo.util.SwingUtil.showMessage("¡Planificación Guardada! Estado actualizado a 'En proceso'.", "Éxito", 1);
            
            listar(); 
            
        } catch (NumberFormatException nfe) {
            giis.demo.util.SwingUtil.showMessage("El ID debe ser un número válido", "Error", 0);
        } catch (Exception ex) {
            ex.printStackTrace(); 
            giis.demo.util.SwingUtil.showMessage("Error SQL: " + ex.getMessage(), "Error", 0);
        }
    }
}