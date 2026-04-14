package giis.demo.tkrun.ResolverCostes;

import java.util.List;

import Izan_33804.IncidenciaDisplayDTO;
import giis.demo.util.SwingUtil;

public class ResolverCostesSelectorController {
    private final ResolverCostesModel model = new ResolverCostesModel();
    private final ResolverCostesSelectorView view;
    private final String tecnicoIdentificacion;

    public ResolverCostesSelectorController(ResolverCostesSelectorView v, String tecnicoIdentificacion) {
        this.view = v;
        this.tecnicoIdentificacion = tecnicoIdentificacion;
        initController();
        view.getFrame().setVisible(true);
        listar();
    }

    private void initController() {
        view.getBtnActualizar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> listar()));
        view.getBtnCerrar().addActionListener(e -> view.getFrame().dispose());
        view.getBtnResolver().addActionListener(e -> SwingUtil.exceptionWrapper(() -> resolverSeleccionada()));
    }

    private void listar() {
        List<IncidenciaDisplayDTO> lista = model.getIncidenciasAsignadas(tecnicoIdentificacion);
        System.out.println("tecnicoIdentificacion: " + tecnicoIdentificacion);
        view.getTabla().setModel(SwingUtil.getTableModelFromPojos(lista, new String[]{"id","descripcion","tipo","localizacion","usuario","tecnico","coste","descrReparacion","fecha","estado","validacion"}));
        SwingUtil.autoAdjustColumns(view.getTabla());
    }

    private void resolverSeleccionada() {
        String id = SwingUtil.getSelectedKey(view.getTabla());
        if (id == null || id.isEmpty()) {
            SwingUtil.showMessage("Seleccione una incidencia para resolver.", "Aviso", 2);
            return;
        }
        int incidenciaId = Integer.parseInt(id);
        // abrir ventana de resolver costes
        new ResolverCostesController(new ResolverCostesModel(), new ResolverCostesView(), incidenciaId, tecnicoIdentificacion, () -> listar());
    }
}
