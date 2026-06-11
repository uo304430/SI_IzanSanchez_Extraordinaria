package giis.demo.tkrun.paqueteria.seguimiento;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ModificacionEntregaView extends JDialog {

    // --- Cabecera informativa ---
    private JLabel lblTitulo;
    private JLabel lblInfoModificaciones;

    // --- Selector de opcion ---
    private JRadioButton rbOpcionA;
    private JRadioButton rbOpcionB;

    // --- Panel A: cambio de direccion ---
    private JPanel panelA;
    private JTextField txtNombreDestinatario;
    private JTextField txtTelefonoDestinatario;
    private JTextField txtDireccion;
    private JTextField txtCiudad;
    private JTextField txtCodigoPostal;

    // --- Panel B: recogida en punto destino ---
    private JPanel panelB;
    private JLabel lblPuntoDestino;
    private JLabel lblHorario;

    // --- Botones ---
    private JButton btnConfirmar;
    private JButton btnCancelar;

    public ModificacionEntregaView(JDialog owner, String codigoEnvio, int modificacionesRealizadas) {
        super(owner, "Modificar lugar de entrega", true);
        initComponents(codigoEnvio, modificacionesRealizadas);
        setSize(560, 520);
        setMinimumSize(new Dimension(500, 460));
        setResizable(true);
        setLocationRelativeTo(owner);
    }

    private void initComponents(String codigoEnvio, int modificacionesRealizadas) {
        JPanel contenido = new JPanel(new MigLayout("insets 15, wrap 1", "[grow, fill]"));

        // Titulo
        lblTitulo = new JLabel("Modificar lugar de entrega del envio " + codigoEnvio);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 13f));
        contenido.add(lblTitulo);

        JLabel lblInfo = new JLabel("Esta modificacion no tiene coste adicional.");
        lblInfo.setForeground(new Color(60, 130, 60));
        contenido.add(lblInfo);

        lblInfoModificaciones = new JLabel(
                "Modificaciones realizadas: " + modificacionesRealizadas + " de 3 permitidas");
        contenido.add(lblInfoModificaciones);

        // Separador
        contenido.add(new JSeparator(), "growx, gaptop 5, gapbottom 5");

        // Radio buttons de opcion
        rbOpcionA = new JRadioButton("Cambiar direccion de entrega", true);
        rbOpcionB = new JRadioButton("Redirigir al almacen/oficina de destino para recogida en mano");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbOpcionA);
        grupo.add(rbOpcionB);

        contenido.add(new JLabel("Seleccione una opcion:"), "gaptop 5");
        contenido.add(rbOpcionA);
        contenido.add(rbOpcionB);

        // Panel A
        panelA = buildPanelA();
        contenido.add(panelA, "gaptop 8");

        // Panel B
        panelB = buildPanelB();
        panelB.setVisible(false);
        contenido.add(panelB, "gaptop 8");

        // Botones
        btnConfirmar = new JButton("Confirmar modificacion");
        btnCancelar  = new JButton("Cancelar");
        JPanel botones = new JPanel(new MigLayout("insets 0", "push[][]"));
        botones.add(btnConfirmar);
        botones.add(btnCancelar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contenido, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);
    }

    private JPanel buildPanelA() {
        JPanel p = new JPanel(new MigLayout("insets 8", "[right][grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder("Nueva direccion de entrega"));

        txtNombreDestinatario   = new JTextField(25);
        txtTelefonoDestinatario = new JTextField(15);
        txtDireccion            = new JTextField(30);
        txtCiudad               = new JTextField(20);
        txtCodigoPostal         = new JTextField(8);

        p.add(new JLabel("Nombre destinatario *:"));
        p.add(txtNombreDestinatario, "wrap");
        p.add(new JLabel("Telefono destinatario *:"));
        p.add(txtTelefonoDestinatario, "wrap");
        p.add(new JLabel("Calle y numero *:"));
        p.add(txtDireccion, "wrap");
        p.add(new JLabel("Ciudad *:"));
        p.add(txtCiudad, "wrap");
        p.add(new JLabel("Codigo postal *:"));
        p.add(txtCodigoPostal, "wrap");

        return p;
    }

    private JPanel buildPanelB() {
        JPanel p = new JPanel(new MigLayout("insets 8", "[right][grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder("Punto de recogida en destino"));

        lblPuntoDestino = new JLabel(" ");
        lblHorario      = new JLabel(" ");

        p.add(new JLabel("Recogida en:"));
        p.add(lblPuntoDestino, "wrap");
        p.add(new JLabel("Horario:"));
        p.add(lblHorario, "wrap");

        return p;
    }

    /** Precarga el panel A con los datos actuales del envio. */
    public void precargarPanelA(String nombre, String telefono) {
        txtNombreDestinatario.setText(nombre);
        txtTelefonoDestinatario.setText(telefono);
        txtDireccion.setText("");
        txtCiudad.setText("");
        txtCodigoPostal.setText("");
    }

    /** Carga la informacion del punto destino en el panel B. */
    public void cargarPanelB(String descripcionPunto, String horario) {
        lblPuntoDestino.setText(descripcionPunto);
        lblHorario.setText(horario);
    }

    /** Alterna entre panel A y panel B segun la opcion seleccionada. */
    public void mostrarPanelOpcion(char opcion) {
        panelA.setVisible(opcion == 'A');
        panelB.setVisible(opcion == 'B');
        revalidate();
        repaint();
    }

    // --- Getters ---
    public JRadioButton getRbOpcionA()               { return rbOpcionA; }
    public JRadioButton getRbOpcionB()               { return rbOpcionB; }
    public JTextField   getTxtNombreDestinatario()   { return txtNombreDestinatario; }
    public JTextField   getTxtTelefonoDestinatario() { return txtTelefonoDestinatario; }
    public JTextField   getTxtDireccion()            { return txtDireccion; }
    public JTextField   getTxtCiudad()               { return txtCiudad; }
    public JTextField   getTxtCodigoPostal()         { return txtCodigoPostal; }
    public JButton      getBtnConfirmar()            { return btnConfirmar; }
    public JButton      getBtnCancelar()             { return btnCancelar; }
}
