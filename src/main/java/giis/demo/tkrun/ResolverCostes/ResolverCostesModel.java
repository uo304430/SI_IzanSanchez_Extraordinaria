package giis.demo.tkrun.ResolverCostes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Izan_33804.IncidenciaDisplayDTO;
import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.Entities.UsuarioEntity;
import giis.demo.util.Database;

public class ResolverCostesModel {
    private Database db = new Database();

    public static class Material {
        public String nombre;
        public double coste;

        public Material(String nombre, double coste) {
            this.nombre = nombre;
            this.coste = coste;
        }
    }

    public List<IncidenciaDisplayDTO> getIncidenciasAsignadas(String dni) {
    
        String sql = "SELECT i.id, i.descripcion, " +
                     "t.nombre AS tipo, z.descripcion AS localizacion, u.nombre AS usuario, tu.nombre AS tecnico, " +
                     "i.Coste AS coste, i.descr_reparación AS descrReparacion, i.fecha, e.nombre AS estado, " +
                     "CASE WHEN i.validación = 1 THEN 'Sí' ELSE 'No' END AS validacion " +
                     "FROM Incidencia i " +
                     "JOIN Estados e ON i.estado = e.id " +
                     "JOIN Tipos t ON i.tipo = t.id " +
                     "JOIN Zonas z ON i.localizacion = z.id " +
                     "JOIN Usuarios u ON i.usuario = u.id " +
                     "LEFT JOIN Usuarios tu ON i.tecnico = tu.id " +
                     "WHERE i.tecnico = (SELECT id FROM Usuarios WHERE dni = ?) " +
                     "AND i.estado = 4";
        return db.executeQueryPojo(IncidenciaDisplayDTO.class, sql, dni);
    }

    public void resolverIncidencia(int idIncidencia, String tecnicoIdentificacion, int horas, double costePorHora, List<Material> materiales) {
        // obtener tecnico id
        IncidenciasModel incModel = new IncidenciasModel();
        UsuarioEntity tecnico = incModel.findUsuario(tecnicoIdentificacion);
        int tecnicoId = tecnico.getId();

        // calcular total
        double totalMateriales = 0.0;
        if (materiales != null) {
            for (Material m : materiales) totalMateriales += m.coste;
        }
        double totalHoras = horas * costePorHora;
        double total = totalHoras + totalMateriales;

        // preparar descripcion de la reparacion
        StringBuilder descr = new StringBuilder();
        descr.append("Horas: ").append(horas).append(" (coste hora: ").append(costePorHora).append(")");
        if (materiales != null && !materiales.isEmpty()) {
            descr.append("; Materiales: ");
            boolean first = true;
            for (Material m : materiales) {
                if (!first) descr.append(", ");
                descr.append(m.nombre).append("(").append(m.coste).append(")");
                first = false;
            }
        }

        // actualizar incidencia: coste y descr_reparación y estado=5 (Resuelta)
        String update = "UPDATE Incidencia SET Coste=?, descr_reparación=?, estado=? WHERE id=?";
        List<Object> params = new ArrayList<>();
        params.add(String.valueOf(total));
        params.add(descr.toString());
        params.add(Integer.valueOf(5));
        params.add(Integer.valueOf(idIncidencia));
        db.executeUpdate(update, params.toArray());

        // insertar en historial
        String insert = "INSERT INTO HistorialIncidencia(incidencia,fecha,accion,usuario,comentario,estado) VALUES (?,?,?,?,?,?)";
        List<Object> ip = new ArrayList<>();
        ip.add(Integer.valueOf(idIncidencia));
        ip.add(LocalDateTime.now().toString());
        ip.add("Resuelta");
        ip.add(Integer.valueOf(tecnicoId));
        ip.add("Coste total: " + total + "; " + descr.toString());
        ip.add(Integer.valueOf(5));
        db.executeUpdate(insert, ip.toArray());
    }
}
