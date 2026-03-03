package giis.demo.tkrun.OperadorValidaIncidencias;

import giis.demo.util.Database;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
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
	
	public void validarIncidencia(int idIncidencia, String identificacion) {
		String sql = "UPDATE Incidencia SET estado=? WHERE id=?";
		List<Object> params = new ArrayList<>();
		params.add(Integer.valueOf(2));
		params.add(Integer.valueOf(idIncidencia));
		
		db.executeUpdate(sql, params.toArray());
	}

	public void rechazarIncidencia(int idIncidencia, String identificacion) {
		String sql = "UPDATE Incidencia SET estado=? WHERE id=?";
		List<Object> params = new ArrayList<>();
		params.add(Integer.valueOf(6));
		params.add(Integer.valueOf(idIncidencia));
		
		db.executeUpdate(sql, params.toArray());
	}
	
}