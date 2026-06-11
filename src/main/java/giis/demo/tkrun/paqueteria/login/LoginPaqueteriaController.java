package giis.demo.tkrun.paqueteria.login;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;

import java.util.List;

public class LoginPaqueteriaController {

    private final LoginPaqueteriaView view;
    private final LoginModel model;

    public LoginPaqueteriaController(LoginPaqueteriaView view) {
        this.view = view;
        this.model = new LoginModel();
        initView();
        initListeners();
        view.setVisible(true);
    }

    private void initView() {
        List<EmpleadoComboDto> empleados = model.getEmpleadosActivos();
        for (EmpleadoComboDto emp : empleados)
            view.getCmbEmpleado().addItem(emp);
    }

    private void initListeners() {
        view.getBtnEntrar().addActionListener(e -> SwingUtil.exceptionWrapper(this::entrar));
        view.getBtnCancelar().addActionListener(e -> System.exit(0));
    }

    private void entrar() {
        EmpleadoComboDto empleado = (EmpleadoComboDto) view.getCmbEmpleado().getSelectedItem();
        if (empleado == null)
            throw new ApplicationException("Debe seleccionar un empleado.");
        SesionUsuario.getInstance().iniciar(
                empleado.getIdUsuario(),
                empleado.getNombre(),
                empleado.getIdPuntoLogistico(),
                empleado.getCodigoPunto(),
                empleado.getRol());
        view.dispose();
        new MenuPaqueteriaController(new MenuPaqueteriaView());
    }
}
