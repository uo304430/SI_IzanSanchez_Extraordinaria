package giis.demo.tkrun.CiudadanoConsulataIncidencias;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import giis.demo.util.Database;
import giis.demo.util.ApplicationException;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.Entities.HistorialIncidenciaEntity;
import giis.demo.tkrun.Entities.IncidenciaEntity;
import giis.demo.tkrun.Entities.UsuarioEntity;

/**
 * Modelo para consultar incidencias de un ciudadano.
 */
public class ConsultaModel {
    private static final int ESTADO_NUEVA = 1;
    private static final int ESTADO_CERRADA = 6;
    private static final int ESTADO_RECHAZADA = 7;

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

    public Map<Integer, String> getMotivosFinales(List<IncidenciaDTO> incidencias) {
        Map<Integer, String> motivos = new HashMap<>();
        if (incidencias == null || incidencias.isEmpty()) {
            return motivos;
        }

        StringBuilder inClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (IncidenciaDTO incidencia : incidencias) {
            if (incidencia == null || incidencia.getId() == null) {
                continue;
            }
            if (inClause.length() > 0) {
                inClause.append(",");
            }
            inClause.append("?");
            params.add(incidencia.getId());
        }
        if (inClause.length() == 0) {
            return motivos;
        }

        params.add(Integer.valueOf(ESTADO_CERRADA));
        params.add(Integer.valueOf(ESTADO_RECHAZADA));

        String sql = "SELECT id, incidencia, fecha, accion, usuario, comentario, estado "
                + "FROM HistorialIncidencia "
                + "WHERE incidencia IN (" + inClause + ") AND estado IN (?,?) "
                + "ORDER BY incidencia ASC, fecha DESC, id DESC";
        List<HistorialIncidenciaEntity> rows = db.executeQueryPojo(HistorialIncidenciaEntity.class, sql, params.toArray());
        if (rows == null) {
            return motivos;
        }

        for (HistorialIncidenciaEntity row : rows) {
            if (row.getIncidencia() == null || motivos.containsKey(row.getIncidencia())) {
                continue;
            }
            motivos.put(row.getIncidencia(), row.getComentario() == null ? "" : row.getComentario());
        }
        return motivos;
    }

    public boolean isReabrible(Integer estadoId) {
        return Integer.valueOf(ESTADO_CERRADA).equals(estadoId) || Integer.valueOf(ESTADO_RECHAZADA).equals(estadoId);
    }

    public void reabrirIncidencia(int incidenciaId, String identificacion, String motivoReapertura) {
        if (motivoReapertura == null || motivoReapertura.trim().isEmpty()) {
            throw new ApplicationException("El motivo de la reapertura es obligatorio.");
        }

        IncidenciasModel im = new IncidenciasModel();
        UsuarioEntity usuario = im.findUsuario(identificacion);

        List<Object[]> incidencias = db.executeQueryArray("SELECT usuario, estado FROM Incidencia WHERE id=?", Integer.valueOf(incidenciaId));
        if (incidencias == null || incidencias.isEmpty()) {
            throw new ApplicationException("No existe la incidencia seleccionada.");
        }

        Object usuarioIncidencia = incidencias.get(0)[0];
        Object estadoIncidencia = incidencias.get(0)[1];
        int usuarioId = usuarioIncidencia instanceof Number ? ((Number) usuarioIncidencia).intValue() : -1;
        int estadoId = estadoIncidencia instanceof Number ? ((Number) estadoIncidencia).intValue() : -1;

        if (usuarioId != usuario.getId()) {
            throw new ApplicationException("La incidencia seleccionada no pertenece al ciudadano autenticado.");
        }
        if (!isReabrible(Integer.valueOf(estadoId))) {
            throw new ApplicationException("Solo se pueden reabrir incidencias cerradas o rechazadas.");
        }

        db.executeUpdate("UPDATE Incidencia SET estado=? WHERE id=?", Integer.valueOf(ESTADO_NUEVA), Integer.valueOf(incidenciaId));

        String insert = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario,estado) VALUES (?,?,?,?,?,?)";
        db.executeUpdate(insert, Integer.valueOf(incidenciaId), LocalDateTime.now().toString(), "Reapertura por ciudadano",
                Integer.valueOf(usuario.getId()), motivoReapertura.trim(), Integer.valueOf(ESTADO_NUEVA));
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
