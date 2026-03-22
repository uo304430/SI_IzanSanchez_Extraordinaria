package RechazoIncidencias;

import java.util.*;
import giis.demo.util.Database;

public class RechazoModel {
    private Database db = new Database();

    public List<Object[]> getListaPendientes() {
        // IMPORTANTE: Al ser números (Integer), NO llevan comillas simples en el IN
        String sql = "SELECT id, fecha, descripcion, estado FROM Incidencia WHERE estado IN (3, 4)";
        return db.executeQueryArray(sql);
    }

    public void actualizarRechazo(RechazoDTO dto) {
        
        String sql = "UPDATE Incidencia SET estado = 'Rechazada', descripcion = ? WHERE id = ?";
        db.executeUpdate(sql, dto.getMotivo(), dto.getId());
    }
}