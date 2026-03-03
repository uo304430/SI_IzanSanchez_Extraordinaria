package giis.demo.tkrun.OperadorValidaIncidencias;

import giis.demo.util.SwingUtil;
import giis.demo.util.ApplicationException;
import giis.demo.tkrun.DTOs.IncidenciaDTO;

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
        int id = view.getSelectedIncidenciaId();
        if (id == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione una incidencia para validar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(view.getFrame(), "¿Confirma que desea validar la incidencia seleccionada?", "Confirmar validación", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        model.validarIncidencia(id, operadorIdentificacion);
        // recargar lista
        List<IncidenciaDTO> incidencias = model.getIncidenciasPorValidar(operadorIdentificacion);
        view.populateTable(incidencias);
        JOptionPane.showMessageDialog(view.getFrame(), "Incidencia validada correctamente.", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void rechazarSeleccionada() {
        int id = view.getSelectedIncidenciaId();
        if (id == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione una incidencia para rechazar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(view.getFrame(), "¿Confirma que desea rechazar (cerrar) la incidencia seleccionada?", "Confirmar rechazo", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        model.rechazarIncidencia(id, operadorIdentificacion);
        List<IncidenciaDTO> incidencias = model.getIncidenciasPorValidar(operadorIdentificacion);
        view.populateTable(incidencias);
        JOptionPane.showMessageDialog(view.getFrame(), "Incidencia rechazada/cerrada correctamente.", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }
}