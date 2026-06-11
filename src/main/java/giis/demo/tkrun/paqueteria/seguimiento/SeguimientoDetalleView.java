package giis.demo.tkrun.paqueteria.seguimiento;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SeguimientoDetalleView extends JDialog {

    // ---- Seccion A: datos generales ----
    private JLabel lblCodigo;
    private JLabel lblEstado;
    private JLabel lblFechaRegistro;
    private JLabel lblTipoServicio;
    private JLabel lblModalidad;
    private JLabel lblCoste;
    private JLabel lblValorDeclarado;
    private JLabel lblDestinatarioNombre;
    private JLabel lblDestinatarioTelefono;
    private JLabel lblDireccion;
    private JLabel lblFechaEstimada;
    private JLabel lblFechaEntregaReal;

    // ---- Seccion B: ruta ----
    private JPanel panelRuta;
    private JLabel lblSinRuta;
    private JTable tblTramos;
    private DefaultTableModel modeloTramos;

    // ---- Seccion C: historial ----
    private JTable tblHistorial;
    private DefaultTableModel modeloHistorial;

    // ---- Seccion D: incidencias ----
    private JPanel panelIncidencias;
    private JTable tblIncidencias;
    private DefaultTableModel modeloIncidencias;

    // ---- Botones ----
    private JButton btnModificarEntrega;
    private JButton btnCancelarEnvio;
    private JButton btnCerrar;
    private JLabel  lblModificaciones;
    private JLabel  lblSinModificaciones;

    private static final String[] COL_TRAMOS     = {"Orden","Tipo","Origen","Destino","Vehiculo","Prevista","Real","Estado"};
    private static final String[] COL_HISTORIAL  = {"Fecha/Hora","Accion","Responsable","Punto","Comentario"};
    private static final String[] COL_INCIDENCIAS= {"Tipo","Fecha apertura","Descripcion","Estado"};

    public SeguimientoDetalleView(JFrame owner, int idEnvio) {
        super(owner, "Detalle del envio", true);
        initComponents();
        setSize(920, 720);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel contenido = new JPanel(new MigLayout("insets 15, wrap 1", "[grow, fill]", "[]10[]10[]10[]"));

        contenido.add(buildSeccionDatosGenerales());
        contenido.add(buildSeccionRuta());
        contenido.add(buildSeccionHistorial());
        panelIncidencias = buildSeccionIncidencias();
        panelIncidencias.setVisible(false);
        contenido.add(panelIncidencias);

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel principal con scroll arriba y botones abajo
        JPanel main = new JPanel(new BorderLayout());
        main.add(scroll, BorderLayout.CENTER);
        main.add(buildBotones(), BorderLayout.SOUTH);
        add(main);
    }

    private JPanel buildSeccionDatosGenerales() {
        JPanel p = new JPanel(new MigLayout("insets 10", "[right][grow, fill][right][grow, fill]", "[]6[]6[]6[]6[]6[]"));
        p.setBorder(BorderFactory.createTitledBorder("Datos generales del envio"));

        lblCodigo = new JLabel(" ");
        lblCodigo.setFont(lblCodigo.getFont().deriveFont(Font.BOLD, 14f));
        p.add(lblCodigo, "span 4, wrap");

        lblEstado = new JLabel(" ");
        lblEstado.setOpaque(true);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        p.add(new JLabel("Estado:"));
        p.add(lblEstado, "span 3, wrap");

        p.add(new JLabel("Fecha registro:"));
        lblFechaRegistro = new JLabel();
        p.add(lblFechaRegistro);

        p.add(new JLabel("Tipo servicio:"));
        lblTipoServicio = new JLabel();
        p.add(lblTipoServicio, "wrap");

        p.add(new JLabel("Modalidad entrega:"));
        lblModalidad = new JLabel();
        p.add(lblModalidad);

        p.add(new JLabel("Coste:"));
        lblCoste = new JLabel();
        p.add(lblCoste, "wrap");

        p.add(new JLabel("Valor declarado:"));
        lblValorDeclarado = new JLabel();
        p.add(lblValorDeclarado);

        p.add(new JLabel("F. estimada entrega:"));
        lblFechaEstimada = new JLabel();
        p.add(lblFechaEstimada, "wrap");

        p.add(new JLabel("F. entrega real:"));
        lblFechaEntregaReal = new JLabel();
        p.add(lblFechaEntregaReal, "span 3, wrap");

        p.add(new JLabel("Destinatario:"));
        lblDestinatarioNombre = new JLabel();
        p.add(lblDestinatarioNombre);
        p.add(new JLabel("Telefono:"));
        lblDestinatarioTelefono = new JLabel();
        p.add(lblDestinatarioTelefono, "wrap");

        p.add(new JLabel("Direccion:"));
        lblDireccion = new JLabel();
        p.add(lblDireccion, "span 3, wrap");

        return p;
    }

    private JPanel buildSeccionRuta() {
        panelRuta = new JPanel(new MigLayout("insets 10, wrap 1", "[grow, fill]"));
        panelRuta.setBorder(BorderFactory.createTitledBorder("Ruta del envio"));

        lblSinRuta = new JLabel("Ruta no asignada todavia.");
        lblSinRuta.setVisible(false);
        panelRuta.add(lblSinRuta);

        modeloTramos = new DefaultTableModel(COL_TRAMOS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTramos = new JTable(modeloTramos);
        tblTramos.setDefaultRenderer(Object.class, new TramoColorRenderer());
        tblTramos.setRowHeight(22);
        tblTramos.getTableHeader().setReorderingAllowed(false);
        tblTramos.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblTramos.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblTramos.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblTramos.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblTramos.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblTramos.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblTramos.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblTramos.getColumnModel().getColumn(7).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(tblTramos);
        scroll.setPreferredSize(new Dimension(860, 120));
        panelRuta.add(scroll);
        return panelRuta;
    }

    private JPanel buildSeccionHistorial() {
        JPanel p = new JPanel(new MigLayout("insets 10, wrap 1", "[grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder("Historial del envio"));

        modeloHistorial = new DefaultTableModel(COL_HISTORIAL, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistorial = new JTable(modeloHistorial);
        tblHistorial.setRowHeight(22);
        tblHistorial.getTableHeader().setReorderingAllowed(false);
        tblHistorial.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblHistorial.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblHistorial.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblHistorial.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblHistorial.getColumnModel().getColumn(4).setPreferredWidth(260);

        JScrollPane scroll = new JScrollPane(tblHistorial);
        scroll.setPreferredSize(new Dimension(860, 140));
        p.add(scroll);
        return p;
    }

    private JPanel buildSeccionIncidencias() {
        JPanel p = new JPanel(new MigLayout("insets 10, wrap 1", "[grow, fill]"));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 80, 80)),
                "Aviso: Incidencias asociadas"));

        modeloIncidencias = new DefaultTableModel(COL_INCIDENCIAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblIncidencias = new JTable(modeloIncidencias);
        tblIncidencias.setRowHeight(22);
        tblIncidencias.getColumnModel().getColumn(0).setPreferredWidth(130);
        tblIncidencias.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblIncidencias.getColumnModel().getColumn(2).setPreferredWidth(400);
        tblIncidencias.getColumnModel().getColumn(3).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(tblIncidencias);
        scroll.setPreferredSize(new Dimension(860, 90));
        p.add(scroll);
        return p;
    }

    private JPanel buildBotones() {
        btnModificarEntrega  = new JButton("Modificar lugar de entrega");
        btnCancelarEnvio     = new JButton("Cancelar envio");
        btnCerrar            = new JButton("Cerrar");
        lblModificaciones    = new JLabel("Modificaciones realizadas: 0 de 3 permitidas");
        lblSinModificaciones = new JLabel("El envio ya no admite modificaciones.");
        lblSinModificaciones.setForeground(new Color(160, 80, 0));
        lblSinModificaciones.setVisible(false);

        JPanel p = new JPanel(new MigLayout("insets 10, wrap 1", "[]"));
        JPanel fila1 = new JPanel(new MigLayout("insets 0", "[][]push[]"));
        fila1.add(btnModificarEntrega);
        fila1.add(btnCancelarEnvio);
        fila1.add(btnCerrar);
        p.add(fila1, "growx");
        p.add(lblModificaciones);
        p.add(lblSinModificaciones);
        return p;
    }

    // ---- Poblado ----

    public void poblarDatosGenerales(EnvioDetalleDto dto) {
        lblCodigo.setText(dto.getCodigo());
        lblEstado.setText("  " + dto.getEstado() + "  ");
        lblEstado.setBackground(colorEstado(dto.getEstado()));
        lblFechaRegistro.setText(dto.getFechaRegistro());
        lblTipoServicio.setText(dto.getTipoServicio());
        lblModalidad.setText(dto.getModalidadEntrega());
        lblCoste.setText(dto.getCoste());
        lblValorDeclarado.setText(dto.getValorDeclarado());
        lblFechaEstimada.setText(dto.getFechaEstimada());
        lblFechaEntregaReal.setText(dto.getFechaEntregaReal());
        lblDestinatarioNombre.setText(dto.getDestinatarioNombre());
        lblDestinatarioTelefono.setText(dto.getDestinatarioTelefono());
        lblDireccion.setText(dto.getDireccionDestinatario());

        // Label contador de modificaciones
        int mod = dto.getModificacionesEntrega();
        lblModificaciones.setText("Modificaciones realizadas: " + mod + " de 3 permitidas");

        // Habilitar boton segun reglas de negocio
        boolean estadoPermite = "REGISTRADO".equals(dto.getEstado())
                || "EN_RUTA".equals(dto.getEstado())
                || "EN_TRANSITO".equals(dto.getEstado());
        boolean puedeModificar = estadoPermite && mod < 3;
        btnModificarEntrega.setEnabled(puedeModificar);

        // Aviso cuando el envio ya no admite modificaciones
        boolean mostrarAviso = !estadoPermite;
        lblSinModificaciones.setVisible(mostrarAviso);
    }

    public void poblarTramos(List<TramoListadoDto> tramos) {
        modeloTramos.setRowCount(0);
        if (tramos.isEmpty()) {
            lblSinRuta.setVisible(true);
            tblTramos.setVisible(false);
            return;
        }
        lblSinRuta.setVisible(false);
        tblTramos.setVisible(true);
        for (TramoListadoDto t : tramos) {
            modeloTramos.addRow(new Object[]{
                t.getOrden(), t.getTipo(), t.getOrigen(), t.getDestino(),
                t.getVehiculo(), t.getFechaPrevista(), t.getFechaReal(), t.getEstado()
            });
        }
    }

    public void poblarHistorial(List<EventoHistorialDto> eventos) {
        modeloHistorial.setRowCount(0);
        for (EventoHistorialDto e : eventos) {
            modeloHistorial.addRow(new Object[]{
                e.getFechaEvento(), e.getAccion(), e.getResponsable(),
                e.getPunto(), e.getComentario()
            });
        }
    }

    public void poblarIncidencias(List<IncidenciaListadoDto> incidencias) {
        if (incidencias.isEmpty()) {
            panelIncidencias.setVisible(false);
            return;
        }
        modeloIncidencias.setRowCount(0);
        for (IncidenciaListadoDto i : incidencias) {
            modeloIncidencias.addRow(new Object[]{
                i.getTipo(), i.getFechaApertura(), i.getDescripcion(), i.getEstado()
            });
        }
        panelIncidencias.setVisible(true);
    }

    // ---- Getters para el controlador ----

    public JButton getBtnModificarEntrega() { return btnModificarEntrega; }
    public JButton getBtnCancelarEnvio()    { return btnCancelarEnvio; }
    public JButton getBtnCerrar()           { return btnCerrar; }
    public JLabel  getLblModificaciones()   { return lblModificaciones; }

    // ---- Color del estado del envio ----

    private Color colorEstado(String estado) {
        if ("ENTREGADO".equals(estado))                                             return new Color(180, 240, 180);
        if ("PENDIENTE_REENTREGA".equals(estado))                                   return new Color(255, 210, 120);
        if ("DEPOSITADO_EN_PUNTO".equals(estado) || "EN_DEVOLUCION".equals(estado)) return new Color(255, 180, 180);
        return new Color(225, 225, 225);
    }

    // ---- Renderer de colores para la tabla de tramos ----

    private static class TramoColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
            if (!selected) {
                // La columna 7 es el estado del tramo
                Object estadoObj = table.getModel().getValueAt(row, 7);
                String estado = estadoObj == null ? "" : estadoObj.toString();
                switch (estado) {
                    case "COMPLETADO":   c.setBackground(new Color(210, 245, 210)); break;
                    case "EN_TRANSITO":
                    case "ALMACENADO":   c.setBackground(new Color(255, 250, 200)); break;
                    case "FALLIDO":      c.setBackground(new Color(255, 210, 210)); break;
                    default:             c.setBackground(Color.WHITE);             break;
                }
            }
            return c;
        }
    }
}
