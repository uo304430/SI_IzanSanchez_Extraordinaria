package Izan_33805;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;

public class HistorialView {
	private JFrame frame;
	private JTable table;

	public HistorialView() {
		frame = new JFrame();
		frame.setTitle("Seguimiento de Incidencias");
		frame.setBounds(100, 100, 700, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	public JFrame getFrame() { return frame; }
	public JTable getTable() { return table; }
}