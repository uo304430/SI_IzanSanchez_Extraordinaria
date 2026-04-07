package giis.demo.tkrun.CiudadanoConsulataIncidencias;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import giis.demo.util.Database;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.Entities.IncidenciaEntity;
import giis.demo.tkrun.Entities.UsuarioEntity;

/**
 * Modelo para consultar incidencias de un ciudadano.
 */
public class ConsultaModel {
    private Database db = new Database();

    /**
     * Devuelve la lista de incidencias registradas por el usuario identificado
     * por email o dni. Si estadoFilter es null o <=0 se devuelven todos los estados.
     */
    public List<IncidenciaDTO> getIncidenciasByUsuario(String emailOrDni, Integer estadoFilter) {
        // Reutilizar la lógica de identificación de usuario de IncidenciasModel
        IncidenciasModel im = new IncidenciasModel();
        UsuarioEntity usuario = im.findUsuario(emailOrDni);
        int uid = usuario.getId();

        String sql;
        List<Object> params = new ArrayList<>();
        params.add(Integer.valueOf(uid));
        if (estadoFilter == null || estadoFilter.intValue() <= 0) {
            sql = "SELECT * FROM Incidencia WHERE usuario=? ORDER BY fecha DESC";
        } else {
            sql = "SELECT * FROM Incidencia WHERE usuario=? AND estado=? ORDER BY fecha DESC";
            params.add(Integer.valueOf(estadoFilter.intValue()));
        }

        // Ejecutar consulta y convertir a DTOs
        List<IncidenciaEntity> rows = db.executeQueryPojo(IncidenciaEntity.class, sql, params.toArray());
        List<IncidenciaDTO> result = new ArrayList<>();
        if (rows == null) return result;

        UsuarioDTO ciudadano = new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getDni(), usuario.getRol());

        for (IncidenciaEntity e : rows) {
            LocalDateTime fecha = null;
            try {
                if (e.getFecha() != null && !e.getFecha().trim().isEmpty())
                    fecha = LocalDateTime.parse(e.getFecha());
            } catch (Exception ex) {
                fecha = null;
            }
            IncidenciaDTO dto = new IncidenciaDTO(e.getId(), e.getTipo(), e.getDescripcion(), e.getLocalizacion(), ciudadano, fecha, e.getEstado());
            result.add(dto);
        }
        return result;
    }

    /**
     * Devuelve la lista de estados (id,nombre) existentes en la BD.
     */
    public List<Object[]> getAllEstados() {
        return db.executeQueryArray("SELECT id,nombre FROM Estados ORDER BY id");
    }

        public static String nombreDeEstado(Integer id) {
        if (id == null) return "";
        try {
            Database db = new Database();
            List<Object[]> rows = db.executeQueryArray("SELECT nombre FROM Estados WHERE id=?", id);
            if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0).length == 0 || rows.get(0)[0] == null)
                return "";
            return rows.get(0)[0].toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String nombreDeTipo(Integer id) {
        if (id == null) return "";
        try {
            Database db = new Database();
            List<Object[]> rows = db.executeQueryArray("SELECT nombre FROM Tipos WHERE id=?", id);
            if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0).length == 0 || rows.get(0)[0] == null)
                return "";
            return rows.get(0)[0].toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String nombreDeZona(Integer id) {
        if (id == null) return "";
        try {
            Database db = new Database();
            List<Object[]> rows = db.executeQueryArray("SELECT descripcion FROM Zonas WHERE id=?", id);
            if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0).length == 0 || rows.get(0)[0] == null)
                return "";
            return rows.get(0)[0].toString();
        } catch (Exception e) {
            return "";
        }
    }
}
