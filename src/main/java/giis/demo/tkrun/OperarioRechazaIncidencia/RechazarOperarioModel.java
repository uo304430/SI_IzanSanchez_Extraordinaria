package giis.demo.tkrun.OperarioRechazaIncidencia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.Entities.UsuarioEntity;
import giis.demo.util.ApplicationException;
import giis.demo.util.Database;

/**
 * Lógica para rechazar incidencias y registrar el motivo en historial.
 */
public class RechazarOperarioModel {
	private Database db = new Database();

	public void rechazarIncidencia(int idIncidencia, String operadorIdentificacion, String comentario) {
		if (comentario == null || comentario.trim().isEmpty()) {
			throw new ApplicationException("El motivo de rechazo es obligatorio.");
		}

		// obtener operador para registrar en historial
		IncidenciasModel incModel = new IncidenciasModel();
		UsuarioEntity operador = incModel.findUsuario(operadorIdentificacion);
		int operadorId = operador.getId();

		// estado 6 = Cerrada/Rechazada según catálogo existente
		String updateSql = "UPDATE Incidencia SET estado=? WHERE id=?";
		List<Object> params = new ArrayList<>();
		params.add(Integer.valueOf(6));
		params.add(Integer.valueOf(idIncidencia));
		db.executeUpdate(updateSql, params.toArray());

		// registrar historial
		String insert = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario,estado) VALUES (?,?,?,?,?,?)";
		List<Object> ip = new ArrayList<>();
		ip.add(Integer.valueOf(idIncidencia));
		ip.add(LocalDateTime.now().toString());
		ip.add("Rechazada");
		ip.add(Integer.valueOf(operadorId));
		ip.add(comentario.trim());
		ip.add(Integer.valueOf(6));
		db.executeUpdate(insert, ip.toArray());
	}
}
