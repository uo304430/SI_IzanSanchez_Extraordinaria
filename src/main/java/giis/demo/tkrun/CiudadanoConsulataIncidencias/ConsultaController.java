package giis.demo.tkrun.CiudadanoConsulataIncidencias;

import giis.demo.util.SwingUtil;
import giis.demo.util.ApplicationException;
import giis.demo.tkrun.DTOs.IncidenciaDTO;

import javax.swing.JOptionPane;
import java.util.List;

/**
 * Controlador para la pantalla de consulta de incidencias.
 */
public class ConsultaController {
    private ConsultaModel model;
    private ConsultaView view;
    private String identificacion;

    public ConsultaController(ConsultaModel m, ConsultaView v, String identificacion) {
        this.model = m;
        this.view = v;
        this.identificacion = identificacion;
        this.initView();
        this.initController();
    }

    public void initView() {
        try {
            view.populateEstados(model.getAllEstados());
        } catch (Exception e) {
            throw new ApplicationException("Error inicializando estados: " + e.getMessage());
        }
        view.getFrame().setVisible(true);
    }

    public void initController() {
        view.getBtnConsultar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> consultarDesdeView()));
    }

    public void consultarDesdeView() {
        String identificador = identificacion == null ? "" : identificacion.trim();
        int estadoId = view.getSelectedEstadoId();
        List<IncidenciaDTO> resultados = model.getIncidenciasByUsuario(identificador, Integer.valueOf(estadoId));
        view.populateTable(resultados);
        if (resultados == null || resultados.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "No hay incidencias para este usuario/estado.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
