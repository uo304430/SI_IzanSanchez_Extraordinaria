package giis.demo.tkrun.paqueteria.seguimiento;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SeguimientoListadoView extends JDialog {

    private JLabel lblTitulo;
    private JComboBox<String> cmbEstado;
    private JTextField txtBuscarId;
    private JButton btnBuscar;
    private JButton btnRefrescar;
    private JTable tblEnvios;
    private DefaultTableModel modeloTabla;
    private JButton btnVerDetalle;
    private JButton btnCerrar;

    private static final String[] COLUMNAS = {
        "Identificador", "Fecha registro", "Destinatario", "Estado", "F. estimada entrega"
    };

    // Valores de estado para el filtro: primer item = sin filtro
    static final String[] ESTADOS_FILTRO = {
        "", "REGISTRADO", "EN_RUTA", "EN_TRANSITO", "EN_REPARTO",
        "PENDIENTE_REENTREGA", "ENTREGADO", "DEPOSITADO_EN_PUNTO",
        "EN_DEVOLUCION", "CANCELADO"
    };
    static final String[] ESTADOS_ETIQUETA = {
        "Todos", "Registrado", "En ruta", "En transito", "En reparto",
        "Pendiente de reentrega", "Entregado", "Depositado en punto",
        "En devolucion", "Cancelado"
    };

    public SeguimientoListadoView(JFrame owner) {
        super(owner, "Mis envios", true);
        initComponents();
        setSize(820, 480);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel main = new JPanel(new MigLayout("insets 15, wrap 1", "[grow, fill]", "[]10[]10[grow, fill]10[]"));

        // Titulo
        lblTitulo = new JLabel("Mis envios");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));
        main.add(lblTitulo);

        // Barra filtros
        JPanel barraFiltros = new JPanel(new MigLayout("insets 0", "[][grow, fill][][grow, fill][][]"));
        barraFiltros.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(ESTADOS_ETIQUETA);
        barraFiltros.add(cmbEstado);

        barraFiltros.add(new JLabel("Buscar:"));
        txtBuscarId = new JTextField(18);
        barraFiltros.add(txtBuscarId);

        btnBuscar     = new JButton("Buscar");
        btnRefrescar  = new JButton("Refrescar");
        barraFiltros.add(btnBuscar);
        barraFiltros.add(btnRefrescar);
        main.add(barraFiltros);

        // Tabla
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEnvios = new JTable(modeloTabla);
        tblEnvios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEnvios.getTableHeader().setReorderingAllowed(false);
        tblEnvios.setRowHeight(22);
        // Ajuste de columnas
        tblEnvios.getColumnModel().getColumn(0).setPreferredWidth(180);
        tblEnvios.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblEnvios.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblEnvios.getColumnModel().getColumn(3).setPreferredWidth(160);
        tblEnvios.getColumnModel().getColumn(4).setPreferredWidth(130);
        main.add(new JScrollPane(tblEnvios));

        // Botones inferiores
        btnVerDetalle = new JButton("Ver detalle");
        btnCerrar     = new JButton("Cerrar");
        JPanel botones = new JPanel(new MigLayout("insets 0", "[]push[]"));
        botones.add(btnVerDetalle);
        botones.add(btnCerrar);
        main.add(botones);

        add(main);
    }

    public JLabel getLblTitulo()            { return lblTitulo; }
    public JComboBox<String> getCmbEstado() { return cmbEstado; }
    public JTextField getTxtBuscarId()      { return txtBuscarId; }
    public JButton getBtnBuscar()           { return btnBuscar; }
    public JButton getBtnRefrescar()        { return btnRefrescar; }
    public JTable getTblEnvios()            { return tblEnvios; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JButton getBtnVerDetalle()       { return btnVerDetalle; }
    public JButton getBtnCerrar()           { return btnCerrar; }
}
