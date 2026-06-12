package giis.demo.tkrun.paqueteria.entregas;

import giis.demo.tkrun.paqueteria.login.SesionUsuario;
import giis.demo.util.SwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.util.List;

public class EntregasController {

    private static final Logger log = LoggerFactory.getLogger(EntregasController.class);

    private final EntregasView view;
    private final EntregasModel model;
    private List<EntregaListadoDto> entregasActuales;

    public EntregasController(EntregasView view, EntregasModel model) {
        this.view  = view;
        this.model = model;

        SesionUsuario sesion = SesionUsuario.getInstance();
        view.setCabecera(sesion.getCodigoPunto());

        cargarListado();
        initListeners();
        view.setVisible(true);
    }

    private void cargarListado() {
        int idVehiculo = SesionUsuario.getInstance().getIdVehiculoHabitual();
        entregasActuales = model.getEntregasAsignadas(idVehiculo);
        view.poblarTabla(entregasActuales);
        log.debug("Listado cargado: {} entregas asignadas al vehiculo {}", entregasActuales.size(), idVehiculo);
    }

    private void initListeners() {
        view.getBtnRegistrar().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::accionRegistrar));
        view.getBtnRefrescar().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::cargarListado));
        view.getBtnCerrar().addActionListener(e -> view.dispose());
    }

    private void accionRegistrar() {
        int fila = view.getFilaSeleccionada();
        if (fila < 0) {
            JOptionPane.showMessageDialog(view,
                    "Seleccione una entrega de la tabla.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        EntregaListadoDto entrega = entregasActuales.get(fila);
        ConfirmacionEntregaView confirmView = new ConfirmacionEntregaView(view, entrega);
        new ConfirmacionEntregaController(confirmView, model, entrega, this::cargarListado);
    }
}
