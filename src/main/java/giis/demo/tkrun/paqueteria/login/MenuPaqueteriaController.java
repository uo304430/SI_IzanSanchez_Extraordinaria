package giis.demo.tkrun.paqueteria.login;

import giis.demo.util.SwingUtil;
import giis.demo.tkrun.paqueteria.almacen.AlmacenController;
import giis.demo.tkrun.paqueteria.almacen.AlmacenModel;
import giis.demo.tkrun.paqueteria.almacen.CargaDescargaView;
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
        view.getBtnCargaDescarga().setEnabled(sesion.isOperario());
    }

    private void initListeners() {
        view.getBtnRegistrarEnvio().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::abrirRegistroEnvio));
        view.getBtnCargaDescarga().addActionListener(e ->
                SwingUtil.exceptionWrapper(this::abrirCargaDescarga));
        view.getBtnSalir().addActionListener(e -> salir());
    }

    private void abrirRegistroEnvio() {
        RegistroEnvioView registroView = new RegistroEnvioView(view);
        new EnvioController(registroView, new EnvioModel());
    }

    private void abrirCargaDescarga() {
        CargaDescargaView cargaView = new CargaDescargaView(view);
        new AlmacenController(cargaView, new AlmacenModel());
    }

    private void salir() {
        view.dispose();
        new LoginPaqueteriaController(new LoginPaqueteriaView());
    }
}
