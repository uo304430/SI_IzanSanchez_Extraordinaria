package FacturaIncidencia;

import giis.demo.util.Database;
import java.util.*;

public class FacturaModel {
    private Database db = new Database();

    public FacturaModel() {
        // inicializa DB como en otros modelos
        db.createDatabase(true);
        db.loadDatabase();
        ensureFacturaTable();
    }

    // Crea tabla Factura si no existe
    private void ensureFacturaTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Factura (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "numero VARCHAR(64) UNIQUE, " +
                     "emisor VARCHAR(128), " +
                     "fecha VARCHAR(32), " +
                     "costeTotal VARCHAR(32), " +
                     "descripcionTecnica VARCHAR(512), " +
                     "incidencia INT, " +
                     "FOREIGN KEY(incidencia) REFERENCES Incidencia(id)" +
                     ")";
        db.executeUpdate(sql);
        // aseguranos tabla de líneas/conceptos
        String sqlLinea = "CREATE TABLE IF NOT EXISTS FacturaLinea (" +
                          "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                          "factura INT NOT NULL, " +
                          "descripcion VARCHAR(512), " +
                          "importe VARCHAR(32), " +
                          "FOREIGN KEY(factura) REFERENCES Factura(id)" +
                          ")";
        db.executeUpdate(sqlLinea);
    }

    // Devuelve lista de incidencias con coste > 0 y que no tengan factura ya asociada
    public List<Object[]> getIncidenciasConCosteNoFacturadas() {
        String sql = "SELECT i.id, i.fecha, i.descripcion, i.Coste, u.nombre as tecnico " +
                     "FROM Incidencia i LEFT JOIN Factura f ON i.id = f.incidencia " +
                     "LEFT JOIN Usuarios u ON i.tecnico = u.id " +
                     "WHERE CAST(i.Coste AS INTEGER) > 0 AND f.id IS NULL";
        return db.executeQueryArray(sql);
    }

    // Devuelve lista de incidencias con coste > 0 y si ya están facturadas (1) o no (0)
    public List<Object[]> getIncidenciasConCoste(boolean soloNoFacturadas) {
        if (soloNoFacturadas) {
            return getIncidenciasConCosteNoFacturadas();
        }
        String sql = "SELECT i.id, i.fecha, i.descripcion, i.Coste, u.nombre as tecnico, " +
                     "CASE WHEN f.id IS NULL THEN 0 ELSE 1 END as facturada " +
                     "FROM Incidencia i LEFT JOIN Factura f ON i.id = f.incidencia " +
                     "LEFT JOIN Usuarios u ON i.tecnico = u.id " +
                     "WHERE CAST(i.Coste AS INTEGER) > 0";
        return db.executeQueryArray(sql);
    }

    // Comprueba si ya existe factura para una incidencia
    public boolean existeFacturaParaIncidencia(int incidenciaId) {
        String sql = "SELECT COUNT(*) as c FROM Factura WHERE incidencia = ?";
        List<Map<String,Object>> res = db.executeQueryMap(sql, incidenciaId);
        if (res == null || res.isEmpty()) return false;
        Object v = res.get(0).get("c");
        if (v == null) return false;
        try { return Integer.parseInt(v.toString()) > 0; } catch(Exception e) { return false; }
    }

    // Genera número único sencillo (prefijo + timestamp + random)
    private String generarNumeroFactura() {
        return "F-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000);
    }

    // Genera la factura, inserta en Factura y registra en HistorialIncidencia
    // Devuelve la FacturaDTO creada o null si error
    public FacturaDTO generarFacturaParaIncidencia(int incidenciaId, String emisor) {
        // Leer datos de la incidencia
        String sqlInc = "SELECT id, Coste, descr_reparación, estado FROM Incidencia WHERE id = ?";
        List<Map<String,Object>> rows = db.executeQueryMap(sqlInc, incidenciaId);
        if (rows == null || rows.isEmpty()) return null;
        Map<String,Object> inc = rows.get(0);
        String coste = inc.get("Coste") != null ? inc.get("Coste").toString() : "0";
        String descr = inc.get("descr_reparación") != null ? inc.get("descr_reparación").toString() : "";

        // Validación: coste > 0
        int costeInt = 0;
        try { costeInt = Integer.parseInt(coste); } catch(Exception e) { costeInt = 0; }
        if (costeInt <= 0) return null;

        // Comprobamos que no exista factura
        if (existeFacturaParaIncidencia(incidenciaId)) return null;

        String numero = generarNumeroFactura();
        String fecha = java.time.LocalDate.now().toString();

        String insert = "INSERT INTO Factura(numero, emisor, fecha, costeTotal, descripcionTecnica, incidencia) VALUES (?, ?, ?, ?, ?, ?)";
        db.executeUpdate(insert, numero, emisor, fecha, Integer.toString(costeInt), descr, incidenciaId);

        // obtener id de la factura insertada para añadir líneas/conceptos
        List<Map<String,Object>> inserted = db.executeQueryMap("SELECT id FROM Factura WHERE numero = ?", numero);
        Integer facturaId = null;
        if (inserted != null && !inserted.isEmpty()) {
            Object idv = inserted.get(0).get("id");
            try { facturaId = Integer.parseInt(idv.toString()); } catch(Exception ex) { facturaId = null; }
        }
        // Insertar línea única por defecto con la descripción técnica y el importe total
        if (facturaId != null) {
            String insertLinea = "INSERT INTO FacturaLinea(factura, descripcion, importe) VALUES (?, ?, ?)";
            db.executeUpdate(insertLinea, facturaId, descr, Integer.toString(costeInt));
        }

        // Registrar en historial
        String insertHist = "INSERT INTO HistorialIncidencia (incidencia, fecha, accion, usuario, comentario, estado) " +
                            "VALUES (?, datetime('now'), ?, COALESCE((SELECT id FROM Usuarios WHERE LOWER(dni)=LOWER(?) OR LOWER(email)=LOWER(?) OR LOWER(nombre)=LOWER(?)), 1), ?, ?)";
        // Usamos la misma identificacion (dni/email/nombre) para intentar recuperar el id; si no existe, usamos 1
        Object estado = inc.get("estado");
        int estadoInt = 0;
        try { estadoInt = Integer.parseInt(estado.toString()); } catch(Exception e) { estadoInt = 0; }
        db.executeUpdate(insertHist, incidenciaId, "Facturada", emisor, emisor, emisor, "Factura generada: " + numero, estadoInt);

        // Construimos DTO con datos
        FacturaDTO dto = new FacturaDTO();
        // obtener id de la factura insertada
        inserted = db.executeQueryMap("SELECT id, numero, emisor, fecha, costeTotal, descripcionTecnica, incidencia FROM Factura WHERE numero = ?", numero);
        if (inserted != null && !inserted.isEmpty()) {
            Map<String,Object> f = inserted.get(0);
            try { dto.setId(Integer.parseInt(f.get("id").toString())); } catch(Exception e) { dto.setId(null); }
            dto.setNumero((String)f.get("numero"));
            dto.setEmisor((String)f.get("emisor"));
            dto.setFecha((String)f.get("fecha"));
            dto.setCosteTotal((String)f.get("costeTotal"));
            dto.setDescripcionTecnica((String)f.get("descripcionTecnica"));
            try { dto.setIncidenciaId(f.get("incidencia") != null ? Integer.parseInt(f.get("incidencia").toString()) : null); } catch(Exception e) { dto.setIncidenciaId(null); }
        }
        // Recuperar líneas/ conceptos y añadir al DTO
        if (facturaId != null) {
            List<Map<String,Object>> filas = db.executeQueryMap("SELECT id, descripcion, importe FROM FacturaLinea WHERE factura = ?", facturaId);
            if (filas != null) {
                for (Map<String,Object> row : filas) {
                    ConceptoDTO c = new ConceptoDTO();
                    try { c.setId(row.get("id")!=null?Integer.parseInt(row.get("id").toString()):null); } catch(Exception ex) { c.setId(null); }
                    c.setFacturaId(facturaId);
                    c.setDescripcion(row.get("descripcion")!=null?row.get("descripcion").toString():"");
                    c.setImporte(row.get("importe")!=null?row.get("importe").toString():"");
                    dto.addConcepto(c);
                }
            }
        }
        return dto;
    }

    // Recupera una factura por id
    public FacturaDTO getFacturaById(int id) {
        List<Map<String,Object>> res = db.executeQueryMap("SELECT id, numero, emisor, fecha, costeTotal, descripcionTecnica, incidencia FROM Factura WHERE id = ?", id);
        if (res == null || res.isEmpty()) return null;
        Map<String,Object> f = res.get(0);
        FacturaDTO dto = new FacturaDTO();
        try { dto.setId(Integer.parseInt(f.get("id").toString())); } catch(Exception e) { dto.setId(null); }
        dto.setNumero((String)f.get("numero"));
        dto.setEmisor((String)f.get("emisor"));
        dto.setFecha((String)f.get("fecha"));
        dto.setCosteTotal((String)f.get("costeTotal"));
        dto.setDescripcionTecnica((String)f.get("descripcionTecnica"));
        try { dto.setIncidenciaId(f.get("incidencia") != null ? Integer.parseInt(f.get("incidencia").toString()) : null); } catch(Exception e) { dto.setIncidenciaId(null); }
        // recuperar conceptos asociados
        List<Map<String,Object>> filas = db.executeQueryMap("SELECT id, descripcion, importe FROM FacturaLinea WHERE factura = ?", dto.getId());
        if (filas != null) {
            for (Map<String,Object> row : filas) {
                ConceptoDTO c = new ConceptoDTO();
                try { c.setId(row.get("id")!=null?Integer.parseInt(row.get("id").toString()):null); } catch(Exception ex) { c.setId(null); }
                c.setFacturaId(dto.getId());
                c.setDescripcion(row.get("descripcion")!=null?row.get("descripcion").toString():"");
                c.setImporte(row.get("importe")!=null?row.get("importe").toString():"");
                dto.addConcepto(c);
            }
        }
        return dto;
    }

    // Recupera una factura por incidencia
    public FacturaDTO getFacturaByIncidencia(int incidenciaId) {
        List<Map<String,Object>> res = db.executeQueryMap("SELECT id FROM Factura WHERE incidencia = ?", incidenciaId);
        if (res == null || res.isEmpty()) return null;
        Object idv = res.get(0).get("id");
        if (idv == null) return null;
        try {
            int id = Integer.parseInt(idv.toString());
            return getFacturaById(id);
        } catch(Exception e) {
            return null;
        }
    }

    // BORRADO DE FACTURAS (uso administrativo/testing): elimina facturas y entradas de historial generadas por facturas
    public void borrarTodasFacturas() {
        db.executeUpdate("PRAGMA foreign_keys = OFF");
        try {
            db.executeUpdate("DELETE FROM FacturaLinea");
        } catch (Exception e) {
            // ignorar si no existe
        }
        db.executeUpdate("DELETE FROM Factura");
        db.executeUpdate("DELETE FROM HistorialIncidencia WHERE comentario LIKE 'Factura generada:%'");
        db.executeUpdate("PRAGMA foreign_keys = ON");
    }
    public Database getDb() {
        return db;
    }
}