package giis.demo.tkrun.TecnicoAddsDetalles;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.DTOs.HistorialDTO;
import giis.demo.util.ApplicationException;
import com.github.lgooddatepicker.components.DateTimePicker;

public class TecnicoAddsDetallesView {
    private JFrame frame;
    private JTable tablaIncidencias;
    private DefaultTableModel tableModel;
    private JTextArea comentarioArea;
    private JTable historialTable;
    private DefaultTableModel historialModel;
    private DateTimePicker fechaPicker;
    private JButton añadirComentarioButton;
    private TecnicoAddsDetallesController controller;
    private List<IncidenciaDTO> incidencias;

    public TecnicoAddsDetallesView() {
        controller = new TecnicoAddsDetallesController();

        frame = new JFrame("Detalle de Incidencias - Técnicos");
        frame.setMinimumSize(new Dimension(710, 400));
        String[] columnas = new String[] {"ID","Tipo","Descripción","Localización","Fecha","Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaIncidencias = new JTable(tableModel);
        tablaIncidencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaIncidencias.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaIncidencias.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tablaIncidencias.getColumnModel().getColumn(1).setPreferredWidth(100); // Tipo
        tablaIncidencias.getColumnModel().getColumn(2).setPreferredWidth(300); // Descripción
        tablaIncidencias.getColumnModel().getColumn(3).setPreferredWidth(100); // Localización
        tablaIncidencias.getColumnModel().getColumn(4).setPreferredWidth(140); // Fecha
        tablaIncidencias.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado

        comentarioArea = new JTextArea(5, 40);
        comentarioArea.setLineWrap(true);
        comentarioArea.setWrapStyleWord(true);
        String[] histCols = new String[] {"Fecha","Acción","Usuario","Comentario","Estado"};
        historialModel = new DefaultTableModel(histCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historialTable = new JTable(historialModel);
        historialTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // usar renderer para permitir wrapping en la columna de comentario
        historialTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea ta = new JTextArea(value == null ? "" : value.toString());
                ta.setLineWrap(true);
                ta.setWrapStyleWord(true);
                ta.setOpaque(true);
                if (isSelected) ta.setBackground(table.getSelectionBackground());
                else ta.setBackground(table.getBackground());
                ta.setFont(table.getFont());
                ta.setBorder(null);
                ta.setEditable(false);
                // fijar altura de fila razonable
                table.setRowHeight(row, 60);
                return ta;
            }
        });
        historialTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        historialTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        historialTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        historialTable.getColumnModel().getColumn(3).setPreferredWidth(360);
        historialTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        fechaPicker = new DateTimePicker();
        añadirComentarioButton = new JButton("Añadir Comentario");

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(tablaIncidencias), BorderLayout.CENTER);
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbcHistLabel = new GridBagConstraints();
        gbcHistLabel.insets = new Insets(6, 6, 6, 6);
        gbcHistLabel.gridx = 0; gbcHistLabel.gridy = 0; gbcHistLabel.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Historial (últimos comentarios):"), gbcHistLabel);

        GridBagConstraints gbcHistScroll = new GridBagConstraints();
        gbcHistScroll.insets = new Insets(6, 6, 6, 6);
        gbcHistScroll.gridx = 1; gbcHistScroll.gridy = 0; gbcHistScroll.gridwidth = 2; gbcHistScroll.fill = GridBagConstraints.BOTH; gbcHistScroll.weightx = 1.0; gbcHistScroll.weighty = 0.6;
        JScrollPane historialScroll = new JScrollPane(historialTable);
        historialScroll.setPreferredSize(new Dimension(400, 140));
        panel.add(historialScroll, gbcHistScroll);

        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(6, 6, 6, 6);
        gbcLabel.gridx = 0; gbcLabel.gridy = 1; gbcLabel.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Comentario:"), gbcLabel);

        GridBagConstraints gbcScroll = new GridBagConstraints();
        gbcScroll.insets = new Insets(6, 6, 6, 6);
        gbcScroll.gridx = 1; gbcScroll.gridy = 1; gbcScroll.gridwidth = 2; gbcScroll.fill = GridBagConstraints.BOTH; gbcScroll.weightx = 1.0; gbcScroll.weighty = 0.4;
        JScrollPane comentarioScroll = new JScrollPane(comentarioArea);
        comentarioScroll.setPreferredSize(new Dimension(400, 100));
        panel.add(comentarioScroll, gbcScroll);

        GridBagConstraints gbcFechaLabel = new GridBagConstraints();
        gbcFechaLabel.insets = new Insets(6, 6, 6, 6);
        gbcFechaLabel.gridx = 0; gbcFechaLabel.gridy = 2; gbcFechaLabel.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Fecha y hora:"), gbcFechaLabel);

        GridBagConstraints gbcFecha = new GridBagConstraints();
        gbcFecha.insets = new Insets(6, 6, 6, 6);
        gbcFecha.gridx = 1; gbcFecha.gridy = 2; gbcFecha.anchor = GridBagConstraints.WEST; gbcFecha.weightx = 0.3; gbcFecha.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fechaPicker, gbcFecha);

        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.insets = new Insets(6, 6, 6, 6);
        gbcButton.gridx = 2; gbcButton.gridy = 2; gbcButton.anchor = GridBagConstraints.EAST;
        panel.add(añadirComentarioButton, gbcButton);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        // Cargar incidencias en curso
        refreshIncidencias();

        añadirComentarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = tablaIncidencias.getSelectedRow();
                if (idx < 0) {
                    JOptionPane.showMessageDialog(frame, "Seleccione una incidencia primero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                IncidenciaDTO sel = incidencias.get(idx);
                String comentario = comentarioArea.getText();
                java.time.LocalDateTime fechaSeleccionada = fechaPicker.getDateTimePermissive();
                String fecha = (fechaSeleccionada == null) ? null : fechaSeleccionada.toString();
                try {
                    controller.añadirComentario(sel.getId(), comentario, fecha);
                    JOptionPane.showMessageDialog(frame, "Comentario añadido correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                    comentarioArea.setText("");
                    fechaPicker.clear();
                    refreshIncidencias();
                    cargarHistorialDeSeleccion();
                } catch (ApplicationException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // cuando se cambia la selección de la tabla, cargar historial asociado
        tablaIncidencias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarHistorialDeSeleccion();
        });
        // cuando se selecciona fila en historial, mantener resaltado pero no editar
        historialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        frame.setSize(712, 406);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void refreshIncidencias() {
        // limpiar tabla
        while (tableModel.getRowCount() > 0) tableModel.removeRow(0);
        incidencias = controller.obtenerIncidenciasEnCurso();
        for (IncidenciaDTO d : incidencias) {
            UsuarioDTO tecnico = d.getTecnico();
            String fecha = d.getFechaHoraRegistro() == null ? "" : d.getFechaHoraRegistro().toString();
            String zonaNombre = d.getLocalizacionNombre();
            Object[] fila = new Object[] {
                d.getId(), d.getTipoNombre(), d.getDescripcion(), zonaNombre, fecha, d.getEstadoNombre()
            };
            tableModel.addRow(fila);
        }
    }

    private void cargarHistorialDeSeleccion() {
        // limpiar tabla de historial
        while (historialModel.getRowCount() > 0) historialModel.removeRow(0);
        int idx = tablaIncidencias.getSelectedRow();
        if (idx < 0 || incidencias == null || incidencias.size() <= idx) return;
        IncidenciaDTO sel = incidencias.get(idx);
        try {
            java.util.List<HistorialDTO> historial = controller.obtenerHistorial(sel.getId());
            if (historial == null || historial.isEmpty()) {
                historialModel.addRow(new Object[] {"(Sin historial)", "", "", "", ""});
                return;
            }
            for (HistorialDTO h : historial) {
                Object usuario = h.getUsuario() == null ? "" : h.getUsuario();
                Object estado = h.getEstado() == null ? "" : h.getEstado();
                historialModel.addRow(new Object[] { h.getFecha(), h.getAccion(), usuario, h.getComentario(), estado });
            }
        } catch (Exception ex) {
            historialModel.addRow(new Object[] {"(Error cargando historial: " + ex.getMessage() + ")", "", "", "", ""});
        }
    }
}