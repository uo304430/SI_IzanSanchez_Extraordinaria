package giis.demo.tkrun.TecnicoAddsDetalles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import giis.demo.util.ApplicationException;
import giis.demo.util.Database;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.HistorialDTO;
import giis.demo.tkrun.Entities.IncidenciaEntity;

public class TecnicoAddsDetallesModel {
    private Database db = new Database();

    // Estado 4 corresponde a "En proceso" (equivalente a "En curso" en la HU)
    private static final int ESTADO_EN_CURSO = 4;

    public List<IncidenciaDTO> obtenerIncidenciasEnCurso() {
        String sql = "SELECT * FROM Incidencia WHERE estado=? ORDER BY fecha ASC";
        List<IncidenciaEntity> rows = db.executeQueryPojo(IncidenciaEntity.class, sql, Integer.valueOf(ESTADO_EN_CURSO));
        List<IncidenciaDTO> result = new ArrayList<>();
        if (rows == null) return result;
        System.out.println("Incidencias en curso encontradas: " + rows.size());

        for (IncidenciaEntity e : rows) {
            java.time.LocalDateTime fecha = null;
            try {
                if (e.getFecha() != null && !e.getFecha().trim().isEmpty())
                    fecha = LocalDateTime.parse(e.getFecha());
            } catch (Exception ex) {
                fecha = null;
            }
            // No incluimos aquí el objeto UsuarioDTO del ciudadano (similar a AsignarModel)
            IncidenciaDTO dto = new IncidenciaDTO(e.getId(), e.getTipo(), e.getDescripcion(), e.getLocalizacion(), null, fecha, e.getEstado());
            result.add(dto);
        }
        return result;
    }

    public java.util.List<HistorialDTO> obtenerHistorial(int incidenciaId) {
        String sql = "SELECT id, incidencia, fecha, accion, usuario, comentario, estado " +
                     "FROM HistorialIncidencia WHERE incidencia = ? ORDER BY fecha DESC";
        java.util.List<HistorialDTO> rows = db.executeQueryPojo(HistorialDTO.class, sql, Integer.valueOf(incidenciaId));
        if (rows == null) return new ArrayList<>();
        return rows;
    }

    public void añadirComentario(int incidenciaId, String comentario) {
        añadirComentario(incidenciaId, comentario, null);
    }

    public void añadirComentario(int incidenciaId, String comentario, String fechaComentario) {
        if (comentario == null || comentario.trim().isEmpty())
            throw new ApplicationException("El comentario no puede estar vacío");

        // comprobar que la incidencia existe, que está en estado "En proceso" y obtener el id del tecnico
        List<Object[]> rows = db.executeQueryArray("SELECT estado, tecnico FROM Incidencia WHERE id=?", Integer.valueOf(incidenciaId));
        if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0).length == 0 || rows.get(0)[0] == null)
            throw new ApplicationException("Incidencia no encontrada: id=" + incidenciaId);
        Number estadoNum = (Number) rows.get(0)[0];
        int estado = estadoNum == null ? -1 : estadoNum.intValue();
        if (estado != ESTADO_EN_CURSO)
            throw new ApplicationException("No se puede añadir comentario: la incidencia no está en estado 'En curso'");

        // obtener id del técnico asignado a la incidencia
        Integer tecnicoId = null;
        try {
            Object val = rows.get(0)[1];
            if (val != null && val instanceof Number) tecnicoId = Integer.valueOf(((Number) val).intValue());
            else if (val != null) tecnicoId = Integer.valueOf(Integer.parseInt(val.toString()));
        } catch (Exception ex) {
            tecnicoId = null;
        }
        if (tecnicoId == null)
            throw new ApplicationException("No se puede añadir comentario: la incidencia no tiene técnico asignado");

        // interpretar la fecha proporcionada o usar la actual
        String fechaParaInsertar;
        if (fechaComentario == null || fechaComentario.trim().isEmpty()) {
            fechaParaInsertar = LocalDateTime.now().toString();
        } else {
            try {
                LocalDateTime dt;
                try {
                    dt = LocalDateTime.parse(fechaComentario);
                } catch (Exception ex) {
                    java.time.LocalDate d = java.time.LocalDate.parse(fechaComentario);
                    dt = d.atStartOfDay();
                }
                fechaParaInsertar = dt.toString();
            } catch (Exception ex) {
                throw new ApplicationException("Fecha del comentario inválida: " + fechaComentario);
            }
        }

        // registrar comentario en HistorialIncidencia incluyendo el id del tecnico en la columna 'usuario'
        String insert = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario,estado) VALUES (?,?,?,?,?,?)";
        db.executeUpdate(insert, Integer.valueOf(incidenciaId), fechaParaInsertar, "Comentario técnico", Integer.valueOf(tecnicoId), comentario, Integer.valueOf(estado));
    }
}