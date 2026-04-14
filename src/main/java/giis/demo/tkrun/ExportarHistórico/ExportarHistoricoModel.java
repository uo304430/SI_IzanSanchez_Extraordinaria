package giis.demo.tkrun.ExportarHistórico;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import giis.demo.util.ApplicationException;
import giis.demo.util.Database;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Modelo para exportar el historial de incidencias a JSON.
 */
public class ExportarHistoricoModel {
    private Database db = new Database();

    public List<Map<String, Object>> getIncidenciasWithHistorial(String fromDate, String toDate, Integer tipo, Integer zona) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT i.id, i.tipo, i.descripcion, i.localizacion as zona, i.usuario as usuario_creador, i.tecnico, i.Coste as coste, i.descr_reparación as descr_reparacion, i.fecha, i.estado FROM Incidencia i");
        List<Object> params = new ArrayList<>();
        List<String> where = new ArrayList<>();
        if (fromDate != null && !fromDate.isBlank()) { where.add("i.fecha >= ?"); params.add(fromDate); }
        if (toDate != null && !toDate.isBlank()) { where.add("i.fecha <= ?"); params.add(toDate); }
        if (tipo != null) { where.add("i.tipo = ?"); params.add(tipo); }
        if (zona != null) { where.add("i.localizacion = ?"); params.add(zona); }
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where));
        }
        sql.append(" ORDER BY i.fecha ASC");

        List<Map<String, Object>> incidencias = db.executeQueryMap(sql.toString(), params.toArray());
        if (incidencias == null) incidencias = new ArrayList<>();

        // por cada incidencia obtengo su historial completo
        for (Map<String, Object> inc : incidencias) {
            Object idObj = inc.get("id");
            if (idObj == null) {
                inc.put("historial", new ArrayList<>());
                continue;
            }
            Integer id = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.valueOf(idObj.toString());
            String hsql = "SELECT id, fecha, accion, usuario, comentario, estado FROM HistorialIncidencia WHERE incidencia=? ORDER BY fecha ASC";
            List<Map<String, Object>> historial = db.executeQueryMap(hsql, Integer.valueOf(id));
            if (historial == null) historial = new ArrayList<>();
            inc.put("historial", historial);
        }

        return incidencias;
    }

    public File writeIncidenciasAsJson(List<Map<String, Object>> incidencias, File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, incidencias);
            return file;
        } catch (IOException e) {
            throw new ApplicationException("Error escribiendo fichero JSON: " + e.getMessage());
        }
    }

    public String defaultFileName() {
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String ts = java.time.LocalDateTime.now().format(dtf);
        return "incidencias_historial_" + ts + ".json";
    }

    public Map<Integer,String> getTipoOptions() {
        Map<Integer,String> m = new LinkedHashMap<>();
        List<Map<String,Object>> rows = db.executeQueryMap("SELECT id,nombre FROM Tipos ORDER BY id");
        if (rows!=null) for (Map<String,Object> r: rows) {
            Object id = r.get("id"); Object nombre = r.get("nombre");
            Integer iid = (id instanceof Number)? ((Number)id).intValue() : Integer.valueOf(id.toString());
            m.put(iid, nombre==null? "" : nombre.toString());
        }
        return m;
    }

    public Map<Integer,String> getZonaOptions() {
        Map<Integer,String> m = new LinkedHashMap<>();
        List<Map<String,Object>> rows = db.executeQueryMap("SELECT id,descripcion as nombre FROM Zonas ORDER BY id");
        if (rows!=null) for (Map<String,Object> r: rows) {
            Object id = r.get("id"); Object nombre = r.get("nombre");
            Integer iid = (id instanceof Number)? ((Number)id).intValue() : Integer.valueOf(id.toString());
            m.put(iid, nombre==null? "" : nombre.toString());
        }
        return m;
    }
}
