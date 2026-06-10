package giis.demo.tkrun.paqueteria.envios;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;
import giis.demo.tkrun.paqueteria.login.SesionUsuario;
import giis.demo.tkrun.paqueteria.util.ComboItem;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class EnvioController {

    private final RegistroEnvioView view;
    private final EnvioModel model;
    private BigDecimal costeCalculado;

    public EnvioController(RegistroEnvioView view, EnvioModel model) {
        this.view = view;
        this.model = model;
        initCombos();
        initListeners();
        view.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Inicializacion
    // -------------------------------------------------------------------------

    private void initCombos() {
        ComboItem placeholder = new ComboItem(0, "-- Seleccione --");

        view.getCmbZonaOrigen().addItem(placeholder);
        view.getCmbZonaDestino().addItem(new ComboItem(0, "-- Seleccione --"));
        for (ComboItem z : model.getZonas()) {
            view.getCmbZonaOrigen().addItem(z);
            view.getCmbZonaDestino().addItem(z);
        }

        view.getCmbTipoServicio().addItem(new ComboItem(0, "-- Seleccione --"));
        for (ComboItem ts : model.getTiposServicio())
            view.getCmbTipoServicio().addItem(ts);

        cargarPuntosDestino(null);
    }

    private void cargarPuntosDestino(String modalidadEntrega) {
        String filtro = "OFICINA".equals(modalidadEntrega) ? "OFICINA" : null;
        view.getCmbPuntoDestino().removeAllItems();
        view.getCmbPuntoDestino().addItem(new ComboItem(0, "-- Seleccione --"));
        for (ComboItem p : model.getPuntosLogisticos(filtro))
            view.getCmbPuntoDestino().addItem(p);
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    private void initListeners() {
        view.getCmbModalidadEntrega().addActionListener(e -> {
            String sel = (String) view.getCmbModalidadEntrega().getSelectedItem();
            cargarPuntosDestino(sel);
            resetCoste();
        });
        view.getBtnCalcular().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::calcularCoste));
        view.getBtnConfirmar().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::confirmarRegistro));
        view.getBtnCancelar().addActionListener(e -> view.dispose());
    }

    // -------------------------------------------------------------------------
    // Acciones
    // -------------------------------------------------------------------------

    private void calcularCoste() {
        validar();
        EnvioCreacionDto dto = leerFormulario();
        Optional<BigDecimal> tarifa = model.calcularCoste(
                dto.getIdTipoServicio(), dto.getPesoKg(),
                dto.getIdZonaOrigen(), dto.getIdZonaDestino());
        if (tarifa.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay tarifa vigente para los datos introducidos.\nContacte con el administrador.",
                    "Sin tarifa", JOptionPane.WARNING_MESSAGE);
            resetCoste();
            return;
        }
        costeCalculado = tarifa.get();
        view.getLblCoste().setText(String.format("%.2f EUR", costeCalculado));
        view.getBtnConfirmar().setEnabled(true);
    }

    private void confirmarRegistro() {
        validar();
        EnvioCreacionDto dto = leerFormulario();
        dto.setCosteCalculado(costeCalculado);

        SesionUsuario sesion = SesionUsuario.getInstance();
        EnvioResumenDto resumen = model.registrarEnvio(
                dto, sesion.getIdUsuario(), sesion.getIdPuntoLogistico());

        String msg = String.format(
                "Envio registrado con exito.%nIdentificador: %s%nCodigo de barras: %s%nCoste cobrado: %.2f EUR",
                resumen.getCodigoEnvio(), resumen.getCodigoBarras(), resumen.getCoste());

        Object[] opciones = {"Aceptar", "Nuevo envio"};
        int resp = JOptionPane.showOptionDialog(view, msg, "Registro completado",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, opciones, opciones[0]);

        if (resp == 0) {
            view.dispose();
        } else {
            limpiarFormulario();
        }
    }

    // -------------------------------------------------------------------------
    // Lectura del formulario
    // -------------------------------------------------------------------------

    private EnvioCreacionDto leerFormulario() {
        EnvioCreacionDto dto = new EnvioCreacionDto();

        dto.setRemitenteNombre(view.getTxtRemNombre().getText().trim());
        dto.setRemitenteDni(view.getTxtRemDni().getText().trim());
        dto.setRemitenteTelefono(view.getTxtRemTelefono().getText().trim());
        dto.setRemitenteDireccion(view.getTxtRemDireccion().getText().trim());
        dto.setRemitenteCiudad(view.getTxtRemCiudad().getText().trim());
        dto.setRemitenteCodigoPostal(view.getTxtRemCp().getText().trim());
        dto.setIdZonaOrigen(idDeCombo(view.getCmbZonaOrigen()));

        dto.setDestinatarioNombre(view.getTxtDestNombre().getText().trim());
        dto.setDestinatarioTelefono(view.getTxtDestTelefono().getText().trim());
        dto.setDestinatarioDireccion(view.getTxtDestDireccion().getText().trim());
        dto.setDestinatarioCiudad(view.getTxtDestCiudad().getText().trim());
        dto.setDestinatarioCodigoPostal(view.getTxtDestCp().getText().trim());
        dto.setIdZonaDestino(idDeCombo(view.getCmbZonaDestino()));

        dto.setDescripcionPaquete(view.getTxtDescripcion().getText().trim());
        dto.setPesoKg(parseBigDecimal(view.getTxtPeso().getText()));
        dto.setLargoCm(parseInt(view.getTxtLargo().getText()));
        dto.setAnchoCm(parseInt(view.getTxtAncho().getText()));
        dto.setAltoCm(parseInt(view.getTxtAlto().getText()));
        String valStr = view.getTxtValorDeclarado().getText().trim();
        dto.setValorDeclarado(valStr.isEmpty() ? BigDecimal.ZERO : parseBigDecimal(valStr));

        dto.setIdTipoServicio(idDeCombo(view.getCmbTipoServicio()));
        dto.setModalidadRecogida((String) view.getCmbModalidadRecogida().getSelectedItem());
        dto.setModalidadEntrega((String) view.getCmbModalidadEntrega().getSelectedItem());
        dto.setFormaPago((String) view.getCmbFormaPago().getSelectedItem());
        dto.setIdPuntoDestino(idDeCombo(view.getCmbPuntoDestino()));

        return dto;
    }

    // -------------------------------------------------------------------------
    // Validacion
    // -------------------------------------------------------------------------

    private void validar() {
        noVacio(view.getTxtRemNombre().getText(), "Nombre del remitente");
        validarDni(view.getTxtRemDni().getText().trim());
        noVacio(view.getTxtRemTelefono().getText(), "Telefono del remitente");

        String modalRecogida = (String) view.getCmbModalidadRecogida().getSelectedItem();
        if ("-- Seleccione --".equals(modalRecogida))
            throw new ApplicationException("Debe seleccionar la modalidad de recogida.");

        String modalEntrega = (String) view.getCmbModalidadEntrega().getSelectedItem();
        if ("-- Seleccione --".equals(modalEntrega))
            throw new ApplicationException("Debe seleccionar la modalidad de entrega.");

        if ("DOMICILIO".equals(modalRecogida)) {
            noVacio(view.getTxtRemDireccion().getText(),
                    "Direccion del remitente (obligatoria con recogida a domicilio)");
            noVacio(view.getTxtRemCiudad().getText(),
                    "Ciudad del remitente (obligatoria con recogida a domicilio)");
            noVacio(view.getTxtRemCp().getText(),
                    "Codigo postal del remitente (obligatorio con recogida a domicilio)");
        }

        noVacio(view.getTxtDestNombre().getText(),    "Nombre del destinatario");
        noVacio(view.getTxtDestTelefono().getText(),  "Telefono del destinatario");
        noVacio(view.getTxtDestDireccion().getText(), "Direccion del destinatario");
        noVacio(view.getTxtDestCiudad().getText(),    "Ciudad del destinatario");
        noVacio(view.getTxtDestCp().getText(),        "Codigo postal del destinatario");

        if (idDeCombo(view.getCmbZonaOrigen()) == 0)
            throw new ApplicationException("Debe seleccionar la zona de origen.");
        if (idDeCombo(view.getCmbZonaDestino()) == 0)
            throw new ApplicationException("Debe seleccionar la zona de destino.");

        noVacio(view.getTxtDescripcion().getText(), "Descripcion del paquete");
        decimalPositivo(view.getTxtPeso().getText(), "Peso");
        enteroPositivo(view.getTxtLargo().getText(), "Largo");
        enteroPositivo(view.getTxtAncho().getText(), "Ancho");
        enteroPositivo(view.getTxtAlto().getText(),  "Alto");

        if (idDeCombo(view.getCmbTipoServicio()) == 0)
            throw new ApplicationException("Debe seleccionar el tipo de servicio.");
        if (idDeCombo(view.getCmbPuntoDestino()) == 0)
            throw new ApplicationException("Debe seleccionar el punto de destino.");
    }

    private void noVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new ApplicationException("El campo '" + campo + "' es obligatorio.");
    }

    private void validarDni(String dni) {
        if (!dni.matches("[0-9]{8}[A-Z]"))
            throw new ApplicationException(
                    "El DNI debe tener 8 digitos seguidos de una letra mayuscula. Valor: " + dni);
    }

    private void decimalPositivo(String valor, String campo) {
        try {
            if (new BigDecimal(valor.trim()).compareTo(BigDecimal.ZERO) <= 0)
                throw new ApplicationException("El campo '" + campo + "' debe ser mayor que 0.");
        } catch (NumberFormatException e) {
            throw new ApplicationException("El campo '" + campo + "' debe ser un numero decimal valido.");
        }
    }

    private void enteroPositivo(String valor, String campo) {
        try {
            if (Integer.parseInt(valor.trim()) <= 0)
                throw new ApplicationException("El campo '" + campo + "' debe ser mayor que 0.");
        } catch (NumberFormatException e) {
            throw new ApplicationException("El campo '" + campo + "' debe ser un entero valido.");
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void resetCoste() {
        view.getLblCoste().setText("—");
        view.getBtnConfirmar().setEnabled(false);
        costeCalculado = null;
    }

    private void limpiarFormulario() {
        List<JTextField> campos = List.of(
                view.getTxtRemNombre(), view.getTxtRemDni(), view.getTxtRemTelefono(),
                view.getTxtRemDireccion(), view.getTxtRemCiudad(), view.getTxtRemCp(),
                view.getTxtDestNombre(), view.getTxtDestTelefono(),
                view.getTxtDestDireccion(), view.getTxtDestCiudad(), view.getTxtDestCp(),
                view.getTxtDescripcion(), view.getTxtPeso(),
                view.getTxtLargo(), view.getTxtAncho(), view.getTxtAlto(),
                view.getTxtValorDeclarado());
        campos.forEach(f -> f.setText(""));

        view.getCmbZonaOrigen().setSelectedIndex(0);
        view.getCmbZonaDestino().setSelectedIndex(0);
        view.getCmbTipoServicio().setSelectedIndex(0);
        view.getCmbModalidadRecogida().setSelectedIndex(0);
        view.getCmbModalidadEntrega().setSelectedIndex(0);
        view.getCmbFormaPago().setSelectedIndex(0);
        view.getCmbPuntoDestino().setSelectedIndex(0);
        resetCoste();
    }

    private int idDeCombo(JComboBox<ComboItem> combo) {
        ComboItem item = (ComboItem) combo.getSelectedItem();
        return item != null ? item.getId() : 0;
    }

    private BigDecimal parseBigDecimal(String s) {
        try { return new BigDecimal(s.trim()); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
