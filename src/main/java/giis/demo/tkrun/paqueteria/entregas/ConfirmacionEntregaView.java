package giis.demo.tkrun.paqueteria.entregas;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ConfirmacionEntregaView extends JDialog {

    // --- Datos del envio (solo lectura) ---
    private JLabel lblCodigo;
    private JLabel lblPaquete;
    private JLabel lblDestinatario;
    private JLabel lblDireccion;
    private JLabel lblIntento;

    // --- Panel principal: dos botones ---
    private JPanel panelPrincipal;
    private JButton btnCompletada;
    private JButton btnFallida;

    // --- Panel fallo: sub-formulario ---
    private JPanel panelFallo;
    private JComboBox<String> cmbMotivo;
    private JTextArea txtComentario;
    private JButton btnConfirmarFallo;
    private JButton btnVolver;

    public ConfirmacionEntregaView(JFrame owner, EntregaListadoDto entrega) {
        super(owner, "Confirmar entrega", true);
        initComponents(entrega);
        setSize(520, 440);
        setMinimumSize(new Dimension(460, 380));
        setResizable(true);
        setLocationRelativeTo(owner);
    }

    private void initComponents(EntregaListadoDto entrega) {
        JPanel contenido = new JPanel(new MigLayout("insets 15, wrap 1", "[grow, fill]"));

        // Datos del envio
        JPanel datosPanel = new JPanel(new MigLayout("insets 8", "[right][grow, fill]"));
        datosPanel.setBorder(BorderFactory.createTitledBorder("Datos de la entrega"));

        lblCodigo       = new JLabel(entrega.getCodigoEnvio());
        lblCodigo.setFont(lblCodigo.getFont().deriveFont(Font.BOLD));
        lblPaquete      = new JLabel(entrega.getDescripcionPaquete());
        lblDestinatario = new JLabel(entrega.getDestinatario() + "  |  " + entrega.getTelefono());
        lblDireccion    = new JLabel(entrega.getDireccion());
        lblIntento      = new JLabel("Intento " + entrega.getIntentoActual() + " de 4");
        lblIntento.setFont(lblIntento.getFont().deriveFont(Font.BOLD));

        datosPanel.add(new JLabel("Envio:"));
        datosPanel.add(lblCodigo, "wrap");
        datosPanel.add(new JLabel("Paquete:"));
        datosPanel.add(lblPaquete, "wrap");
        datosPanel.add(new JLabel("Destinatario:"));
        datosPanel.add(lblDestinatario, "wrap");
        datosPanel.add(new JLabel("Direccion:"));
        datosPanel.add(lblDireccion, "wrap");
        datosPanel.add(new JLabel("Intento:"));
        datosPanel.add(lblIntento, "wrap");
        contenido.add(datosPanel);

        // Panel principal: dos botones grandes
        panelPrincipal = new JPanel(new MigLayout("insets 10", "[grow, fill][grow, fill]"));
        btnCompletada = new JButton("<html><center>Entrega<br>completada</center></html>");
        btnCompletada.setBackground(new Color(0xE8, 0xF5, 0xE9));
        btnCompletada.setOpaque(true);
        btnCompletada.setPreferredSize(new Dimension(190, 60));

        btnFallida = new JButton("<html><center>Registrar<br>entrega fallida</center></html>");
        btnFallida.setBackground(new Color(0xFF, 0xF3, 0xE0));
        btnFallida.setOpaque(true);
        btnFallida.setPreferredSize(new Dimension(190, 60));

        panelPrincipal.add(btnCompletada);
        panelPrincipal.add(btnFallida);
        contenido.add(panelPrincipal, "growx");

        // Panel fallo: sub-formulario
        panelFallo = new JPanel(new MigLayout("insets 8, wrap 1", "[grow, fill]"));
        panelFallo.setBorder(BorderFactory.createTitledBorder("Motivo del fallo"));
        panelFallo.setVisible(false);

        cmbMotivo = new JComboBox<>(new String[]{"-- Seleccione --", "AUSENTE", "DIRECCION_INCORRECTA", "RECHAZADO", "OTROS"});
        txtComentario = new JTextArea(4, 30);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        JScrollPane scrollComentario = new JScrollPane(txtComentario);

        JPanel botonesSubForm = new JPanel(new MigLayout("insets 0", "[]push[]"));
        btnVolver        = new JButton("Volver");
        btnConfirmarFallo = new JButton("Confirmar fallo");
        botonesSubForm.add(btnVolver);
        botonesSubForm.add(btnConfirmarFallo);

        panelFallo.add(new JLabel("Motivo *:"));
        panelFallo.add(cmbMotivo, "growx");
        panelFallo.add(new JLabel("Comentario:"));
        panelFallo.add(scrollComentario, "growx, h 80!");
        panelFallo.add(botonesSubForm, "growx");
        contenido.add(panelFallo, "growx");

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(new JScrollPane(contenido), java.awt.BorderLayout.CENTER);
    }

    /** Muestra el sub-formulario de fallo y oculta los dos botones principales. */
    public void mostrarPanelFallo() {
        panelPrincipal.setVisible(false);
        panelFallo.setVisible(true);
        revalidate();
        repaint();
    }

    /** Vuelve a los dos botones principales. */
    public void mostrarPanelPrincipal() {
        panelFallo.setVisible(false);
        panelPrincipal.setVisible(true);
        cmbMotivo.setSelectedIndex(0);
        txtComentario.setText("");
        revalidate();
        repaint();
    }

    public JButton      getBtnCompletada()    { return btnCompletada; }
    public JButton      getBtnFallida()       { return btnFallida; }
    public JComboBox<String> getCmbMotivo()   { return cmbMotivo; }
    public JTextArea    getTxtComentario()    { return txtComentario; }
    public JButton      getBtnConfirmarFallo(){ return btnConfirmarFallo; }
    public JButton      getBtnVolver()        { return btnVolver; }
}
