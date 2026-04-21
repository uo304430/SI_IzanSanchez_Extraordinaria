package giis.demo.tkrun.CiudadanoConsulataIncidencias;

import giis.demo.util.SwingUtil;
import giis.demo.util.ApplicationException;
import giis.demo.tkrun.DTOs.IncidenciaDTO;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.Map;

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
        view.getBtnReabrir().addActionListener(e -> SwingUtil.exceptionWrapper(() -> reabrirIncidenciaSeleccionada()));
        view.addSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            IncidenciaDTO incidencia = view.getSelectedIncidencia();
            view.updateReabrirButton(incidencia != null && model.isReabrible(incidencia.getEstado()));
        });
    }

    public void consultarDesdeView() {
        String identificador = identificacion == null ? "" : identificacion.trim();
        int estadoId = view.getSelectedEstadoId();
        List<IncidenciaDTO> resultados = model.getIncidenciasByUsuario(identificador, Integer.valueOf(estadoId));
        Map<Integer, String> motivosFinales = model.getMotivosFinales(resultados);
        view.populateTable(resultados, motivosFinales);
        if (resultados == null || resultados.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "No hay incidencias para este usuario/estado.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void reabrirIncidenciaSeleccionada() {
        IncidenciaDTO incidencia = view.getSelectedIncidencia();
        if (incidencia == null) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione una incidencia para reabrir.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!model.isReabrible(incidencia.getEstado())) {
            JOptionPane.showMessageDialog(view.getFrame(), "Solo se pueden reabrir incidencias cerradas o rechazadas.", "Operación no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String motivo = view.getMotivoReapertura();
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ApplicationException("Debe indicar el motivo de la reapertura.");
        }

        int confirmacion = JOptionPane.showConfirmDialog(view.getFrame(),
                "Se reabrirá la incidencia " + incidencia.getId() + " y volverá al estado Nueva.\n¿Desea continuar?",
                "Confirmar reapertura", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        model.reabrirIncidencia(incidencia.getId().intValue(), identificacion, motivo);
        view.clearMotivoReapertura();
        consultarDesdeView();
        JOptionPane.showMessageDialog(view.getFrame(), "La incidencia se ha reabierto correctamente.", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }
}
