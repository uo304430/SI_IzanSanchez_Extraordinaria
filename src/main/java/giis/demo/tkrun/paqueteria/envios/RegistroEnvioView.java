package giis.demo.tkrun.paqueteria.envios;

import giis.demo.tkrun.paqueteria.util.ComboItem;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RegistroEnvioView extends JDialog {

    // --- Remitente ---
    private JTextField txtRemNombre;
    private JTextField txtRemDni;
    private JTextField txtRemTelefono;
    private JTextField txtRemDireccion;
    private JTextField txtRemCiudad;
    private JTextField txtRemCp;
    private JComboBox<ComboItem> cmbZonaOrigen;

    // --- Destinatario ---
    private JTextField txtDestNombre;
    private JTextField txtDestTelefono;
    private JTextField txtDestDireccion;
    private JTextField txtDestCiudad;
    private JTextField txtDestCp;
    private JComboBox<ComboItem> cmbZonaDestino;

    // --- Paquete ---
    private JTextField txtDescripcion;
    private JTextField txtPeso;
    private JTextField txtLargo;
    private JTextField txtAncho;
    private JTextField txtAlto;
    private JTextField txtValorDeclarado;

    // --- Opciones ---
    private JComboBox<ComboItem> cmbTipoServicio;
    private JComboBox<String> cmbModalidadRecogida;
    private JComboBox<String> cmbModalidadEntrega;
    private JComboBox<String> cmbFormaPago;
    private JComboBox<ComboItem> cmbPuntoDestino;

    // --- Resultado y acciones ---
    private JLabel lblCoste;
    private JButton btnCalcular;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    public RegistroEnvioView(JFrame owner) {
        super(owner, "Registrar Envio", true);
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel main = new JPanel(new MigLayout("insets 10, wrap 1", "[grow, fill]"));
        main.add(buildSeccionRemitente());
        main.add(buildSeccionDestinatario());
        main.add(buildSeccionPaquete());
        main.add(buildSeccionOpciones());
        main.add(buildSeccionAcciones());

        JScrollPane scroll = new JScrollPane(main);
        scroll.setPreferredSize(new Dimension(660, 620));
        add(scroll);
    }

    private JPanel buildSeccionRemitente() {
        JPanel p = new JPanel(new MigLayout("insets 8",
                "[right][grow, fill][right][grow, fill][right][grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder("Datos del Remitente"));

        txtRemNombre   = new JTextField(20);
        txtRemDni      = new JTextField(12);
        txtRemTelefono = new JTextField(14);
        txtRemDireccion = new JTextField(30);
        txtRemCiudad   = new JTextField(15);
        txtRemCp       = new JTextField(8);
        cmbZonaOrigen  = new JComboBox<>();

        p.add(new JLabel("Nombre *:"));
        p.add(txtRemNombre, "span 5, grow, wrap");

        p.add(new JLabel("DNI *:"));
        p.add(txtRemDni);
        p.add(new JLabel("Telefono *:"));
        p.add(txtRemTelefono, "span 3, wrap");

        p.add(new JLabel("Direccion:"));
        p.add(txtRemDireccion, "span 5, grow, wrap");

        p.add(new JLabel("Ciudad:"));
        p.add(txtRemCiudad);
        p.add(new JLabel("C.P.:"));
        p.add(txtRemCp, "span 3, wrap");

        p.add(new JLabel("Zona origen *:"));
        p.add(cmbZonaOrigen, "span 5, grow, wrap");

        return p;
    }

    private JPanel buildSeccionDestinatario() {
        JPanel p = new JPanel(new MigLayout("insets 8",
                "[right][grow, fill][right][grow, fill][right][grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder("Datos del Destinatario"));

        txtDestNombre    = new JTextField(20);
        txtDestTelefono  = new JTextField(14);
        txtDestDireccion = new JTextField(30);
        txtDestCiudad    = new JTextField(15);
        txtDestCp        = new JTextField(8);
        cmbZonaDestino   = new JComboBox<>();

        p.add(new JLabel("Nombre *:"));
        p.add(txtDestNombre, "span 5, grow, wrap");

        p.add(new JLabel("Telefono *:"));
        p.add(txtDestTelefono, "span 5, grow, wrap");

        p.add(new JLabel("Direccion *:"));
        p.add(txtDestDireccion, "span 5, grow, wrap");

        p.add(new JLabel("Ciudad *:"));
        p.add(txtDestCiudad);
        p.add(new JLabel("C.P. *:"));
        p.add(txtDestCp, "span 3, wrap");

        p.add(new JLabel("Zona destino *:"));
        p.add(cmbZonaDestino, "span 5, grow, wrap");

        return p;
    }

    private JPanel buildSeccionPaquete() {
        JPanel p = new JPanel(new MigLayout("insets 8",
                "[right][grow, fill][right][grow, fill][right][grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder("Datos del Paquete"));

        txtDescripcion    = new JTextField(30);
        txtPeso           = new JTextField(8);
        txtLargo          = new JTextField(6);
        txtAncho          = new JTextField(6);
        txtAlto           = new JTextField(6);
        txtValorDeclarado = new JTextField(8);

        p.add(new JLabel("Descripcion *:"));
        p.add(txtDescripcion, "span 5, grow, wrap");

        p.add(new JLabel("Peso kg *:"));
        p.add(txtPeso);
        p.add(new JLabel("Largo cm *:"));
        p.add(txtLargo);
        p.add(new JLabel("Ancho cm *:"));
        p.add(txtAncho, "wrap");

        p.add(new JLabel("Alto cm *:"));
        p.add(txtAlto);
        p.add(new JLabel("Valor dec. EUR:"));
        p.add(txtValorDeclarado, "span 3, wrap");

        return p;
    }

    private JPanel buildSeccionOpciones() {
        JPanel p = new JPanel(new MigLayout("insets 8",
                "[right][grow, fill][right][grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder("Opciones"));

        cmbTipoServicio      = new JComboBox<>();
        cmbModalidadRecogida = new JComboBox<>(new String[]{"-- Seleccione --", "OFICINA", "DOMICILIO"});
        cmbModalidadEntrega  = new JComboBox<>(new String[]{"-- Seleccione --", "DOMICILIO", "OFICINA"});
        cmbFormaPago         = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA"});
        cmbPuntoDestino      = new JComboBox<>();

        p.add(new JLabel("Tipo servicio *:"));
        p.add(cmbTipoServicio);
        p.add(new JLabel("Forma de pago *:"));
        p.add(cmbFormaPago, "wrap");

        p.add(new JLabel("Modalidad recogida *:"));
        p.add(cmbModalidadRecogida);
        p.add(new JLabel("Modalidad entrega *:"));
        p.add(cmbModalidadEntrega, "wrap");

        p.add(new JLabel("Punto destino *:"));
        p.add(cmbPuntoDestino, "span 3, grow, wrap");

        return p;
    }

    private JPanel buildSeccionAcciones() {
        JPanel p = new JPanel(new MigLayout("insets 8", "[right][grow, fill]"));

        lblCoste     = new JLabel("—");
        btnCalcular  = new JButton("Calcular coste");
        btnConfirmar = new JButton("Confirmar registro");
        btnCancelar  = new JButton("Cancelar");
        btnConfirmar.setEnabled(false);

        p.add(new JLabel("Coste:"));
        p.add(lblCoste, "wrap");
        p.add(btnCalcular, "span 2, split 3, center");
        p.add(btnConfirmar);
        p.add(btnCancelar);

        return p;
    }

    // --- Getters ---
    public JTextField getTxtRemNombre()       { return txtRemNombre; }
    public JTextField getTxtRemDni()          { return txtRemDni; }
    public JTextField getTxtRemTelefono()     { return txtRemTelefono; }
    public JTextField getTxtRemDireccion()    { return txtRemDireccion; }
    public JTextField getTxtRemCiudad()       { return txtRemCiudad; }
    public JTextField getTxtRemCp()           { return txtRemCp; }
    public JComboBox<ComboItem> getCmbZonaOrigen()  { return cmbZonaOrigen; }

    public JTextField getTxtDestNombre()      { return txtDestNombre; }
    public JTextField getTxtDestTelefono()    { return txtDestTelefono; }
    public JTextField getTxtDestDireccion()   { return txtDestDireccion; }
    public JTextField getTxtDestCiudad()      { return txtDestCiudad; }
    public JTextField getTxtDestCp()          { return txtDestCp; }
    public JComboBox<ComboItem> getCmbZonaDestino() { return cmbZonaDestino; }

    public JTextField getTxtDescripcion()     { return txtDescripcion; }
    public JTextField getTxtPeso()            { return txtPeso; }
    public JTextField getTxtLargo()           { return txtLargo; }
    public JTextField getTxtAncho()           { return txtAncho; }
    public JTextField getTxtAlto()            { return txtAlto; }
    public JTextField getTxtValorDeclarado()  { return txtValorDeclarado; }

    public JComboBox<ComboItem> getCmbTipoServicio()     { return cmbTipoServicio; }
    public JComboBox<String>    getCmbModalidadRecogida(){ return cmbModalidadRecogida; }
    public JComboBox<String>    getCmbModalidadEntrega() { return cmbModalidadEntrega; }
    public JComboBox<String>    getCmbFormaPago()        { return cmbFormaPago; }
    public JComboBox<ComboItem> getCmbPuntoDestino()     { return cmbPuntoDestino; }

    public JLabel   getLblCoste()     { return lblCoste; }
    public JButton  getBtnCalcular()  { return btnCalcular; }
    public JButton  getBtnConfirmar() { return btnConfirmar; }
    public JButton  getBtnCancelar()  { return btnCancelar; }
}
