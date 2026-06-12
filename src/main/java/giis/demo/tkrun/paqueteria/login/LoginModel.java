package giis.demo.tkrun.paqueteria.login;

import giis.demo.util.Database;

import java.util.List;

public class LoginModel {

    private final Database db;

    public LoginModel() {
        this.db = new Database();
    }

    /** Devuelve empleados, operarios, clientes y transportistas activos para poblar el combo del login. */
    public List<EmpleadoComboDto> getEmpleadosActivos() {
        String sql =
                "SELECT u.id AS idUsuario, u.nombre, p.codigo AS codigoPunto, e.idPuntoLogistico, 'EMPLEADO' AS rol, '' AS dni, 0 AS idVehiculoHabitual "
                + "FROM Empleado e "
                + "JOIN Usuario u ON e.idUsuario = u.id "
                + "JOIN PuntoLogistico p ON e.idPuntoLogistico = p.id "
                + "WHERE u.activo = 1 "
                + "UNION ALL "
                + "SELECT u.id AS idUsuario, u.nombre, p.codigo AS codigoPunto, o.idPuntoLogistico, 'OPERARIO' AS rol, '' AS dni, 0 AS idVehiculoHabitual "
                + "FROM Operario o "
                + "JOIN Usuario u ON o.idUsuario = u.id "
                + "JOIN PuntoLogistico p ON o.idPuntoLogistico = p.id "
                + "WHERE u.activo = 1 "
                + "UNION ALL "
                + "SELECT u.id AS idUsuario, u.nombre, '' AS codigoPunto, 0 AS idPuntoLogistico, 'CLIENTE' AS rol, u.dni AS dni, 0 AS idVehiculoHabitual "
                + "FROM Cliente c "
                + "JOIN Usuario u ON c.idUsuario = u.id "
                + "WHERE u.activo = 1 "
                + "UNION ALL "
                + "SELECT u.id AS idUsuario, u.nombre, COALESCE(v.matricula,'') AS codigoPunto, 0 AS idPuntoLogistico, 'TRANSPORTISTA' AS rol, '' AS dni, COALESCE(t.idVehiculoHabitual, 0) AS idVehiculoHabitual "
                + "FROM Transportista t "
                + "JOIN Usuario u ON t.idUsuario = u.id "
                + "LEFT JOIN Vehiculo v ON t.idVehiculoHabitual = v.id "
                + "WHERE u.activo = 1 "
                + "ORDER BY nombre";
        return db.executeQueryPojo(EmpleadoComboDto.class, sql);
    }
}
