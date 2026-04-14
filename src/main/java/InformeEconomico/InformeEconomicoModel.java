package InformeEconomico;

import java.util.*;
import giis.demo.util.Database;

public class InformeEconomicoModel {
    private Database db = new Database();

    public InformeEconomicoModel() {
        db.createDatabase(true);
        db.loadDatabase();
    }

    /**
     * Devuelve estadísticas por tipo: número de incidencias, coste medio y coste total.
     * Los alias deben coincidir con los nombres de los atributos del DTO para mapeo.
     */
    public List<InformeEconomicoDTO> getCostesPorTipo(String desde, String hasta) {
        String sql = "SELECT t.nombre as tipo, " +
                     "COUNT(i.id) as numeroIncidencias, " +
                     "AVG(CAST(i.Coste AS NUMERIC)) as costeMedio, " +
                     "SUM(CAST(i.Coste AS NUMERIC)) as costeTotal " +
                     "FROM Incidencia i " +
                     "JOIN Tipos t ON i.tipo = t.id " +
                     "WHERE i.fecha BETWEEN ? AND ? " +
                     "GROUP BY t.nombre";
        return db.executeQueryPojo(InformeEconomicoDTO.class, sql, new Object[]{desde, hasta});
    }

    /**
     * Calcula detalles de presupuesto: importe consumido por todas las incidencias y porcentaje
     * respecto al presupuesto total.
     * Devuelve un Map con claves: presupuestoTotal, importeConsumido, porcentajeConsumido (como Strings).
     */
    public Map<String, String> getDetallesPresupuesto(String presupuestoTotalStr) {
        double presupuestoTotal = 0.0;
        try {
            presupuestoTotal = Double.parseDouble(presupuestoTotalStr);
        } catch (NumberFormatException e) {
            presupuestoTotal = 0.0;
        }

        String sql = "SELECT COALESCE(SUM(CAST(Coste AS NUMERIC)),0) as importeConsumido FROM Incidencia";
        List<Map<String, Object>> res = db.executeQueryMap(sql);
        double importeConsumido = 0.0;
        if (!res.isEmpty() && res.get(0).get("importeConsumido") != null) {
            Object val = res.get(0).get("importeConsumido");
            if (val instanceof Number) {
                importeConsumido = ((Number)val).doubleValue();
            } else {
                try { importeConsumido = Double.parseDouble(val.toString()); } catch (Exception ex) { importeConsumido = 0.0; }
            }
        }

        double porcentaje = 0.0;
        if (presupuestoTotal > 0) {
            porcentaje = (importeConsumido / presupuestoTotal) * 100.0;
        }

        Map<String, String> out = new HashMap<>();
        out.put("presupuestoTotal", String.format("%.2f", presupuestoTotal));
        out.put("importeConsumido", String.format("%.2f", importeConsumido));
        out.put("porcentajeConsumido", String.format("%.2f", porcentaje));
        return out;
    }
}