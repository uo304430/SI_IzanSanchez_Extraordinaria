package Izan.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import giis.demo.util.Database;
import CierreIncidencias.CierreIncidenciasModel;

public class testCierreConPresupuestoSuficiente {

    @Test
    public void testCierreConPresupuestoSuficiente() {
        // Preparar: inicializar DB y crear las filas necesarias
        Database db = new Database();
        db.createDatabase(true);
        db.loadDatabase();

        // Insertar datos maestros mínimos (Tipos, Zonas, Roles, Estados, Usuarios)
        db.executeUpdate("INSERT OR IGNORE INTO Tipos(id,nombre) VALUES (?,?)", 1, "TipoTest");
        db.executeUpdate("INSERT OR IGNORE INTO Zonas(id,descripcion) VALUES (?,?)", 1, "ZonaTest");
        db.executeUpdate("INSERT OR IGNORE INTO Roles(id,nombre) VALUES (?,?)", 1, "RoleTest");
        // Estados: 5 = Resuelta (estado previo), 6 = Cerrada (estado esperado)
        db.executeUpdate("INSERT OR IGNORE INTO Estados(id,nombre) VALUES (?,?)", 5, "Resuelta");
        db.executeUpdate("INSERT OR IGNORE INTO Estados(id,nombre) VALUES (?,?)", 6, "Cerrada");
        db.executeUpdate("INSERT OR IGNORE INTO Usuarios(id,nombre,email,dni,rol) VALUES (?,?,?,?,?)", 1, "TestUser", "test@example.com", "00000000T", 1);

        // Insertar presupuesto vigente para el tipo 1 con presupuesto 500 y consumido 0
        String inicio = "2000-01-01";
        String fin = "2099-12-31";
        db.executeUpdate("INSERT OR IGNORE INTO Presupuestos(id,tipo,presupuesto,consumido,fecha_inicio,fecha_fin) VALUES (?,?,?,?,?,?)", 1, 1, 500, 0, inicio, fin);

        // Insertar incidencia con id 1000, tipo 1, Coste 100 y estado 5 (Resuelta)
        int incidenciaId = 1000;
        String fecha = java.time.LocalDate.now().toString();
        db.executeUpdate("INSERT OR REPLACE INTO Incidencia(id,tipo,descripcion,localizacion,usuario,tecnico,Coste,descr_reparación,fecha,estado,validación) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                incidenciaId, 1, "Incidencia Test", 1, 1, null, 100, "", fecha, 5, 1);

        // Ejecutar: llamar al modelo de cierre
        CierreIncidenciasModel model = new CierreIncidenciasModel();
        boolean resultado = model.cerrarIncidencia(incidenciaId, "testuser");

        // Comprobar: debe devolver true y la incidencia debe quedar en estado 6
        assertTrue(resultado);

        // Leer estado actualizado directamente de la DB
        java.util.List<java.util.Map<String,Object>> r = db.executeQueryMap("SELECT estado FROM Incidencia WHERE id = ?", incidenciaId);
        int estado = -1;
        if (r != null && !r.isEmpty() && r.get(0).get("estado") != null) {
            try { estado = Integer.parseInt(r.get(0).get("estado").toString()); } catch(Exception e) { estado = -1; }
        }
        assertEquals(6, estado);
    }
}