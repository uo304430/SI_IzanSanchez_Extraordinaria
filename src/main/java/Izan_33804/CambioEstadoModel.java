package Izan_33804;

import java.util.List;

import giis.demo.util.Database;

public class CambioEstadoModel {
    private Database db = new Database();

    public List<IncidenciaDisplayDTO> getIncidenciasAsignadas(String email) {
    
        String sql = "SELECT id, descripcion, estado FROM Incidencia " +
                     "WHERE tecnico = (SELECT id FROM Usuarios WHERE email = ?) " +
                     "AND estado = 3"; // Solo las asignadas
        return db.executeQueryPojo(IncidenciaDisplayDTO.class, sql, email);
    }

    public void planificarIncidencia(int id, String horas, String trabajos) {
        // 1. Cambiamos el estado a 4 (En proceso) y guardamos planificación
        String sqlUpdate = "UPDATE Incidencia SET Coste = ?, descr_reparación = ?, estado = 4 WHERE id = ?";
        db.executeUpdate(sqlUpdate, horas, trabajos, id);

        // 2. Registramos el evento en el historial unificado
        String sqlLog = "INSERT INTO HistorialIncidencia (incidencia, accion, comentario, estado) VALUES (?, ?, ?, ?)";
        db.executeUpdate(sqlLog, id, "Planificación técnica", "Planificación técnica: " + trabajos, 4);
    }

    public IncidenciaEntity getIncidencia(int id) {
        String sql = "SELECT id, tipo, descripcion, localizacion, usuario, tecnico, Coste, descr_reparación, fecha, estado, validación FROM Incidencia WHERE id = ?";
        List<IncidenciaEntity> lista = db.executeQueryPojo(IncidenciaEntity.class, sql, id);
        return lista.isEmpty() ? null : lista.get(0);
    }
}