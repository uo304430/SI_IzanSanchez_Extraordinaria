package giis.demo.tkrun.GestorEconomicoDefinePresupuesto;

import java.util.List;

import javax.swing.JOptionPane;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;

public class DefinePresupuestoControler {
    private final DefinePresupuestoModel model;
    private final DefinePresupuestoView view;
    private final String identificacion;

    public DefinePresupuestoControler(DefinePresupuestoModel model, DefinePresupuestoView view, String identificacion) {
        this.model = model;
        this.view = view;
        this.identificacion = identificacion;
        initView();
        initController();
    }

    private void initView() {
        try {
            view.populateTipos(model.getTiposIncidencia());
            recargarPresupuestos();
            view.setVisible(true);
        } catch (Exception e) {
            throw new ApplicationException("Error inicializando la definición de presupuestos: " + e.getMessage());
        }
    }

    private void initController() {
        view.getBtnGuardar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> guardarPresupuesto()));
        view.getRbAnual().addActionListener(e -> view.updateVigenciaMode());
        view.getRbFechas().addActionListener(e -> view.updateVigenciaMode());
    }

    private void guardarPresupuesto() {
        model.definirPresupuesto(identificacion, view.getSelectedTipoId(), view.getImporte(),
                view.isVigenciaAnual(), view.getAnio(), view.getFechaInicio(), view.getFechaFin());
        recargarPresupuestos();
        view.clearForm();
        JOptionPane.showMessageDialog(view.getFrame(),
                "Presupuesto definido correctamente. El importe consumido se ha registrado a 0.",
                "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void recargarPresupuestos() {
        List<Object[]> presupuestos = model.getPresupuestos();
        view.populatePresupuestos(presupuestos);
    }
}
