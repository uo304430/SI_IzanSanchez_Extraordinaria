package giis.demo.tkrun.paqueteria.almacen;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;
import giis.demo.tkrun.paqueteria.login.SesionUsuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AlmacenController {

    private static final String PLACEHOLDER_TIPO      = "Seleccione...";
    private static final String PLACEHOLDER_INSPECCION = "Seleccione...";

    private final CargaDescargaView view;
    private final AlmacenModel model;
    private PaqueteVerificacionDto paqueteActual;

    public AlmacenController(CargaDescargaView view, AlmacenModel model) {
        this.view  = view;
        this.model = model;
        initView();
        initListeners();
        view.setVisible(true);
    }

    private void initView() {
        SesionUsuario sesion = SesionUsuario.getInstance();
        view.getLblAlmacen().setText("Almacen: " + sesion.getCodigoPunto());

        view.getCmbTipoOperacion().addItem(PLACEHOLDER_TIPO);
        view.getCmbTipoOperacion().addItem("ENTRADA");
        view.getCmbTipoOperacion().addItem("SALIDA");

        view.getCmbInspeccion().addItem(PLACEHOLDER_INSPECCION);
        view.getCmbInspeccion().addItem("CORRECTO");
        view.getCmbInspeccion().addItem("DANO_LEVE");
        view.getCmbInspeccion().addItem("DANO_GRAVE");
    }

    private void initListeners() {
        view.getBtnBuscar().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::buscarPaquete));
        view.getBtnRegistrar().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::registrarOperacion));
        view.getBtnCancelar().addActionListener(e -> view.dispose());

        view.getCmbTipoOperacion().addActionListener(e -> {
            paqueteActual = null;
            limpiarDatos();
            actualizarEstadoBoton();
        });
        view.getChkConfirmo().addActionListener(e -> actualizarEstadoBoton());
        view.getCmbInspeccion().addActionListener(e -> actualizarEstadoBoton());
        view.getTxtPesoMedido().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { actualizarEstadoBoton(); }
            public void removeUpdate(DocumentEvent e)  { actualizarEstadoBoton(); }
            public void changedUpdate(DocumentEvent e) { actualizarEstadoBoton(); }
        });
    }

    private void buscarPaquete() {
        String cb = view.getTxtCodigoBarras().getText().trim();
        if (cb.isEmpty())
            throw new ApplicationException("Introduzca el codigo de barras del paquete.");

        String tipo = (String) view.getCmbTipoOperacion().getSelectedItem();
        if (PLACEHOLDER_TIPO.equals(tipo))
            throw new ApplicationException("Seleccione el tipo de operacion antes de buscar.");

        SesionUsuario sesion = SesionUsuario.getInstance();
        paqueteActual = model.buscarPaquete(cb, tipo,
                sesion.getIdPuntoLogistico(), sesion.getIdUsuario());

        view.getLblIdEnvio().setText(paqueteActual.getCodigoEnvio());
        view.getLblDestinatario().setText(paqueteActual.getDestinatario());
        view.getLblDescripcion().setText(paqueteActual.getDescripcion());
        view.getLblPesoRef().setText(String.format("%.2f kg", paqueteActual.getPesoRefKg()));

        actualizarEstadoBoton();
    }

    private void registrarOperacion() {
        if (paqueteActual == null)
            throw new ApplicationException("Busque primero el paquete.");

        String inspeccion = (String) view.getCmbInspeccion().getSelectedItem();
        double peso = parsePeso(view.getTxtPesoMedido().getText());
        if (peso <= 0)
            throw new ApplicationException("El peso medido debe ser mayor que 0.");

        SesionUsuario sesion = SesionUsuario.getInstance();

        OperacionAlmacenDto dto = new OperacionAlmacenDto();
        dto.setIdPaquete(paqueteActual.getIdPaquete());
        dto.setIdEnvio(paqueteActual.getIdEnvio());
        dto.setIdTramo(paqueteActual.getIdTramo());
        dto.setTipoOperacion((String) view.getCmbTipoOperacion().getSelectedItem());
        dto.setInspeccionVisual(inspeccion);
        dto.setPesoMedidoKg(peso);
        dto.setPesoRefKg(paqueteActual.getPesoRefKg());
        dto.setIdAlmacen(sesion.getIdPuntoLogistico());
        dto.setIdOperario(sesion.getIdUsuario());

        ResultadoOperacionDto resultado = model.registrarOperacion(dto);

        String msg = "Operacion registrada con exito.";
        if (resultado.isIncidenciaGenerada()) {
            msg += "\nSe ha generado una incidencia que sera revisada por el administrador.";
        }
        JOptionPane.showMessageDialog(view, msg, "Operacion completada", JOptionPane.INFORMATION_MESSAGE);
        view.dispose();
    }

    private void actualizarEstadoBoton() {
        boolean habilitado = paqueteActual != null
                && !PLACEHOLDER_TIPO.equals(view.getCmbTipoOperacion().getSelectedItem())
                && view.getChkConfirmo().isSelected()
                && !PLACEHOLDER_INSPECCION.equals(view.getCmbInspeccion().getSelectedItem())
                && parsePeso(view.getTxtPesoMedido().getText()) > 0;
        view.getBtnRegistrar().setEnabled(habilitado);
    }

    private void limpiarDatos() {
        view.getLblIdEnvio().setText("—");
        view.getLblDestinatario().setText("—");
        view.getLblDescripcion().setText("—");
        view.getLblPesoRef().setText("—");
        view.getChkConfirmo().setSelected(false);
        view.getCmbInspeccion().setSelectedIndex(0);
        view.getTxtPesoMedido().setText("");
    }

    private double parsePeso(String s) {
        try {
            return Double.parseDouble(s.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
