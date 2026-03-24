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

    public void actualizarRechazo(RechazoDTO dto, String identificacion) {
        // Actualizamos el estado a 6 (Cerrada) para representar el rechazo y guardamos el motivo en la descripción
        String sql = "UPDATE Incidencia SET estado = ?, descripcion = ? WHERE id = ?";
        db.executeUpdate(sql, 6, dto.getMotivo(), dto.getId());

        // Insertamos en historial la acción de rechazo, asociando el usuario por dni o email
        String sqlLog = "INSERT INTO HistorialIncidencia (incidencia, accion, comentario, estado, usuario, fecha) " +
                        "VALUES (?, ?, ?, ?, (SELECT id FROM Usuarios WHERE LOWER(dni)=LOWER(?) OR LOWER(email)=LOWER(?)), datetime('now'))";
        db.executeUpdate(sqlLog, dto.getId(), "Rechazo", dto.getMotivo(), 6, identificacion, identificacion);
    }
}