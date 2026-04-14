package giis.demo.tkrun.Login;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


import java.awt.Font;
import giis.demo.tkrun.ResolverCostes.ResolverCostesSelectorView;
import giis.demo.tkrun.ResolverCostes.ResolverCostesSelectorController;

import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasController;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasView;
import giis.demo.tkrun.CiudadanoConsulataIncidencias.ConsultaController;
import giis.demo.tkrun.CiudadanoConsulataIncidencias.ConsultaModel;
import giis.demo.tkrun.CiudadanoConsulataIncidencias.ConsultaView;
import giis.demo.tkrun.OperadorValidaIncidencias.ValidarControler;
import giis.demo.tkrun.OperadorValidaIncidencias.ValidarModel;
import giis.demo.tkrun.OperadorValidaIncidencias.ValidarView;
import giis.demo.tkrun.OperadorAsigna.AsignarController;
import giis.demo.tkrun.OperadorAsigna.AsignarModel;
import giis.demo.tkrun.OperadorAsigna.AsignarView;
import giis.demo.tkrun.TecnicoAddsDetalles.TecnicoAddsDetallesView;
import giis.demo.tkrun.TecnicoAddsDetalles.TecnicoAddsDetallesController;
import giis.demo.tkrun.TecnicoAddsDetalles.TecnicoAddsDetallesModel;
import giis.demo.tkrun.ExportarHistórico.ExportarHistoricoController;
import giis.demo.tkrun.ExportarHistórico.ExportarHistoricoModel;
import giis.demo.tkrun.ExportarHistórico.ExportarHistoricoView;
import Izan_33804.CambioEstadoController;
import Izan_33804.CambioEstadoModel;
import Izan_33804.CambioEstadoView;
import Izan_33805.HistorialController;
import Izan_33805.HistorialModel;
import Izan_33805.HistorialView;

/**
 * Ventana de menú principal con acciones dependientes del rol del usuario.
 * Recibe el tipo de usuario y su identificación, y muestra únicamente
 * los botones correspondientes a ese rol:
 * <ul>
 *   <li>Ciudadano  → Registrar Incidencias, Consultar Incidencias</li>
 *   <li>Operador   → Validar Incidencias, Asignar Incidencias</li>
 *   <li>Técnico    → Planificar Resolución, Visualizar Historial, Añadir Detalles (prueba)</li>
 * </ul>
 */
public class MenuView {

    private JFrame frame;
    private final String tipoUsuario;
    private final String identificacion;

    public MenuView(String tipoUsuario, String identificacion) {
        this.tipoUsuario = tipoUsuario;
        this.identificacion = identificacion;
        initialize();
    }

    private void initialize() {
        boolean esCiudadano = "Ciudadano".equals(tipoUsuario);
        boolean esOperador  = "Operador".equals(tipoUsuario);
        boolean esTecnico   = "Técnico".equals(tipoUsuario);

        frame = new JFrame("Menú Principal — " + tipoUsuario);
        frame.setName("Menu");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Cabecera con el usuario identificado
        JLabel lblUsuario = new JLabel("  Usuario: " + identificacion + "  (" + tipoUsuario + ")");
        lblUsuario.setFont(lblUsuario.getFont().deriveFont(Font.BOLD));
        lblUsuario.setName("lblUsuario");
        frame.getContentPane().add(lblUsuario);

        // --- Botones de CIUDADANO ---
        JButton btnRegistrar = new JButton("Registrar Incidencias");
        btnRegistrar.setName("btnRegistrar");
        btnRegistrar.setVisible(esCiudadano);
        btnRegistrar.addActionListener(e ->
                new IncidenciasController(new IncidenciasModel(), new IncidenciasView(), identificacion));
        frame.getContentPane().add(btnRegistrar);

        JButton btnConsultar = new JButton("Consultar Incidencias");
        btnConsultar.setName("btnConsultar");
        btnConsultar.setVisible(esCiudadano);
        btnConsultar.addActionListener(e ->
                new ConsultaController(new ConsultaModel(), new ConsultaView(), identificacion));
        frame.getContentPane().add(btnConsultar);

        // --- Botones de OPERADOR ---
        JButton btnValidar = new JButton("Validar Incidencias");
        btnValidar.setName("btnValidar");
        btnValidar.setVisible(esOperador);
        btnValidar.addActionListener(e ->
                new ValidarControler(new ValidarModel(), new ValidarView(), identificacion));
        frame.getContentPane().add(btnValidar);
        
        JButton btnAsignar = new JButton("Asignar Incidencias");
        btnAsignar.setName("btnAsignar");
        btnAsignar.setVisible(esOperador);
        btnAsignar.addActionListener(e ->
                new AsignarController(new AsignarModel(), new AsignarView(), identificacion));
        frame.getContentPane().add(btnAsignar);

        // --- Botón para exportar historial (Operador y Técnico) ---
        JButton btnExportarHistorial = new JButton("Exportar Historial (JSON)");
        btnExportarHistorial.setName("btnExportarHistorial");
        btnExportarHistorial.setVisible(esOperador || esTecnico);
        btnExportarHistorial.addActionListener(e ->
            new ExportarHistoricoController(new ExportarHistoricoModel(), new ExportarHistoricoView()));
        frame.getContentPane().add(btnExportarHistorial);

        // --- Botones de TÉCNICO ---
        JButton btnPlanificar = new JButton("Planificar Resolución");
        btnPlanificar.setName("btnPlanificar");
        btnPlanificar.setVisible(esTecnico);
        btnPlanificar.addActionListener(e -> {
            CambioEstadoController ctrl =
                    new CambioEstadoController(new CambioEstadoModel(), new CambioEstadoView(), identificacion);
            ctrl.initController();
        });
        frame.getContentPane().add(btnPlanificar);
        
        JButton btnResolverCostes = new JButton("Resolver Costes");
        btnResolverCostes.setName("btnResolverCostes");
        btnResolverCostes.setVisible(esTecnico);
        btnResolverCostes.addActionListener(e -> {
            ResolverCostesSelectorView sv = new ResolverCostesSelectorView();
            new ResolverCostesSelectorController(sv, identificacion);
        });
        frame.getContentPane().add(btnResolverCostes);
        

        JButton btnHistorial = new JButton("Visualizar Historial");
        btnHistorial.setName("btnHistorial");
        btnHistorial.setVisible(esTecnico);
        btnHistorial.addActionListener(e -> {
            HistorialController ctrl =
                    new HistorialController(new HistorialModel(), new HistorialView());
            ctrl.initController();
        });
        frame.getContentPane().add(btnHistorial);
        
        
        JButton btnInforme = new JButton("Informe de Incidencias");
        btnInforme.setName("btnInforme");
        btnInforme.setVisible(esTecnico); 
        btnInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                EstadisticasIncidencias.InformeModel model = new EstadisticasIncidencias.InformeModel();
                EstadisticasIncidencias.InformeView view = new EstadisticasIncidencias.InformeView();
                EstadisticasIncidencias.InformeController controller = new EstadisticasIncidencias.InformeController(model, view);
                controller.initController();
            }
        });
        frame.getContentPane().add(btnInforme);
        
     // Código para SwingMain
        JButton btnRechazo = new JButton("Rechazar Incidencias");
        btnRechazo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                RechazoIncidencias.RechazoModel m = new RechazoIncidencias.RechazoModel();
                RechazoIncidencias.RechazoView v = new RechazoIncidencias.RechazoView();
                RechazoIncidencias.RechazoController c = new RechazoIncidencias.RechazoController(m, v);

                // Conectamos botones de la vista con métodos del controlador
                v.getBtnCargar().addActionListener(ev -> c.cargarDatos());
                v.getBtnRechazar().addActionListener(ev -> c.ejecutarRechazo());

                v.getFrame().setVisible(true);
            }
        });
        frame.getContentPane().add(btnRechazo);
       

        JButton btnAddDetalles = new JButton("Añadir Detalles (prueba)");
        btnAddDetalles.setName("btnAddDetalles");
        btnAddDetalles.setVisible(esTecnico);
                btnAddDetalles.addActionListener(e ->
                        new TecnicoAddsDetallesController(new TecnicoAddsDetallesModel(), new TecnicoAddsDetallesView(), identificacion));
        frame.getContentPane().add(btnAddDetalles);


        frame.pack();
        frame.setMinimumSize(new java.awt.Dimension(280, 0));
        frame.setVisible(true);
    }

    public JFrame getFrame() { return frame;
    
    
    }
}