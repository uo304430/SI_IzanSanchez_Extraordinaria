package EstadisticasIncidencias;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class InformeView {
    private JFrame frame;
    private JTable tabla;
    private JTextField txtDesde, txtHasta;
    private JComboBox<String> cbTipo, cbEstado, cbZona; // txtZona ahora es cbZona
    private JButton btnGenerar;

    public InformeView() {
        frame = new JFrame("Informe Temporal de Incidencias");
        frame.setBounds(100, 100, 850, 550);
        frame.getContentPane().setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][][][grow]"));

        // Fila 0: Fechas
        frame.getContentPane().add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), "cell 0 0");
        txtDesde = new JTextField("2010-01-01");
        frame.getContentPane().add(txtDesde, "growx");

        frame.getContentPane().add(new JLabel("Fecha Fin (YYYY-MM-DD):"), "cell 2 0");
        txtHasta = new JTextField("2026-12-31");
        frame.getContentPane().add(txtHasta, "growx");

        // Fila 1: Filtros desplegables
        frame.getContentPane().add(new JLabel("Tipo:"), "cell 0 1");
        cbTipo = new JComboBox<>(new String[]{"Todos"});
        frame.getContentPane().add(cbTipo, "growx");

        frame.getContentPane().add(new JLabel("Estado:"), "cell 1 1");
        cbEstado = new JComboBox<>(new String[]{"Todos"});
        frame.getContentPane().add(cbEstado, "growx");

        // Cambio: JTextField -> JComboBox para Zona
        frame.getContentPane().add(new JLabel("Zona:"), "cell 2 1");
        cbZona = new JComboBox<>(new String[]{"Todas"});
        frame.getContentPane().add(cbZona, "growx");

        btnGenerar = new JButton("Generar Informe");
        frame.getContentPane().add(btnGenerar, "cell 3 1, growx");

        // Fila 3: Tabla de resultados
        tabla = new JTable();
        frame.getContentPane().add(new JScrollPane(tabla), "cell 0 3 4 1, grow");
    }

    // Getters actualizados
    public JFrame getFrame() { return frame; }
    public JTable getTabla() { return tabla; }
    public JButton getBtnGenerar() { return btnGenerar; }
    public String getDesde() { return txtDesde.getText(); }
    public String getHasta() { return txtHasta.getText(); }
    
    // Ahora devuelve el String seleccionado en el combo
    public String getZona() { return cbZona.getSelectedItem().toString(); }
    
    public JComboBox<String> getCbTipo() { return cbTipo; }
    public JComboBox<String> getCbEstado() { return cbEstado; }
    public JComboBox<String> getCbZona() { return cbZona; } // Nuevo getter para el controlador
}