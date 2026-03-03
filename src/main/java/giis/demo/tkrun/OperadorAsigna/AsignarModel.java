package giis.demo.tkrun.OperadorAsigna;

import giis.demo.util.Database;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.Entities.IncidenciaEntity;
import giis.demo.tkrun.Entities.UsuarioEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AsignarModel {
    private Database db = new Database();

    public List<IncidenciaDTO> getIncidenciasParaAsignar(String identificacion) {
        IncidenciasModel incM = new IncidenciasModel();
        UsuarioEntity operador = incM.findUsuario(identificacion);
        if (operador.getRol() != 2) {
            System.err.println("Este usuario no tiene permisos para ver incidencias para asignar");
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM Incidencia WHERE estado=2 ORDER BY fecha ASC";
        List<Object> params = new ArrayList<>();
        List<IncidenciaEntity> rows = db.executeQueryPojo(IncidenciaEntity.class, sql, params.toArray());
        List<IncidenciaDTO> result = new ArrayList<>();
        if (rows == null) return result;

        for (IncidenciaEntity e : rows) {
            LocalDateTime fecha = null;
            try {
                if (e.getFecha() != null && !e.getFecha().trim().isEmpty())
                    fecha = LocalDateTime.parse(e.getFecha());
            } catch (Exception ex) {
                fecha = null;
            }
            IncidenciaDTO dto = new IncidenciaDTO(e.getId(), e.getTipo(), e.getDescripcion(), e.getLocalizacion(), null, fecha, e.getEstado());
            result.add(dto);
        }
        return result;
    }

    public List<UsuarioDTO> getTecnicos() {
        String sql = "SELECT * FROM Usuarios WHERE rol=?";
        List<Object> params = new ArrayList<>();
        params.add(Integer.valueOf(2));
        List<UsuarioEntity> rows = db.executeQueryPojo(UsuarioEntity.class, sql, params.toArray());
        List<UsuarioDTO> result = new ArrayList<>();
        if (rows == null) return result;
        for (UsuarioEntity u : rows) {
            result.add(new UsuarioDTO(u.getId(), u.getNombre(), u.getEmail(), u.getDni(), u.getRol()));
        }
        return result;
    }

    
    public void asignarIncidencia(int idIncidencia, int idTecnico, String operadorIdentificacion) {
        // actualizar incidencia
        String sql = "UPDATE Incidencia SET tecnico=?, estado=? WHERE id=?";
        List<Object> params = new ArrayList<>();
        params.add(Integer.valueOf(idTecnico));
        params.add(Integer.valueOf(3)); // estado Asignada
        params.add(Integer.valueOf(idIncidencia));
        db.executeUpdate(sql, params.toArray());

        // registrar historial
        IncidenciasModel incM = new IncidenciasModel();
        UsuarioEntity operador = incM.findUsuario(operadorIdentificacion);
        int operadorId = operador.getId();

        String insert = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario) VALUES (?,?,?,?,?)";
        List<Object> ip = new ArrayList<>();
        ip.add(Integer.valueOf(idIncidencia));
        String now = LocalDateTime.now().toString();
        ip.add(now);
        ip.add("Asignada");
        ip.add(Integer.valueOf(operadorId));
        ip.add("Asignada al tecnico id=" + idTecnico);
        db.executeUpdate(insert, ip.toArray());
    }
}
