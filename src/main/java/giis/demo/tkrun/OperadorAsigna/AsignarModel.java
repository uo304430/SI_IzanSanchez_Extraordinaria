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

    /** Devuelve un mapa id->nombre para los técnicos indicados */
    public java.util.Map<Integer,String> getNombresTecnicosByIds(java.util.List<Integer> ids) {
        java.util.Map<Integer,String> m = new java.util.HashMap<>();
        if (ids == null || ids.isEmpty()) return m;
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<ids.size();i++) {
            if (i>0) sb.append(',');
            sb.append('?');
        }
        String sql = "SELECT id,nombre FROM Usuarios WHERE id IN (" + sb.toString() + ")";
        java.util.List<Object> params = new java.util.ArrayList<>();
        for (Integer id: ids) params.add(id);
        List<Object[]> rows = db.executeQueryArray(sql, params.toArray());
        if (rows == null) return m;
        for (Object[] r: rows) {
            if (r.length>1 && r[0] instanceof Number) m.put(((Number)r[0]).intValue(), r[1]==null? null: r[1].toString());
        }
        return m;
    }

    /**
     * Devuelve los técnicos que están asociados al tipo de incidencia indicado
     * junto con su carga total de incidencias asignadas actualmente.
     * Cada fila del resultado contiene: id, nombre, email, carga(Integer).
     */
    public List<Object[]> getTecnicosConCargaParaTipo(Integer tipoId) {
        // Query technicians for the given type and compute their current assignment load
        // using Incidencia_Tecnico join, ordering from least to most assigned.
        String sql = "SELECT u.id,u.nombre,u.email,u.dni,u.rol, COALESCE(c.carga,0) AS carga "
                + "FROM Usuarios u "
                + "JOIN TipoTecnico tt ON u.id=tt.usuario AND tt.tipo=? "
                + "LEFT JOIN (SELECT it.tecnico, COUNT(*) AS carga FROM Incidencia_Tecnico it "
                + "JOIN Incidencia i ON it.incidencia=i.id WHERE i.estado IN (3,4) GROUP BY it.tecnico) c "
                + "ON u.id=c.tecnico "
                + "WHERE tt.tipo=? "
                + "ORDER BY carga ASC";
        List<Object> params = new ArrayList<>();
        params.add(tipoId);
        params.add(tipoId);
        List<Object[]> rows = db.executeQueryArray(sql, params.toArray());
        if (rows == null) return new ArrayList<>();

        // rows already include carga as last column (index 5)
        if (!rows.isEmpty()) System.out.println("Técnicos con carga para el tipo " + tipoId + ": " + rows.get(0)[5]);
        return rows;
    }

    /**
     * Asigna una incidencia a varios técnicos: crea filas en Incidencia_Tecnico,
     * actualiza el estado de la Incidencia a Asignada y registra una única entrada en HistorialIncidencia.
     */
    public void asignarIncidencia(int idIncidencia, java.util.List<Integer> idsTecnicos, String operadorIdentificacion) {
        if (idsTecnicos == null || idsTecnicos.isEmpty()) return;

        // actualizar estado de la incidencia a Asignada (las relaciones con técnicos están en Incidencia_Tecnico)
        String sql = "UPDATE Incidencia SET estado=? WHERE id=?";
        java.util.List<Object> params = new java.util.ArrayList<>();
        params.add(Integer.valueOf(3));
        params.add(Integer.valueOf(idIncidencia));
        db.executeUpdate(sql, params.toArray());

        // insertar filas en Incidencia_Tecnico
        String insertRel = "INSERT INTO Incidencia_Tecnico(incidencia, tecnico) VALUES (?,?)";
        for (Integer t : idsTecnicos) {
            java.util.List<Object> rp = new java.util.ArrayList<>();
            rp.add(Integer.valueOf(idIncidencia));
            rp.add(Integer.valueOf(t));
            db.executeUpdate(insertRel, rp.toArray());
        }

        // registrar historial: una entrada que indica todos los técnicos asignados
        IncidenciasModel incM = new IncidenciasModel();
        giis.demo.tkrun.Entities.UsuarioEntity operador = incM.findUsuario(operadorIdentificacion);
        int operadorId = operador.getId();

        String insert = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario,estado) VALUES (?,?,?,?,?,?)";
        java.util.List<Object> ip = new java.util.ArrayList<>();
        ip.add(Integer.valueOf(idIncidencia));
        String now = java.time.LocalDateTime.now().toString();
        ip.add(now);
        ip.add("Asignada a varios técnicos");
        ip.add(Integer.valueOf(operadorId));
        ip.add("Asignada a tecnicos ids=" + idsTecnicos.toString());
        ip.add(Integer.valueOf(3)); // estado Asignada
        db.executeUpdate(insert, ip.toArray());
    }
}
