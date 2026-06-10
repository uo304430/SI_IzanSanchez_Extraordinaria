package giis.demo.util;

import giis.demo.tkrun.paqueteria.login.LoginPaqueteriaController;
import giis.demo.tkrun.paqueteria.login.LoginPaqueteriaView;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Punto de entrada principal con botones de inicializacion de BD y acceso al sistema.
 */
public class SwingMain {

    private JFrame frame;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() { //NOSONAR codigo autogenerado
            public void run() {
                try {
                    SwingMain window = new SwingMain();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace(); //NOSONAR codigo autogenerado
                }
            }
        });
    }

    public SwingMain() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Main");
        frame.setBounds(0, 0, 335, 200);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JButton btnPaqueteria = new JButton("Sistema de Paqueteria");
        btnPaqueteria.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginPaqueteriaController(new LoginPaqueteriaView());
            }
        });
        frame.getContentPane().add(btnPaqueteria);

        JButton btnInicializarBD = new JButton("Inicializar Base de Datos en Blanco");
        btnInicializarBD.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
            public void actionPerformed(ActionEvent e) {
                new Database().createDatabase(false);
            }
        });
        frame.getContentPane().add(btnInicializarBD);

        JButton btnCargarDatos = new JButton("Cargar Datos Iniciales para Pruebas");
        btnCargarDatos.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
            public void actionPerformed(ActionEvent e) {
                Database db = new Database();
                db.createDatabase(false);
                db.loadDatabase();
            }
        });
        frame.getContentPane().add(btnCargarDatos);
    }

    public JFrame getFrame() { return this.frame; }
}
