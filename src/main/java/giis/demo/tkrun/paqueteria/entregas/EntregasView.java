package giis.demo.tkrun.paqueteria.entregas;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EntregasView extends JFrame {

    private JLabel lblCabecera;
    private JTable tblEntregas;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar;
    private JButton btnRefrescar;
    private JButton btnCerrar;

    private static final String[] COLUMNAS = {
        "Envio", "Destinatario", "Telefono", "Direccion", "Fecha prevista", "Intento"
    };

    public EntregasView() {
        setTitle("Mis entregas — PDA Transportista");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new MigLayout("insets 15, wrap 1", "[grow, fill]"));

        lblCabecera = new JLabel("Entregas asignadas");
        lblCabecera.setFont(lblCabecera.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(lblCabecera);

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEntregas = new JTable(modeloTabla);
        tblEntregas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEntregas.setRowHeight(22);
        tblEntregas.getTableHeader().setReorderingAllowed(false);
        tblEntregas.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblEntregas.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblEntregas.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblEntregas.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblEntregas.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblEntregas.getColumnModel().getColumn(5).setPreferredWidth(55);

        JScrollPane scroll = new JScrollPane(tblEntregas);
        scroll.setPreferredSize(new Dimension(780, 200));
        panel.add(scroll);

        JPanel botonesPanel = new JPanel(new MigLayout("insets 0", "[][]push[]"));
        btnRegistrar  = new JButton("Registrar entrega");
        btnRefrescar  = new JButton("Refrescar");
        btnCerrar     = new JButton("Cerrar");
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnRefrescar);
        botonesPanel.add(btnCerrar);
        panel.add(botonesPanel, "growx");

        add(panel);
        setSize(840, 320);
        setLocationRelativeTo(null);
    }

    public void setCabecera(String matricula) {
        lblCabecera.setText("Entregas asignadas — Vehiculo " + matricula);
    }

    public void poblarTabla(List<EntregaListadoDto> lista) {
        modeloTabla.setRowCount(0);
        for (EntregaListadoDto dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getCodigoEnvio(), dto.getDestinatario(), dto.getTelefono(),
                dto.getDireccion(), dto.getFechaPrevista(), dto.getIntentoActual()
            });
        }
    }

    public int getFilaSeleccionada()        { return tblEntregas.getSelectedRow(); }
    public JButton getBtnRegistrar()        { return btnRegistrar; }
    public JButton getBtnRefrescar()        { return btnRefrescar; }
    public JButton getBtnCerrar()           { return btnCerrar; }
}
