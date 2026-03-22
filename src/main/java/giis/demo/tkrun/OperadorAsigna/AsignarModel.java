package giis.demo.tkrun.OperadorAsigna;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.Entities.IncidenciaEntity;
import giis.demo.tkrun.Entities.UsuarioEntity;
import giis.demo.util.Database;

public class AsignarModel {
    private Database db = new Database();

    public List<IncidenciaDTO> getIncidenciasParaAsignar(String identificacion) {
        IncidenciasModel incM = new IncidenciasModel();
        UsuarioEntity operador = incM.findUsuario(identificacion);
        if (operador.getRol() != 2) {
            System.err.println("Este usuario no tiene permisos para ver incidencias para asignar");
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM Incidencia WHERE estado=2 ORDER BY fecha ASC";
        List<Object> params = new ArrayList<>();
        List<IncidenciaEntity> rows = db.executeQueryPojo(IncidenciaEntity.class, sql, params.toArray());
        List<IncidenciaDTO> result = new ArrayList<>();
        if (rows == null) return result;

        for (IncidenciaEntity e : rows) {
            LocalDateTime fecha = null;
            try {
                if (e.getFecha() != null && !e.getFecha().trim().isEmpty())
                    fecha = LocalDateTime.parse(e.getFecha());
            } catch (Exception ex) {
                fecha = null;
            }
            IncidenciaDTO dto = new IncidenciaDTO(e.getId(), e.getTipo(), e.getDescripcion(), e.getLocalizacion(), null, fecha, e.getEstado());
            result.add(dto);
        }
        return result;
    }

    public List<UsuarioDTO> getTecnicos() {
        String sql = "SELECT * FROM Usuarios WHERE rol=?";
        List<Object> params = new ArrayList<>();
        params.add(Integer.valueOf(2));
        List<UsuarioEntity> rows = db.executeQueryPojo(UsuarioEntity.class, sql, params.toArray());
        List<UsuarioDTO> result = new ArrayList<>();
        if (rows == null) return result;
        for (UsuarioEntity u : rows) {
            result.add(new UsuarioDTO(u.getId(), u.getNombre(), u.getEmail(), u.getDni(), u.getRol()));
        }
        return result;
    }

    /**
     * Devuelve los técnicos que están asociados al tipo de incidencia indicado
     * junto con su carga total de incidencias asignadas actualmente.
     * Cada fila del resultado contiene: id, nombre, email, carga(Integer).
     */
    public List<Object[]> getTecnicosConCargaParaTipo(Integer tipoId) {
        String sql = "SELECT * FROM Usuarios u WHERE u.id IN (SELECT usuario FROM TipoTecnico WHERE tipo=?)";
        List<Object> params = new ArrayList<>();
        params.add(tipoId);
        List<Object[]> rows = db.executeQueryArray(sql, params.toArray());
        if (rows == null) return new ArrayList<>();

        String countSql = "SELECT COUNT(*) FROM Incidencia WHERE tecnico=? AND estado IN (3, 4)";
        
        List<Object[]> result = new ArrayList<>();
        for (Object[] row : rows) {
            int tecnicoId = (Integer) row[0];
            List<Object> countParams = new ArrayList<>();
            countParams.add(tecnicoId);
            List<Object[]> countResult = db.executeQueryArray(countSql, countParams.toArray());
            int carga = (countResult != null && !countResult.isEmpty()) ? ((Number) countResult.get(0)[0]).intValue() : 0;
            
            Object[] resultRow = new Object[row.length + 1];
            System.arraycopy(row, 0, resultRow, 0, row.length);
            resultRow[row.length] = carga;
            result.add(resultRow);
        }
        System.out.println("Técnicos con carga para el tipo " + tipoId + ": " + result.get(0)[5]);
        return result;
    }

    
    public void asignarIncidencia(int idIncidencia, int idTecnico, String operadorIdentificacion) {
        // actualizar incidencia
        String sql = "UPDATE Incidencia SET tecnico=?, estado=? WHERE id=?";
        List<Object> params = new ArrayList<>();
        params.add(Integer.valueOf(idTecnico));
        params.add(Integer.valueOf(3)); // estado Asignada
        params.add(Integer.valueOf(idIncidencia));
        db.executeUpdate(sql, params.toArray());

        // registrar historial
        IncidenciasModel incM = new IncidenciasModel();
        UsuarioEntity operador = incM.findUsuario(operadorIdentificacion);
        int operadorId = operador.getId();

        String insert = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario,estado) VALUES (?,?,?,?,?,?)";
        List<Object> ip = new ArrayList<>();
        ip.add(Integer.valueOf(idIncidencia));
        String now = LocalDateTime.now().toString();
        ip.add(now);
        ip.add("Asignada");
        ip.add(Integer.valueOf(operadorId));
        ip.add("Asignada al tecnico id=" + idTecnico);
        ip.add(Integer.valueOf(3)); // estado Asignada
        db.executeUpdate(insert, ip.toArray());
    }
}
