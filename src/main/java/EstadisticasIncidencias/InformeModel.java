package EstadisticasIncidencias;

import java.util.*;
import giis.demo.util.Database;

public class InformeModel {
    private Database db = new Database();

    public InformeModel() {
        // Inicialización automática de la DB al arrancar el modelo
        db.createDatabase(true);
        db.loadDatabase();
    }

    /**
     * Ejecuta la consulta dinámica con todos los JOINs necesarios (Estados, Tipos, Zonas).
     */
    public List<IncidenciaReporteDTO> getInforme(String desde, String hasta, String tipo, String estado, String zonaNombre) {
        StringBuilder sql = new StringBuilder(
            "SELECT i.id, i.fecha, i.descripcion, e.nombre as estado, t.nombre as tipo, z.descripcion as zona " +
            "FROM Incidencia i " +
            "JOIN Estados e ON i.estado = e.id " +
            "JOIN Tipos t ON i.tipo = t.id " +
            "JOIN Zonas z ON i.localizacion = z.id " + 
            "WHERE i.fecha BETWEEN ? AND ?"
        );

        List<Object> params = new ArrayList<>();
        params.add(desde);
        params.add(hasta);

        // Filtro opcional: Tipo
        if (tipo != null && !tipo.equals("Todos")) {
            sql.append(" AND t.nombre = ?");
            params.add(tipo);
        }
        // Filtro opcional: Estado
        if (estado != null && !estado.equals("Todos")) {
            sql.append(" AND e.nombre = ?");
            params.add(estado);
        }
        // Filtro opcional: Zona (por nombre/descripción)
        if (zonaNombre != null && !zonaNombre.equals("Todas")) {
            sql.append(" AND z.descripcion = ?");
            params.add(zonaNombre);
        }

        return db.executeQueryPojo(IncidenciaReporteDTO.class, sql.toString(), params.toArray(new Object[0]));
    }

    /**
     * Método genérico para obtener nombres de filtros (Tipos, Estados).
     * Se usa executeQueryMap para evitar problemas de tipos de la librería.
     */
    public List<String> getFiltros(String tabla) {
        String sql = "SELECT nombre FROM " + tabla;
        List<Map<String, Object>> res = db.executeQueryMap(sql);
        List<String> lista = new ArrayList<>();
        for (Map<String, Object> fila : res) {
            if (fila.get("nombre") != null) {
                lista.add(fila.get("nombre").toString());
            }
        }
        return lista;
    }

    /**
     * Método específico para la tabla Zonas, ya que la columna se llama 'descripcion' en tu DB.
     */
    public List<String> getListaZonas() {
        String sql = "SELECT descripcion FROM Zonas";
        List<Map<String, Object>> res = db.executeQueryMap(sql);
        List<String> lista = new ArrayList<>();
        for (Map<String, Object> fila : res) {
            if (fila.get("descripcion") != null) {
                lista.add(fila.get("descripcion").toString());
            }
        }
        return lista;
    }
}