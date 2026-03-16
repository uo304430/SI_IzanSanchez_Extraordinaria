package giis.demo.tkrun.Login;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/**
 * Vista de identificación de usuario.
 * Permite seleccionar el tipo de usuario (Ciudadano, Operador, Técnico)
 * e introducir el email o DNI para continuar.
 */
public class LoginView {

    private JFrame frame;
    private JComboBox<String> cmbTipo;
    private JTextField txtIdentificacion;
    private JButton btnEntrar;

    public LoginView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Identificación de Usuario");
        frame.setName("Login");
        frame.setBounds(200, 200, 400, 180);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(
                new MigLayout("insets 15", "[right][grow]", "[][][]"));

        frame.getContentPane().add(new JLabel("Tipo de usuario:"), "cell 0 0");
        cmbTipo = new JComboBox<>(new String[]{"Ciudadano", "Operador", "Técnico"});
        cmbTipo.setName("cmbTipo");
        frame.getContentPane().add(cmbTipo, "cell 1 0, growx");

        frame.getContentPane().add(new JLabel("Email / DNI:"), "cell 0 1");
        txtIdentificacion = new JTextField();
        txtIdentificacion.setName("txtIdentificacion");
        frame.getContentPane().add(txtIdentificacion, "cell 1 1, growx");

        btnEntrar = new JButton("Entrar");
        btnEntrar.setName("btnEntrar");
        frame.getContentPane().add(btnEntrar, "cell 1 2, growx");
    }

    public JFrame getFrame() { return frame; }

    public String getTipoUsuario() { return (String) cmbTipo.getSelectedItem(); }

    public String getIdentificacion() { return txtIdentificacion.getText(); }

    public JButton getBtnEntrar() { return btnEntrar; }
}
