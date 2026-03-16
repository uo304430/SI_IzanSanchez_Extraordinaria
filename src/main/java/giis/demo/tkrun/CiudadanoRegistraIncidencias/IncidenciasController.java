package giis.demo.tkrun.CiudadanoRegistraIncidencias;

import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;

import javax.swing.JOptionPane;

/**
 * Controlador para la pantalla de registro de incidencias.
 */
public class IncidenciasController {
    private IncidenciasModel model;
    private IncidenciasView view;
    private String identificacion;

    public IncidenciasController(IncidenciasModel m, IncidenciasView v, String identificacion) {
        this.model = m;
        this.view = v;
        this.identificacion = identificacion;
        this.initView();
        this.initController();
    }

    public void initController() {
        // El nombre de los getters en la vista puede variar; ajustar si es necesario
        view.getBtnRegistrar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> registrarDesdeView()));
    }

    public void initView() {
        // Inicialización de la vista (si procede)
        // primero poblar los tipos desde el modelo
        try {
            view.populateTipos(model.getAllTipos());
        } catch (Exception e) {
            // si hay error, envolver en ApplicationException para que SwingUtil lo muestre
            throw new ApplicationException("Error cargando tipos: " + e.getMessage());
        }
        view.getFrame().setVisible(true);
    }

    /**
     * Lee datos de la vista, invoca al modelo y muestra confirmación.
     * Ajusta los nombres de getters/setters de la view si fuese necesario.
     */
    public void registrarDesdeView() {
        String identificador = identificacion == null ? "" : identificacion.trim();
        // if (identificador == null || identificador.isEmpty()) {
        //     JOptionPane.showMessageDialog(view.getFrame(), "Debe indicar el email o DNI del ciudadano.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        //     return;
        // }
        int idTipo = view.getSelectedTipoId();
        if (idTipo <= 0) {
            JOptionPane.showMessageDialog(view.getFrame(), "Debe seleccionar un tipo de incidencia.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String descripcion = view.getDescripcion().trim();
        String localizacion = view.getLocalizacion().trim();

        IncidenciaDTO dto = model.registrarIncidencia(identificador, idTipo, descripcion, localizacion);

        // Mostrar confirmación en la vista (método a implementar en la vista)
        view.showConfirmation(dto);

        // Opcional: limpiar campos
        view.clearForm();
    }
}