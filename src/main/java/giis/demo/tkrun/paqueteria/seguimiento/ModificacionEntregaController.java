package giis.demo.tkrun.paqueteria.seguimiento;

import giis.demo.tkrun.paqueteria.login.SesionUsuario;
import giis.demo.util.SwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;

public class ModificacionEntregaController {

    private static final Logger log = LoggerFactory.getLogger(ModificacionEntregaController.class);

    private final ModificacionEntregaView view;
    private final ModificacionEntregaModel model;
    private final ModificacionEntregaDto contexto;

    /** Callback que ejecuta SeguimientoDetalleController al cerrar el dialogo con exito. */
    private final Runnable onExito;

    public ModificacionEntregaController(ModificacionEntregaView view,
                                          ModificacionEntregaModel model,
                                          int idEnvio,
                                          Runnable onExito) {
        this.view    = view;
        this.model   = model;
        this.onExito = onExito;
        this.contexto = model.cargarContexto(idEnvio);

        if (contexto == null) {
            JOptionPane.showMessageDialog(view,
                    "No se ha podido cargar el envio seleccionado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            view.dispose();
            return;
        }

        precargar();
        initListeners();
        view.setVisible(true);
    }

    private void precargar() {
        String[] datosDestinatario = cargarNombreTelefonoDestinatario(contexto.getIdEnvio());
        view.precargarPanelA(datosDestinatario[0], datosDestinatario[1]);
        // contexto.getNuevaCiudad() contiene el horario del punto destino (cargado en cargarContexto)
        view.cargarPanelB(contexto.getCodigoPuntoDestinoActual(), contexto.getNuevaCiudad());
    }

    private String[] cargarNombreTelefonoDestinatario(int idEnvio) {
        String[] result = model.obtenerNombreTelefonoDestinatario(idEnvio);
        return result != null ? result : new String[]{"", ""};
    }

    private void initListeners() {
        view.getRbOpcionA().addActionListener(e -> view.mostrarPanelOpcion('A'));
        view.getRbOpcionB().addActionListener(e -> view.mostrarPanelOpcion('B'));

        view.getBtnCancelar().addActionListener(e -> view.dispose());

        view.getBtnConfirmar().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::accionConfirmar));
    }

    private void accionConfirmar() {
        char opcion = view.getRbOpcionA().isSelected() ? 'A' : 'B';

        if (opcion == 'A') {
            // Validar campos obligatorios
            String nombre  = view.getTxtNombreDestinatario().getText().trim();
            String tlf     = view.getTxtTelefonoDestinatario().getText().trim();
            String dir     = view.getTxtDireccion().getText().trim();
            String ciudad  = view.getTxtCiudad().getText().trim();
            String cp      = view.getTxtCodigoPostal().getText().trim();

            if (nombre.isEmpty() || tlf.isEmpty() || dir.isEmpty() || ciudad.isEmpty() || cp.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Debe completar todos los campos para cambiar la direccion.",
                        "Campos obligatorios", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar zona
            if (!model.esMismaZona(contexto.getIdEnvio(), cp)) {
                JOptionPane.showMessageDialog(view,
                        "La nueva direccion queda fuera de la zona de cobertura del punto de destino.\n"
                        + "Para cambiar de zona, cancele el envio y registre uno nuevo, "
                        + "o seleccione la opcion de recogida en oficina.",
                        "Zona fuera de cobertura", JOptionPane.WARNING_MESSAGE);
                return;
            }

            contexto.setOpcion('A');
            contexto.setNuevoNombreDestinatario(nombre);
            contexto.setNuevoTelefonoDestinatario(tlf);
            contexto.setNuevaDireccion(dir);
            contexto.setNuevaCiudad(ciudad);
            contexto.setNuevoCodigoPostal(cp);
        } else {
            contexto.setOpcion('B');
        }

        // Confirmacion final
        int resp = JOptionPane.showConfirmDialog(view,
                "¿Confirma la modificacion del lugar de entrega?\n"
                + "Esta accion consumira 1 de las 3 modificaciones permitidas.",
                "Confirmar modificacion", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        int idUsuario = SesionUsuario.getInstance().getIdUsuario();
        model.aplicarModificacion(contexto, idUsuario);

        log.info("Modificacion de entrega aplicada: envio={}, opcion={}", contexto.getIdEnvio(), contexto.getOpcion());

        JOptionPane.showMessageDialog(view,
                "Modificacion registrada con exito.",
                "Exito", JOptionPane.INFORMATION_MESSAGE);

        view.dispose();
        if (onExito != null) onExito.run();
    }
}
