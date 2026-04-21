package giis.demo.tkrun.OperadorValidaIncidencias;

import giis.demo.util.SwingUtil;
import giis.demo.util.ApplicationException;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.OperarioRechazaIncidencia.RechazarOperarioController;
import giis.demo.tkrun.OperarioRechazaIncidencia.RechazarOperarioModel;
import giis.demo.tkrun.OperarioRechazaIncidencia.RechazarOperarioView;

import javax.swing.JOptionPane;
import java.util.List;

public class ValidarControler {
    private ValidarModel model;
    private ValidarView view;
    private String operadorIdentificacion; 

    public ValidarControler(ValidarModel m, ValidarView v, String operadorIdentificacion) {
        this.model = m;
        this.view = v;
        this.operadorIdentificacion = operadorIdentificacion;
        initView();
        initController();
    }

    public void initView() {
        try {
            view.populateTipos(model.getTiposIncidencia());
            List<IncidenciaDTO> incidencias = model.getIncidenciasPorValidar(operadorIdentificacion);
            view.populateTable(incidencias);
        } catch (Exception e) {
            throw new ApplicationException("Error inicializando incidencias para validar: " + e.getMessage());
        }
        view.getFrame().setVisible(true);
    }

    public void initController() {
        view.getBtnValidar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> validarSeleccionada()));
        view.getBtnRechazar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> rechazarSeleccionada()));
    }

    private void validarSeleccionada() {
        IncidenciaDTO seleccionada = view.getSelectedIncidencia();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione una incidencia para validar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int tipoSeleccionado = view.getSelectedTipoId();
        if (tipoSeleccionado == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione un tipo válido para la incidencia.", "Tipo no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipoActual = seleccionada.getTipoNombre();
        Object tipoElegido = view.getCbTipos().getSelectedItem();
        String tipoDestino = tipoElegido == null ? "" : tipoElegido.toString();
        int r = JOptionPane.showConfirmDialog(view.getFrame(), "¿Confirma que desea validar la incidencia seleccionada?", "Confirmar validación", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        model.validarIncidencia(seleccionada.getId(), tipoSeleccionado, operadorIdentificacion);
        // recargar lista
        List<IncidenciaDTO> incidencias = model.getIncidenciasPorValidar(operadorIdentificacion);
        view.populateTable(incidencias);
        String mensaje = "Incidencia validada correctamente.\nTipo anterior: " + tipoActual + "\nTipo validado: " + tipoDestino;
        JOptionPane.showMessageDialog(view.getFrame(), mensaje, "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void rechazarSeleccionada() {
        int id = view.getSelectedIncidenciaId();
        if (id == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione una incidencia para rechazar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RechazarOperarioModel rm = new RechazarOperarioModel();
        RechazarOperarioView rv = new RechazarOperarioView();
        Runnable refresh = () -> {
            List<IncidenciaDTO> incidencias = model.getIncidenciasPorValidar(operadorIdentificacion);
            view.populateTable(incidencias);
        };
        new RechazarOperarioController(rm, rv, id, operadorIdentificacion, refresh);
    }
}
