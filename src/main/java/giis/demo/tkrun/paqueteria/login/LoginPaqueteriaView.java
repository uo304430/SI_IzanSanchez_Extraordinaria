package giis.demo.tkrun.paqueteria.login;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class LoginPaqueteriaView extends JFrame {

    private JComboBox<EmpleadoComboDto> cmbEmpleado;
    private JButton btnEntrar;
    private JButton btnCancelar;

    public LoginPaqueteriaView() {
        setTitle("Sistema de Paqueteria — Acceso");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout("insets 20, wrap 2", "[right][grow, fill]", "[]10[]20[]"));

        panel.add(new JLabel("Empleado:"));
        cmbEmpleado = new JComboBox<>();
        panel.add(cmbEmpleado);

        btnEntrar    = new JButton("Entrar");
        btnCancelar  = new JButton("Cancelar");
        panel.add(btnEntrar, "span 2, split 2, center");
        panel.add(btnCancelar);

        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    public JComboBox<EmpleadoComboDto> getCmbEmpleado() { return cmbEmpleado; }
    public JButton getBtnEntrar()   { return btnEntrar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
