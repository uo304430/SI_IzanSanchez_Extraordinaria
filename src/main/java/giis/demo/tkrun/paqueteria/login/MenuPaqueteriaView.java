package giis.demo.tkrun.paqueteria.login;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class MenuPaqueteriaView extends JFrame {

    private JLabel lblSesion;
    private JButton btnRegistrarEnvio;
    private JButton btnCargaDescarga;
    private JButton btnMisEnvios;
    private JButton btnSalir;

    public MenuPaqueteriaView() {
        setTitle("Sistema de Paqueteria — Menu Principal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout("insets 20, wrap 1", "[grow, fill]", "[]15[]10[]10[]10[]"));

        lblSesion = new JLabel(" ");
        panel.add(lblSesion);

        btnRegistrarEnvio = new JButton("Registrar envio");
        panel.add(btnRegistrarEnvio);

        btnCargaDescarga = new JButton("Carga/Descarga en almacen");
        panel.add(btnCargaDescarga);

        btnMisEnvios = new JButton("Mis envios");
        panel.add(btnMisEnvios);

        btnSalir = new JButton("Salir");
        panel.add(btnSalir);

        add(panel);
        setSize(320, 245);
        setLocationRelativeTo(null);
    }

    public JLabel getLblSesion()           { return lblSesion; }
    public JButton getBtnRegistrarEnvio()  { return btnRegistrarEnvio; }
    public JButton getBtnCargaDescarga()   { return btnCargaDescarga; }
    public JButton getBtnMisEnvios()       { return btnMisEnvios; }
    public JButton getBtnSalir()           { return btnSalir; }
}
