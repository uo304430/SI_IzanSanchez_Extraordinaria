package Izan_33805;

import java.util.List;
import giis.demo.util.*;

public class HistorialModel {
    private Database db = new Database();

    public HistorialModel() {
        // Asegurar que las tablas están presentes antes de realizar consultas
        List<Object[]> tables = db.executeQueryArray(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='Incidencia'");
        boolean incidenciaExists = (tables != null && !tables.isEmpty());
        if (!incidenciaExists) {
            db.createDatabase(false);
            db.loadDatabase();
        }
    }

    // Método para el listado inicial
    public List<HistorialDTO> getListaSeleccion() {
        return db.executeQueryPojo(HistorialDTO.class, 
               "SELECT id, descripcion FROM Incidencia ORDER BY id ASC");
    }

    
    public List<HistorialDTO> getHistorialPorId(int idIncidencia) {
        String sql = "SELECT fecha as fecha, incidencia as id, " +
                     "comentario as descripcion, estado as estado " +
                     "FROM HistorialIncidencia WHERE incidencia = ? " +
                     "ORDER BY fecha DESC";
        return db.executeQueryPojo(HistorialDTO.class, sql, idIncidencia);
    }
}