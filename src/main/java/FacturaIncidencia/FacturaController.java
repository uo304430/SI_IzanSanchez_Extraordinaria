package FacturaIncidencia;

import giis.demo.util.SwingUtil;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import java.util.List;

public class FacturaController {
    private FacturaModel model;
    private FacturaView view;
    private String usuario; // nombre del emisor (Gestor Económico) opcional

    public FacturaController(FacturaModel m, FacturaView v) {
        this(m, v, null);
    }

    public FacturaController(FacturaModel m, FacturaView v, String usuario) {
        this.model = m;
        this.view = v;
        this.usuario = usuario;
    }

    public void initController() {
        cargarDatos();
        view.getBtnGenerar().addActionListener(e -> ejecutarGenerar());
        view.getBtnVer().addActionListener(e -> ejecutarVer());
        // Listener para habilitar/deshabilitar botón Generar según selección
        view.getTabla().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesSegunSeleccion();
            }
        });
        view.getFrame().setVisible(true);
    }

    public void cargarDatos() {
        try {
            // Cargamos todas las incidencias con coste y el flag 'facturada'
            List<Object[]> lista = model.getIncidenciasConCoste(false);
            String[] cols = new String[]{"id", "fecha", "descripcion", "coste", "tecnico", "facturada"};
            if (lista == null || lista.isEmpty()) {
                view.getTabla().setModel(new DefaultTableModel(cols, 0));
                view.getBtnGenerar().setEnabled(false);
                view.getBtnVer().setEnabled(false);
                return;
            }
            DefaultTableModel tm = new DefaultTableModel(cols, lista.size());
            for (int i = 0; i < lista.size(); i++) {
                Object[] row = lista.get(i);
                for (int j = 0; j < cols.length && j < row.length; j++) {
                    // Convertir el flag facturada en Sí/No cuando corresponde
                    if (j == 5) {
                        Object v = row[j];
                        String s = "";
                        if (v != null) {
                            try { int iv = Integer.parseInt(v.toString()); s = iv == 1 ? "Sí" : "No"; } catch(Exception ex) { s = v.toString(); }
                        }
                        tm.setValueAt(s, i, j);
                    } else {
                        tm.setValueAt(row[j], i, j);
                    }
                }
            }
            view.getTabla().setModel(tm);
            // Inicializar estado botones
            view.getBtnGenerar().setEnabled(false);
            view.getBtnVer().setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getFrame(), "Error al cargar incidencias: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarEstadoBotonesSegunSeleccion() {
        int fila = view.getTabla().getSelectedRow();
        if (fila == -1) {
            view.getBtnGenerar().setEnabled(false);
            view.getBtnVer().setEnabled(false);
            return;
        }
        // columna 'facturada' es la 5
        Object fact = view.getTabla().getValueAt(fila, 5);
        boolean estaFacturada = false;
        if (fact != null) {
            String s = fact.toString();
            estaFacturada = s.equalsIgnoreCase("Sí") || s.equals("1");
        }
        view.getBtnGenerar().setEnabled(!estaFacturada);
        view.getBtnVer().setEnabled(true);
    }

    private void ejecutarGenerar() {
        int fila = view.getTabla().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Debes seleccionar una incidencia.");
            return;
        }
        try {
            // chequeo del flag facturada por seguridad
            Object fact = view.getTabla().getValueAt(fila, 5);
            if (fact != null && (fact.toString().equalsIgnoreCase("Sí") || fact.toString().equals("1"))) {
                JOptionPane.showMessageDialog(view.getFrame(), "La incidencia ya está facturada. Solo se permite su consulta.");
                return;
            }

            int id = Integer.parseInt(view.getTabla().getValueAt(fila, 0).toString());
            String em = this.usuario != null ? this.usuario : "Gestor Económico";
            FacturaDTO dto = model.generarFacturaParaIncidencia(id, em);
            if (dto == null) {
                JOptionPane.showMessageDialog(view.getFrame(), "No se pudo generar la factura (coste inválido o ya facturada).");
                return;
            }
            // Mostrar la factura en el area
            mostrarFactura(dto);
            JOptionPane.showMessageDialog(view.getFrame(), "Factura generada con número: " + dto.getNumero());
            // recargar datos para reflejar que ahora está facturada
            cargarDatos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Error al generar factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ejecutarVer() {
        int fila = view.getTabla().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Debes seleccionar una incidencia.");
            return;
        }
        try {
            int id = Integer.parseInt(view.getTabla().getValueAt(fila, 0).toString());

            // Buscamos la factura por incidencia usando el método del modelo
            FacturaDTO f = model.getFacturaByIncidencia(id);

            if (f == null) {
                JOptionPane.showMessageDialog(view.getFrame(), "No existe factura para la incidencia seleccionada.");
                return;
            }
            mostrarFactura(f);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Error al ver factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarFactura(FacturaDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Factura Nº: ").append(dto.getNumero()).append("\n");
        sb.append("Emisor: ").append(dto.getEmisor()).append("\n");
        sb.append("Fecha: ").append(dto.getFecha()).append("\n");
        sb.append("Coste total: ").append(dto.getCosteTotal()).append("\n\n");
        sb.append("Descripción técnica:\n").append(dto.getDescripcionTecnica()).append("\n\n");
        sb.append("Conceptos:\n");
        if (dto.getConceptos() != null && !dto.getConceptos().isEmpty()) {
            for (ConceptoDTO c : dto.getConceptos()) {
                sb.append(" - ").append(c.getDescripcion()).append(" : ").append(c.getImporte()).append("\n");
            }
        } else {
            sb.append(" (sin conceptos)\n");
        }
        view.getAreaFactura().setText(sb.toString());
    }
}