package CierreIncidencias;

import giis.demo.util.Database;
import java.util.*;

public class CierreIncidenciasModel {
    private Database db = new Database();

    public CierreIncidenciasModel() {
        db.createDatabase(true);
        db.loadDatabase();
    }

    // Lista incidencias que pueden cerrarse (por ejemplo en estado 'Resuelta' o 'En proceso')
    public List<Object[]> getIncidenciasParaCierre() {
        String sql = "SELECT i.id, i.tipo, i.descripcion, i.fecha, i.Coste, u.nombre as tecnico, i.estado " +
                     "FROM Incidencia i LEFT JOIN Usuarios u ON i.tecnico = u.id " +
                     "WHERE i.estado IN (4,5)"; // 4='En proceso',5='Resuelta'
        return db.executeQueryArray(sql);
    }

    // Obtenemos presupuesto total para un tipo (se asumirá un presupuesto almacenado en otra tabla 'Presupuestos' si existe)
    // Como no hay tabla de presupuestos en schema.sql, asumimos que el presupuesto se pasa como parámetro o se guarda en Roles/Tipos.
    // Implementamos la búsqueda en una tabla Presupuestos opcional; si no existe, devolvemos -1.
    public double getPresupuestoPorTipo(int tipoId) {
        try {
            List<Map<String,Object>> res = db.executeQueryMap("SELECT presupuesto FROM Presupuestos WHERE tipo = ?", tipoId);
            if (res != null && !res.isEmpty() && res.get(0).get("presupuesto") != null) {
                Object v = res.get(0).get("presupuesto");
                if (v instanceof Number) return ((Number)v).doubleValue();
                return Double.parseDouble(v.toString());
            }
        } catch(Exception e) {
            // tabla Presupuestos puede no existir
        }
        return -1.0;
    }

    // Obtiene el presupuesto vigente para un tipo. Si hay exactamente uno vigente devuelve un Map con keys id,presupuesto,consumido; si no hay ninguno o hay >1 devuelve null
    public Map<String,Object> getPresupuestoVigentePorTipo(int tipoId) {
        String sql = "SELECT id, presupuesto, consumido, fecha_inicio, fecha_fin FROM Presupuestos WHERE tipo = ? AND date('now') BETWEEN date(fecha_inicio) AND date(fecha_fin)";
        List<Map<String,Object>> res = null;
        try { res = db.executeQueryMap(sql, tipoId); } catch(Exception e) { return null; }
        if (res == null || res.isEmpty()) return null;
        if (res.size() > 1) return null; // conflicto: más de un presupuesto vigente
        return res.get(0);
    }

    // Calcula importe consumido por todas las incidencias de un tipo (usan la columna Coste)
    public double getImporteConsumidoPorTipo(int tipoId) {
        String sql = "SELECT COALESCE(SUM(CAST(Coste AS NUMERIC)),0) as importeConsumido FROM Incidencia WHERE tipo = ?";
        List<Map<String,Object>> res = db.executeQueryMap(sql, tipoId);
        if (res != null && !res.isEmpty() && res.get(0).get("importeConsumido") != null) {
            Object v = res.get(0).get("importeConsumido");
            if (v instanceof Number) return ((Number)v).doubleValue();
            try { return Double.parseDouble(v.toString()); } catch(Exception e) { return 0.0; }
        }
        return 0.0;
    }

    // Intentamos cerrar la incidencia: comprobaremos el coste frente al presupuesto del tipo
    // Devuelve true si cierre ok, false si excede o error
    public boolean cerrarIncidencia(int incidenciaId, String usuarioIdentificacion) {
        // Leemos la incidencia
        List<Map<String,Object>> res = db.executeQueryMap("SELECT id, tipo, Coste, estado FROM Incidencia WHERE id = ?", incidenciaId);
        if (res == null || res.isEmpty()) return false;
        Map<String,Object> inc = res.get(0);
        int tipo = inc.get("tipo") != null ? Integer.parseInt(inc.get("tipo").toString()) : -1;
        String costeStr = inc.get("Coste") != null ? inc.get("Coste").toString() : "0";
        double coste = 0.0;
        try { coste = Double.parseDouble(costeStr); } catch(Exception e) { coste = 0.0; }

        // Obtenemos presupuesto vigente
        Map<String,Object> presupuestoRow = getPresupuestoVigentePorTipo(tipo);
        if (presupuestoRow == null) {
            // No hay presupuesto vigente o hay conflicto: según la HU es obligatorio que siempre haya un presupuesto vigente,
            // por lo que debemos bloquear el cierre
            return false;
        }

        double presupuesto = 0.0;
        try { presupuesto = presupuestoRow.get("presupuesto") instanceof Number ? ((Number)presupuestoRow.get("presupuesto")).doubleValue() : Double.parseDouble(presupuestoRow.get("presupuesto").toString()); } catch(Exception e) { presupuesto = 0.0; }
        double consumido = 0.0;
        try { consumido = presupuestoRow.get("consumido") instanceof Number ? ((Number)presupuestoRow.get("consumido")).doubleValue() : Double.parseDouble(presupuestoRow.get("consumido").toString()); } catch(Exception e) { consumido = 0.0; }

        // Disponible actual sin contar la incidencia
        double disponible = presupuesto - consumido;

        if (coste > disponible) {
            // excede presupuesto, no cerramos
            return false;
        }

        // actualizamos estado a 6 (Cerrada)
        db.executeUpdate("UPDATE Incidencia SET estado = ? WHERE id = ?", 6, incidenciaId);

        // incrementamos el campo consumido del presupuesto vigente
        try {
            int presupuestoId = presupuestoRow.get("id")!=null?Integer.parseInt(presupuestoRow.get("id").toString()):-1;
            if (presupuestoId != -1) {
                double nuevoConsumido = consumido + coste;
                db.executeUpdate("UPDATE Presupuestos SET consumido = ? WHERE id = ?", nuevoConsumido, presupuestoId);
            }
        } catch(Exception e) {
            // Si falla la actualización del consumido, seguimos adelante pero el sistema debería registrar el error
            e.printStackTrace();
        }

        // registramos en historial la acción
        String sqlHist = "INSERT INTO HistorialIncidencia (incidencia, fecha, accion, usuario, comentario, estado) " +
                         "VALUES (?, datetime('now'), 'Cerrada', COALESCE((SELECT id FROM Usuarios WHERE LOWER(dni)=LOWER(?) OR LOWER(email)=LOWER(?) OR LOWER(nombre)=LOWER(?)),1), ?, ?)";
        db.executeUpdate(sqlHist, incidenciaId, usuarioIdentificacion, usuarioIdentificacion, usuarioIdentificacion,
                         "Cierre validado. Coste final: " + costeStr + ", Presupuesto restante: " + String.format("%.2f", (presupuesto - (consumido + coste))), 6);

        return true;
    }

    // Recupera tipo y coste de una incidencia por id
    public Map<String,Object> getIncidenciaTipoCoste(int incidenciaId) {
        List<Map<String,Object>> res = db.executeQueryMap("SELECT tipo, Coste FROM Incidencia WHERE id = ?", incidenciaId);
        if (res == null || res.isEmpty()) return null;
        return res.get(0);
    }
}