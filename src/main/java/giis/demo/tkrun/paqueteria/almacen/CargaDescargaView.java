package giis.demo.tkrun.paqueteria.almacen;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CargaDescargaView extends JDialog {

    private JLabel lblAlmacen;

    private JComboBox<String> cmbTipoOperacion;
    private JTextField txtCodigoBarras;
    private JButton btnBuscar;

    // Panel datos encontrados
    private JLabel lblIdEnvio;
    private JLabel lblDestinatario;
    private JLabel lblDescripcion;
    private JLabel lblPesoRef;

    private JCheckBox chkConfirmo;
    private JComboBox<String> cmbInspeccion;
    private JTextField txtPesoMedido;

    private JButton btnRegistrar;
    private JButton btnCancelar;

    public CargaDescargaView(JFrame owner) {
        super(owner, "Control de Carga/Descarga en Almacen", true);
        initComponents();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel main = new JPanel(new MigLayout("insets 15, wrap 2", "[right][grow, fill]", "[]8[]8[]15[]8[]8[]8[]15[]15[]"));

        // Almacen
        lblAlmacen = new JLabel("Almacen: —");
        lblAlmacen.setFont(lblAlmacen.getFont().deriveFont(Font.BOLD));
        main.add(lblAlmacen, "span 2");

        // Tipo de operacion
        main.add(new JLabel("Tipo de operacion:"));
        cmbTipoOperacion = new JComboBox<>();
        main.add(cmbTipoOperacion);

        // Codigo de barras
        main.add(new JLabel("Codigo de barras:"));
        JPanel panelCb = new JPanel(new MigLayout("insets 0", "[grow, fill][]"));
        txtCodigoBarras = new JTextField(20);
        btnBuscar = new JButton("Buscar paquete");
        panelCb.add(txtCodigoBarras);
        panelCb.add(btnBuscar);
        main.add(panelCb);

        // Panel datos del envio encontrado
        JPanel panelDatos = new JPanel(new MigLayout("insets 8", "[right][grow, fill]", "[]6[]6[]6[]"));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos del envio encontrado"));

        panelDatos.add(new JLabel("Identificador:"));
        lblIdEnvio = new JLabel("—");
        panelDatos.add(lblIdEnvio);

        panelDatos.add(new JLabel("Destinatario:"));
        lblDestinatario = new JLabel("—");
        panelDatos.add(lblDestinatario);

        panelDatos.add(new JLabel("Descripcion:"));
        lblDescripcion = new JLabel("—");
        panelDatos.add(lblDescripcion);

        panelDatos.add(new JLabel("Peso registrado:"));
        lblPesoRef = new JLabel("—");
        panelDatos.add(lblPesoRef);

        main.add(panelDatos, "span 2, grow");

        // Checkbox confirmacion
        chkConfirmo = new JCheckBox("Confirmo que los datos coinciden con el bulto fisico");
        main.add(chkConfirmo, "span 2");

        // Inspeccion visual
        main.add(new JLabel("Inspeccion visual:"));
        cmbInspeccion = new JComboBox<>();
        main.add(cmbInspeccion);

        // Peso medido
        main.add(new JLabel("Peso medido (kg):"));
        txtPesoMedido = new JTextField(10);
        main.add(txtPesoMedido);

        // Botones
        btnRegistrar = new JButton("Registrar operacion");
        btnRegistrar.setEnabled(false);
        btnCancelar  = new JButton("Cancelar");
        main.add(btnRegistrar, "span 2, split 2, center");
        main.add(btnCancelar);

        add(main);
    }

    public JLabel getLblAlmacen()          { return lblAlmacen; }
    public JComboBox<String> getCmbTipoOperacion() { return cmbTipoOperacion; }
    public JTextField getTxtCodigoBarras() { return txtCodigoBarras; }
    public JButton getBtnBuscar()          { return btnBuscar; }
    public JLabel getLblIdEnvio()          { return lblIdEnvio; }
    public JLabel getLblDestinatario()     { return lblDestinatario; }
    public JLabel getLblDescripcion()      { return lblDescripcion; }
    public JLabel getLblPesoRef()          { return lblPesoRef; }
    public JCheckBox getChkConfirmo()      { return chkConfirmo; }
    public JComboBox<String> getCmbInspeccion() { return cmbInspeccion; }
    public JTextField getTxtPesoMedido()   { return txtPesoMedido; }
    public JButton getBtnRegistrar()       { return btnRegistrar; }
    public JButton getBtnCancelar()        { return btnCancelar; }
}
