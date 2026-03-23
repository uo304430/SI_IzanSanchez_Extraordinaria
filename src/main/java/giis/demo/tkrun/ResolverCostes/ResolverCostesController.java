package giis.demo.tkrun.ResolverCostes;

import java.util.List;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;

public class ResolverCostesController {
    private final ResolverCostesModel model;
    private final ResolverCostesView view;
    private final int incidenciaId;
    private final String tecnicoIdentificacion;
    private final Runnable onSuccess;

    public ResolverCostesController(ResolverCostesModel m, ResolverCostesView v, int incidenciaId, String tecnicoIdentificacion, Runnable onSuccess) {
        this.model = m;
        this.view = v;
        this.incidenciaId = incidenciaId;
        this.tecnicoIdentificacion = tecnicoIdentificacion;
        this.onSuccess = onSuccess;
        initController();
        view.getTxtId().setText(String.valueOf(incidenciaId));
        view.getFrame().setVisible(true);
    }

    private void initController() {
        view.getBtnAddMaterial().addActionListener(e -> SwingUtil.exceptionWrapper(() -> addMaterial()));
        view.getBtnRemoveMaterial().addActionListener(e -> SwingUtil.exceptionWrapper(() -> view.removeSelectedMaterial()));
        view.getBtnConfirmar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> confirmar()));
        view.getBtnCancelar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> cancelar()));
        // update total when fields change (simple approach: on focus loss)
        view.getTxtHoras().addActionListener(e -> view.updateTotal());
        view.getTxtCosteHora().addActionListener(e -> view.updateTotal());
    }

    private void addMaterial() {
        String nombre = view.getTxtMatNombre().getText();
        String costeStr = view.getTxtMatCoste().getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ApplicationException("Nombre del material obligatorio.");
        }
        double coste = 0.0;
        try { coste = Double.parseDouble(costeStr); } catch (Exception ex) { throw new ApplicationException("Coste de material inválido."); }
        view.addMaterial(nombre.trim(), coste);
        view.getTxtMatNombre().setText("");
        view.getTxtMatCoste().setText("");
    }

    private void confirmar() {
        int horas = 0;
        double costeHora = 0.0;
        try { horas = Integer.parseInt(view.getTxtHoras().getText()); } catch (Exception ex) { throw new ApplicationException("Horas inválidas."); }
        try { costeHora = Double.parseDouble(view.getTxtCosteHora().getText()); } catch (Exception ex) { throw new ApplicationException("Coste por hora inválido."); }

        List<ResolverCostesModel.Material> materiales = view.getMateriales();
        model.resolverIncidencia(incidenciaId, tecnicoIdentificacion, horas, costeHora, materiales);
        view.getFrame().dispose();
        if (onSuccess != null) onSuccess.run();
    }

    private void cancelar() {
        view.getFrame().dispose();
    }
}
