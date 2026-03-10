package giis.demo.util;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.*;
import giis.demo.tkrun.CiudadanoConsulataIncidencias.*;
import giis.demo.tkrun.OperadorValidaIncidencias.*;
import giis.demo.tkrun.OperadorAsigna.*;
import Izan_33804.*;
import Izan_33805.*;

/**
 * Punto de entrada principal que incluye botones para la ejecucion de las pantallas
 * de las aplicaciones de ejemplo
 * y acciones de inicializacion de la base de datos.
 * No sigue MVC pues es solamente temporal para que durante el desarrollo se tenga posibilidad
 * de realizar acciones de inicializacion
 */
public class SwingMain {

	private JFrame frame;
	// Keep a reference to controllers to avoid unused-variable warnings and to manage lifecycle
	private ValidarControler validarController;
	private AsignarController asignarController;

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
		frame.setBounds(0, 0, 287, 185);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		// Establecer layout vertical para que los botones se apilen y no se sobreescriban
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		JButton btnEjecutarIncidencias = new JButton("Registrar Incidencias");
		btnEjecutarIncidencias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IncidenciasController controller = new IncidenciasController(new IncidenciasModel(), new IncidenciasView());
				controller.initController();
			}
		});
		frame.getContentPane().add(btnEjecutarIncidencias);

		JButton btnProbarConsultaIncidencias = new JButton("Consultar Incidencias");
		btnProbarConsultaIncidencias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConsultaController controller = new ConsultaController(new ConsultaModel(), new ConsultaView());
				controller.initController();
			}
		});
		frame.getContentPane().add(btnProbarConsultaIncidencias);

		JButton btnProbarValidarIncidencias = new JButton("Validar Incidencias");
		btnProbarValidarIncidencias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String operador = "carlos.ruiz@example.com";
				validarController = new ValidarControler(new ValidarModel(), new ValidarView(), operador);
			}
		});
		frame.getContentPane().add(btnProbarValidarIncidencias);

		JButton btnProbarAsignarIncidencias = new JButton("Asignar Incidencias");
		btnProbarAsignarIncidencias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String operador = "carlos.ruiz@example.com";
				asignarController = new AsignarController(new AsignarModel(), new AsignarView(), operador);
			}
		});
		frame.getContentPane().add(btnProbarAsignarIncidencias);

		JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
		btnInicializarBaseDeDatos.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
			}
		});
		frame.getContentPane().add(btnInicializarBaseDeDatos);

		JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
		btnCargarDatosIniciales.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
				db.loadDatabase();
			}
		});
		frame.getContentPane().add(btnCargarDatosIniciales);

		JButton btnPlanRes = new JButton("Planificar resolucion de una incidencia ");
		btnPlanRes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Database db = new Database();

					db.createDatabase(false);

					db.loadDatabase();


					CambioEstadoModel model = new CambioEstadoModel();
					CambioEstadoView view = new CambioEstadoView();
					String emailTecnico = "carlos.ruiz@example.com";

					CambioEstadoController controller = new CambioEstadoController(model, view, emailTecnico);
					controller.initController();

					System.out.println("Módulo lanzado con éxito.");
				} catch (Exception ex) {
					ex.printStackTrace();
					SwingUtil.showMessage("Error en el proceso unificado: " + ex.getMessage(), "Error", 0);
				}
			}
		});
		frame.getContentPane().add(btnPlanRes);

		JButton btnHistorial = new JButton(" Visualizar Historial Completo");
		btnHistorial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					HistorialModel model = new HistorialModel();
					HistorialView view = new HistorialView();
					HistorialController controller = new HistorialController(model, view);
					controller.initController();

					System.out.println("Historial lanzado con éxito.");
				} catch (Exception ex) {
					ex.printStackTrace();
					SwingUtil.showMessage("Error al lanzar el historial: " + ex.getMessage(), "Error", 0);
				}
			}
		});
		frame.getContentPane().add(btnHistorial);}

	public JFrame getFrame() { return this.frame; }

}