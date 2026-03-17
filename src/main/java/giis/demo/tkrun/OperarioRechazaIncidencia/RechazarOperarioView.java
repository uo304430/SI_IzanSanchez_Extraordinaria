package giis.demo.tkrun.OperarioRechazaIncidencia;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;

/**
 * Ventana para registrar el motivo de rechazo de una incidencia.
 */
public class RechazarOperarioView {
	private JFrame frame;
	private JTextArea txtMotivo;
	private JButton btnConfirmar;

	public RechazarOperarioView() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Rechazar incidencia");
		frame.setName("RechazarIncidencia");
		frame.setBounds(100, 100, 420, 240);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][]"));

		frame.getContentPane().add(new JLabel("Motivo del rechazo:"), "cell 0 0");

		txtMotivo = new JTextArea(4, 30);
		txtMotivo.setName("txtMotivoRechazo");
		txtMotivo.setLineWrap(true);
		txtMotivo.setWrapStyleWord(true);
		frame.getContentPane().add(new JScrollPane(txtMotivo), "cell 0 1,grow");

		btnConfirmar = new JButton("Confirmar rechazo");
		btnConfirmar.setName("btnConfirmarRechazo");
		frame.getContentPane().add(btnConfirmar, "cell 0 2,alignx center");
	}

	public JFrame getFrame() { return frame; }
	public JButton getBtnConfirmar() { return btnConfirmar; }
	public String getMotivo() { return txtMotivo.getText(); }

	public void showSuccess() {
		JOptionPane.showMessageDialog(frame, "Incidencia rechazada correctamente.", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
	}

	public void showWarning(String msg) {
		JOptionPane.showMessageDialog(frame, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
	}

	public void close() {
		frame.dispose();
	}
}
