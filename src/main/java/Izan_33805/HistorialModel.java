package Izan_33805;

import java.util.List;
import giis.demo.util.*;

public class HistorialModel {
    private DbUtil db = new Database();

    // Método para el listado inicial
    public List<HistorialDTO> getListaSeleccion() {
        return db.executeQueryPojo(HistorialDTO.class, 
               "SELECT id, descripcion FROM Incidencia ORDER BY id ASC");
    }

    
    public List<HistorialDTO> getHistorialPorId(int idIncidencia) {
        String sql = "SELECT fecha_hora as fecha, id_incidencia as id, " +
                     "descripcion_evento as descripcion, estado_alcanzado as estado " +
                     "FROM HistorialIncidencias WHERE id_incidencia = ? " +
                     "ORDER BY fecha_hora DESC";
        return db.executeQueryPojo(HistorialDTO.class, sql, idIncidencia);
    }
}