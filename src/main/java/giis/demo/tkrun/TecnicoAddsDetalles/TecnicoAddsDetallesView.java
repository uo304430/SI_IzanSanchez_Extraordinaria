package giis.demo.tkrun.TecnicoAddsDetalles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.util.ApplicationException;
import com.github.lgooddatepicker.components.DateTimePicker;

public class TecnicoAddsDetallesView {
    private JFrame frame;
    private JList<String> listaIncidencias;
    private DefaultListModel<String> listModel;
    private JTextArea comentarioArea;
    private DateTimePicker fechaPicker;
    private JButton añadirComentarioButton;
    private TecnicoAddsDetallesController controller;
    private List<IncidenciaDTO> incidencias;

    public TecnicoAddsDetallesView() {
        controller = new TecnicoAddsDetallesController();

        frame = new JFrame("Detalle de Incidencias - Técnicos");
        frame.setMinimumSize(new Dimension(710, 400));
        listModel = new DefaultListModel<>();
        listaIncidencias = new JList<>(listModel);
        listaIncidencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        comentarioArea = new JTextArea(5, 40);
        comentarioArea.setLineWrap(true);
        comentarioArea.setWrapStyleWord(true);
        fechaPicker = new DateTimePicker();
        añadirComentarioButton = new JButton("Añadir Comentario");

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(listaIncidencias), BorderLayout.CENTER);
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(6, 6, 6, 6);
        gbcLabel.gridx = 0; gbcLabel.gridy = 0; gbcLabel.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Comentario:"), gbcLabel);

        GridBagConstraints gbcScroll = new GridBagConstraints();
        gbcScroll.insets = new Insets(6, 6, 6, 6);
        gbcScroll.gridx = 1; gbcScroll.gridy = 0; gbcScroll.gridwidth = 2; gbcScroll.fill = GridBagConstraints.BOTH; gbcScroll.weightx = 1.0; gbcScroll.weighty = 1.0;
        JScrollPane comentarioScroll = new JScrollPane(comentarioArea);
        comentarioScroll.setPreferredSize(new Dimension(400, 100));
        panel.add(comentarioScroll, gbcScroll);

        GridBagConstraints gbcFechaLabel = new GridBagConstraints();
        gbcFechaLabel.insets = new Insets(6, 6, 6, 6);
        gbcFechaLabel.gridx = 0; gbcFechaLabel.gridy = 1; gbcFechaLabel.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Fecha y hora:"), gbcFechaLabel);

        GridBagConstraints gbcFecha = new GridBagConstraints();
        gbcFecha.insets = new Insets(6, 6, 6, 6);
        gbcFecha.gridx = 1; gbcFecha.gridy = 1; gbcFecha.anchor = GridBagConstraints.WEST; gbcFecha.weightx = 0.3; gbcFecha.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fechaPicker, gbcFecha);

        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.insets = new Insets(6, 6, 6, 6);
        gbcButton.gridx = 2; gbcButton.gridy = 1; gbcButton.anchor = GridBagConstraints.EAST;
        panel.add(añadirComentarioButton, gbcButton);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        // Cargar incidencias en curso
        refreshIncidencias();

        añadirComentarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = listaIncidencias.getSelectedIndex();
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
                } catch (ApplicationException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setSize(712, 406);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void refreshIncidencias() {
        listModel.clear();
        incidencias = controller.obtenerIncidenciasEnCurso();
        for (IncidenciaDTO d : incidencias) {
            String text = String.format("%d - %s - %s", d.getId(), d.getTipoNombre(), d.getDescripcion());
            listModel.addElement(text);
        }
    }
}