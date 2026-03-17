package giis.demo.tkrun.OperarioRechazaIncidencia;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;

/**
 * Controlador para la ventana de rechazo con motivo.
 */
public class RechazarOperarioController {
	private final RechazarOperarioModel model;
	private final RechazarOperarioView view;
	private final int incidenciaId;
	private final String operadorIdentificacion;
	private final Runnable onSuccess;

	public RechazarOperarioController(RechazarOperarioModel m, RechazarOperarioView v, int incidenciaId, String operadorIdentificacion, Runnable onSuccess) {
		this.model = m;
		this.view = v;
		this.incidenciaId = incidenciaId;
		this.operadorIdentificacion = operadorIdentificacion;
		this.onSuccess = onSuccess;
		initController();
		view.getFrame().setVisible(true);
	}

	private void initController() {
		view.getBtnConfirmar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> confirmarRechazo()));
	}

	private void confirmarRechazo() {
		String motivo = view.getMotivo();
		if (motivo == null || motivo.trim().isEmpty()) {
			view.showWarning("Debe indicar un motivo de rechazo.");
			return;
		}
		model.rechazarIncidencia(incidenciaId, operadorIdentificacion, motivo);
		view.showSuccess();
		view.close();
		if (onSuccess != null) {
			onSuccess.run();
		}
	}
}
