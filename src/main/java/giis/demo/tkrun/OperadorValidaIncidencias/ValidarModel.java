package giis.demo.tkrun.OperadorValidaIncidencias;

import giis.demo.util.Database;
import giis.demo.util.ApplicationException;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.Entities.TipoIncidenciaEntity;
// import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.Entities.IncidenciaEntity;
import giis.demo.tkrun.Entities.UsuarioEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidarModel {
	
	private Database db = new Database();
	
	
	public List<IncidenciaDTO> getIncidenciasPorValidar(String identificacion){
		
		IncidenciasModel incM = new IncidenciasModel();
        UsuarioEntity operador = incM.findUsuario(identificacion);
        
        List<IncidenciaDTO> result = new ArrayList<>();
        
        String sql;
        if (operador.getRol()==2) {
            sql = "SELECT * FROM Incidencia WHERE estado=1 ORDER BY fecha DESC";
        } else {
            System.err.println("Este usuario no tiene permisos para ver incidencias en estado Nueva");
            return result;
        }
        
        List<Object> params = new ArrayList<>();
        
        List<IncidenciaEntity> rows = db.executeQueryPojo(IncidenciaEntity.class, sql, params.toArray());
        if (rows == null) return result;
        
        for (IncidenciaEntity e : rows) {
            LocalDateTime fecha = null;
            try {
                if (e.getFecha() != null && !e.getFecha().trim().isEmpty())
                    fecha = LocalDateTime.parse(e.getFecha());
            } catch (Exception ex) {
                fecha = null;
            }
            
            // Propuesta para modificar el constructor y que se pasen los ID de los usuarios, no el objeto
            IncidenciaDTO dto = new IncidenciaDTO(e.getId(), e.getTipo(), e.getDescripcion(), e.getLocalizacion(), null, fecha, e.getEstado());
            result.add(dto);
        }
        return result;
	}

    public List<TipoIncidenciaEntity> getTiposIncidencia() {
        List<TipoIncidenciaEntity> tipos = db.executeQueryPojo(TipoIncidenciaEntity.class, "SELECT id, nombre FROM Tipos ORDER BY nombre");
        return tipos == null ? new ArrayList<>() : tipos;
    }
	
	public void validarIncidencia(int idIncidencia, int nuevoTipoId, String identificacion) {
        IncidenciasModel incModel = new IncidenciasModel();
        UsuarioEntity operador = incModel.findUsuario(identificacion);
        if (operador == null || operador.getRol() != 2) {
            throw new ApplicationException("El usuario identificado no puede validar incidencias.");
        }

        List<IncidenciaEntity> incidencias = db.executeQueryPojo(IncidenciaEntity.class, "SELECT * FROM Incidencia WHERE id=?", Integer.valueOf(idIncidencia));
        if (incidencias == null || incidencias.isEmpty()) {
            throw new ApplicationException("No existe la incidencia seleccionada.");
        }

        IncidenciaEntity actual = incidencias.get(0);
        String updateSql = "UPDATE Incidencia SET tipo=?, estado=?, validación=? WHERE id=?";
        db.executeUpdate(updateSql, Integer.valueOf(nuevoTipoId), Integer.valueOf(2), Boolean.TRUE, Integer.valueOf(idIncidencia));

        String tipoAnterior = getNombreTipo(actual.getTipo());
        String tipoNuevo = getNombreTipo(Integer.valueOf(nuevoTipoId));
        String comentario = "Validada por operador. Tipo anterior: " + tipoAnterior + ". Tipo validado: " + tipoNuevo + ". Estado: Validada.";
        String insertHist = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario,estado) VALUES (?,?,?,?,?,?)";
        db.executeUpdate(insertHist, Integer.valueOf(idIncidencia), LocalDateTime.now().toString(), "Validada",
                Integer.valueOf(operador.getId()), comentario, Integer.valueOf(2));
	}

	public void rechazarIncidencia(int idIncidencia, String identificacion) {
		String sql = "UPDATE Incidencia SET estado=? WHERE id=?";
		List<Object> params = new ArrayList<>();
		params.add(Integer.valueOf(6));
		params.add(Integer.valueOf(idIncidencia));
		
		db.executeUpdate(sql, params.toArray());
	}

    private String getNombreTipo(Integer tipoId) {
        if (tipoId == null) return "";
        List<Object[]> rows = db.executeQueryArray("SELECT nombre FROM Tipos WHERE id=?", tipoId);
        if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0).length == 0 || rows.get(0)[0] == null) {
            return "";
        }
        return rows.get(0)[0].toString();
    }
	
}
