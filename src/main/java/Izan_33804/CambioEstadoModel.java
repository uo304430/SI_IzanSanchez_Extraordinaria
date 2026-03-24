package Izan_33804;

import java.util.List;

import giis.demo.util.Database;

public class CambioEstadoModel {
    private Database db = new Database();

    public List<IncidenciaDisplayDTO> getIncidenciasAsignadas(String dniLogueado) {
        // Usamos un JOIN para conectar la Incidencia con el Usuario
        // Aceptamos tanto DNI como email para mayor flexibilidad (el login puede pasar email o dni)
        // Comparamos en minúsculas para evitar problemas de mayúsculas/minúsculas
        String sql = "SELECT i.id, i.descripcion, i.estado " +
                     "FROM Incidencia i " +
                     "JOIN Usuarios u ON i.tecnico = u.id " +
                     "WHERE (LOWER(u.dni) = LOWER(?) OR LOWER(u.email) = LOWER(?)) AND i.estado IN (2, 3)"; 
                     // Filtramos por estados asignada/validada (2,3) para mostrar tareas pendientes de planificación
        
        // Pasamos el mismo parámetro dos veces: puede ser un DNI o un email
        List<IncidenciaDisplayDTO> lista = db.executeQueryPojo(IncidenciaDisplayDTO.class, sql, dniLogueado, dniLogueado);
        
        System.out.println("DEBUG: Identificador usado para filtrar (dni/email): '" + dniLogueado + "'");
        System.out.println("DEBUG: Incidencias encontradas: " + (lista==null?0:lista.size()));
        
        return lista;
    }

 // 1. Añadimos 'dniTecnico' como parámetro para saber quién firma la acción
 // 1. Añadimos 'dniTecnico' como parámetro para saber quién firma la acción
    public void planificarIncidencia(int id, String horas, String trabajos, String dniTecnico) {
        
        // El UPDATE de la incidencia se queda igual (asegúrate de que los campos coincidan)
        String sqlUpdate = "UPDATE Incidencia SET Coste = ?, descr_reparación = ?, estado = 4 WHERE id = ?";
        db.executeUpdate(sqlUpdate, horas, trabajos, id);

        // 2. CORRECCIÓN DEL INSERT: Añadimos la columna 'usuario'
        // Usamos (SELECT id FROM Usuarios WHERE dni = ? OR email = ?) para obtener el ID numérico a partir del identificador pasado
        String sqlLog = "INSERT INTO HistorialIncidencia (incidencia, accion, comentario, estado, usuario, fecha) " +
                        "VALUES (?, ?, ?, ?, (SELECT id FROM Usuarios WHERE LOWER(dni) = LOWER(?) OR LOWER(email) = LOWER(?)), datetime('now'))";
        
        // Pasamos los 6 parámetros (id, acción, comentario, estado, identificador, identificador)
        db.executeUpdate(sqlLog, 
            id, 
            "Planificación técnica", 
            "Planificación técnica: " + trabajos, 
            4, 
            dniTecnico, // identificador (dni o email)
            dniTecnico  // se pasa dos veces para los dos placeholders
        );
    }
    public IncidenciaEntity getIncidencia(int id) {
        // Mantenemos la consulta tal cual, aquí el '?' sí coincide con el parámetro 'id'
        String sql = "SELECT id, tipo, descripcion, localizacion, usuario, tecnico, Coste, descr_reparación, fecha, estado, validación FROM Incidencia WHERE id = ?";
        List<IncidenciaEntity> lista = db.executeQueryPojo(IncidenciaEntity.class, sql, id);
        return lista.isEmpty() ? null : lista.get(0);
    }
}