package giis.demo.tkrun.paqueteria.entregas;

import giis.demo.tkrun.paqueteria.login.SesionUsuario;
import giis.demo.util.SwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;

public class ConfirmacionEntregaController {

    private static final Logger log = LoggerFactory.getLogger(ConfirmacionEntregaController.class);

    private final ConfirmacionEntregaView view;
    private final EntregasModel model;
    private final EntregaListadoDto entrega;
    private final Runnable onExito;

    public ConfirmacionEntregaController(ConfirmacionEntregaView view, EntregasModel model,
                                          EntregaListadoDto entrega, Runnable onExito) {
        this.view    = view;
        this.model   = model;
        this.entrega = entrega;
        this.onExito = onExito;
        initListeners();
        view.setVisible(true);
    }

    private void initListeners() {
        view.getBtnCompletada().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::accionCompletada));
        view.getBtnFallida().addActionListener(e ->
                view.mostrarPanelFallo());
        view.getBtnVolver().addActionListener(e ->
                view.mostrarPanelPrincipal());
        view.getBtnConfirmarFallo().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::accionConfirmarFallo));
    }

    private void accionCompletada() {
        int resp = JOptionPane.showConfirmDialog(view,
                "¿Confirmar la entrega del envio " + entrega.getCodigoEnvio() + "?",
                "Confirmar entrega", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        int idTransportista = SesionUsuario.getInstance().getIdUsuario();
        ResultadoEntregaDto resultado = model.registrarEntregaCompletada(
                entrega.getIdEnvio(), entrega.getIdTramo(), idTransportista);

        log.info("Entrega completada registrada: envio={}, intento={}",
                entrega.getCodigoEnvio(), resultado.getNumeroIntento());

        JOptionPane.showMessageDialog(view,
                "Entrega registrada con exito. El envio se marca como ENTREGADO.",
                "Entrega completada", JOptionPane.INFORMATION_MESSAGE);

        view.dispose();
        if (onExito != null) onExito.run();
    }

    private void accionConfirmarFallo() {
        String motivoSeleccionado = (String) view.getCmbMotivo().getSelectedItem();
        if (motivoSeleccionado == null || motivoSeleccionado.startsWith("--")) {
            JOptionPane.showMessageDialog(view,
                    "Debe seleccionar un motivo del fallo.",
                    "Motivo obligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comentario = view.getTxtComentario().getText().trim();
        if ("OTROS".equals(motivoSeleccionado) && comentario.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Debe indicar el motivo en el comentario.",
                    "Comentario obligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransportista = SesionUsuario.getInstance().getIdUsuario();
        ResultadoEntregaDto resultado = model.registrarEntregaFallida(
                entrega.getIdEnvio(), entrega.getIdTramo(), idTransportista,
                motivoSeleccionado, comentario);

        String mensaje;
        if (resultado.isEsCuartoFallo()) {
            mensaje = "Cuarto intento fallido. El envio queda depositado en "
                    + resultado.getCodigoPuntoDestino() + " para recogida por el destinatario.";
        } else {
            mensaje = "Intento fallido registrado. Se programara un nuevo intento.";
        }
        JOptionPane.showMessageDialog(view, mensaje,
                "Fallo registrado", JOptionPane.INFORMATION_MESSAGE);

        view.dispose();
        if (onExito != null) onExito.run();
    }
}
