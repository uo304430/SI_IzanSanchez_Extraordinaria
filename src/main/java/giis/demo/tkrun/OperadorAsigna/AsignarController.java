package giis.demo.tkrun.OperadorAsigna;

import giis.demo.util.SwingUtil;
import giis.demo.util.ApplicationException;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;

import javax.swing.JOptionPane;
import java.util.List;

public class AsignarController {
    private AsignarModel model;
    private AsignarView view;
    private String operadorIdentificacion;

    public AsignarController(AsignarModel m, AsignarView v, String operadorIdentificacion) {
        this.model = m;
        this.view = v;
        this.operadorIdentificacion = operadorIdentificacion;
        initView();
        initController();
    }

    public void initView() {
        try {
            List<IncidenciaDTO> incidencias = model.getIncidenciasParaAsignar(operadorIdentificacion);
            List<UsuarioDTO> tecnicos = model.getTecnicos();
            view.populateIncidencias(incidencias);
            view.populateTecnicos(tecnicos);
        } catch (Exception e) {
            throw new ApplicationException("Error inicializando incidencias para asignar: " + e.getMessage());
        }
        view.getFrame().setVisible(true);
    }

    public void initController() {
        view.getBtnAsignar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> asignarSeleccionada()));
    }

    private void asignarSeleccionada() {
        int idInc = view.getSelectedIncidenciaId();
        int idTec = view.getSelectedTecnicoId();
        if (idInc == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione una incidencia para asignar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (idTec == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione un técnico para asignar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(view.getFrame(), "¿Confirma que desea asignar la incidencia seleccionada al técnico?", "Confirmar asignación", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        model.asignarIncidencia(idInc, idTec, operadorIdentificacion);
        List<IncidenciaDTO> incidencias = model.getIncidenciasParaAsignar(operadorIdentificacion);
        view.populateIncidencias(incidencias);
        JOptionPane.showMessageDialog(view.getFrame(), "Incidencia asignada correctamente y registrado en el historial.", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }
}
