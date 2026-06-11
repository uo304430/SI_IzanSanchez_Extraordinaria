package giis.demo.tkrun.paqueteria.seguimiento;

import giis.demo.util.SwingUtil;

import javax.swing.*;
import java.util.List;

public class SeguimientoDetalleController {

    private final SeguimientoDetalleView view;
    private final SeguimientoModel model;
    private final int idEnvio;

    public SeguimientoDetalleController(SeguimientoDetalleView view, SeguimientoModel model, int idEnvio) {
        this.view    = view;
        this.model   = model;
        this.idEnvio = idEnvio;
        cargarDatos();
        initListeners();
        view.setVisible(true);
    }

    private void cargarDatos() {
        EnvioDetalleDto detalle = model.getEnvioDetalle(idEnvio);
        if (detalle == null) return;

        view.poblarDatosGenerales(detalle);

        List<TramoListadoDto> tramos = model.getTramosDeEnvio(idEnvio);
        view.poblarTramos(tramos);

        List<EventoHistorialDto> historial = model.getHistorialDeEnvio(idEnvio);
        view.poblarHistorial(historial);

        List<IncidenciaListadoDto> incidencias = model.getIncidenciasDeEnvio(idEnvio);
        view.poblarIncidencias(incidencias);
    }

    private void initListeners() {
        view.getBtnModificarEntrega().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::accionModificarEntrega));
        view.getBtnCancelarEnvio().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::accionCancelarEnvio));
        view.getBtnCerrar().addActionListener(e -> view.dispose());
    }

    private void accionModificarEntrega() {
        JOptionPane.showMessageDialog(view,
                "La modificacion del lugar de entrega estara disponible en HU-05.",
                "Funcionalidad pendiente", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionCancelarEnvio() {
        JOptionPane.showMessageDialog(view,
                "La cancelacion de envios estara disponible en HU-22 (fuera del alcance actual).",
                "Funcionalidad no disponible", JOptionPane.INFORMATION_MESSAGE);
    }
}
