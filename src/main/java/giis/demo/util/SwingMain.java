package giis.demo.util;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import giis.demo.tkrun.Login.LoginController;
import giis.demo.tkrun.Login.LoginView;
import FacturaIncidencia.FacturaModel;

/**
 * Punto de entrada principal que incluye botones para la ejecucion de las pantallas
 * de las aplicaciones de ejemplo
 * y acciones de inicializacion de la base de datos.
 * No sigue MVC pues es solamente temporal para que durante el desarrollo se tenga posibilidad
 * de realizar acciones de inicializacion
 */
public class SwingMain {

	private JFrame frame;


	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the application.
	 */
	public SwingMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Main");
		frame.setBounds(0, 0, 335, 381);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		// Establecer layout vertical para que los botones se apilen y no se sobreescriban
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		JButton btnIdentificarse = new JButton("Identificarse");
		btnIdentificarse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LoginController(new LoginView());
			}
		});
		frame.getContentPane().add(btnIdentificarse);

		JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
		btnInicializarBaseDeDatos.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
				// Además de recrear el esquema, borrar facturas existentes para que se puedan regenerar
				try {
					FacturaModel fm = new FacturaModel();
					fm.borrarTodasFacturas();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(btnInicializarBaseDeDatos);

		JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
		btnCargarDatosIniciales.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
				db.loadDatabase();
				// Además, borrar facturas previas para asegurar estado limpio de pruebas
				try {
					FacturaModel fm = new FacturaModel();
					fm.borrarTodasFacturas();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(btnCargarDatosIniciales);
	}

	public JFrame getFrame() { return this.frame; }

}