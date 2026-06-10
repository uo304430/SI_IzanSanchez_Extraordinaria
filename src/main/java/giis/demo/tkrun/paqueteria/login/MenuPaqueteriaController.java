package giis.demo.tkrun.paqueteria.login;

import giis.demo.util.SwingUtil;
import giis.demo.tkrun.paqueteria.envios.EnvioController;
import giis.demo.tkrun.paqueteria.envios.EnvioModel;
import giis.demo.tkrun.paqueteria.envios.RegistroEnvioView;

public class MenuPaqueteriaController {

    private final MenuPaqueteriaView view;

    public MenuPaqueteriaController(MenuPaqueteriaView view) {
        this.view = view;
        initView();
        initListeners();
        view.setVisible(true);
    }

    private void initView() {
        SesionUsuario sesion = SesionUsuario.getInstance();
        view.getLblSesion().setText("Sesion: " + sesion.getNombre() + " · " + sesion.getCodigoPunto());
    }

    private void initListeners() {
        view.getBtnRegistrarEnvio().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::abrirRegistroEnvio));
        view.getBtnSalir().addActionListener(e -> salir());
    }

    private void abrirRegistroEnvio() {
        RegistroEnvioView registroView = new RegistroEnvioView(view);
        new EnvioController(registroView, new EnvioModel());
    }

    private void salir() {
        view.dispose();
        new LoginPaqueteriaController(new LoginPaqueteriaView());
    }
}
