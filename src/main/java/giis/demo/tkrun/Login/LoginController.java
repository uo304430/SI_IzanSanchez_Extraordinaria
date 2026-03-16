package giis.demo.tkrun.Login;

import javax.swing.JOptionPane;
import giis.demo.util.SwingUtil;

/**
 * Controlador de la ventana de identificación.
 * Valida los datos introducidos y abre el menú principal correspondiente al rol.
 */
public class LoginController {

    private LoginView view;

    public LoginController(LoginView v) {
        this.view = v;
        initController();
    }

    public void initController() {
        view.getFrame().setVisible(true);
        view.getBtnEntrar().addActionListener(
                e -> SwingUtil.exceptionWrapper(this::entrar));
    }

    private void entrar() {
        String tipo = view.getTipoUsuario();
        String identificacion = view.getIdentificacion().trim();
        if (identificacion.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view.getFrame(),
                    "Debe introducir su email o DNI.",
                    "Campo obligatorio",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        view.getFrame().setVisible(false);
        view.getFrame().dispose();
        new MenuView(tipo, identificacion);
    }
}
