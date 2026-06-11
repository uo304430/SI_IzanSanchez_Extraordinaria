package giis.demo.tkrun.paqueteria.login;

import giis.demo.util.Database;

import java.util.List;

public class LoginModel {

    private final Database db;

    public LoginModel() {
        this.db = new Database();
    }

    /** Devuelve empleados y operarios activos con su punto logistico para poblar el combo del login. */
    public List<EmpleadoComboDto> getEmpleadosActivos() {
        String sql = "SELECT u.id AS idUsuario, u.nombre, p.codigo AS codigoPunto, e.idPuntoLogistico, 'EMPLEADO' AS rol "
                + "FROM Empleado e "
                + "JOIN Usuario u ON e.idUsuario = u.id "
                + "JOIN PuntoLogistico p ON e.idPuntoLogistico = p.id "
                + "WHERE u.activo = 1 "
                + "UNION ALL "
                + "SELECT u.id AS idUsuario, u.nombre, p.codigo AS codigoPunto, o.idPuntoLogistico, 'OPERARIO' AS rol "
                + "FROM Operario o "
                + "JOIN Usuario u ON o.idUsuario = u.id "
                + "JOIN PuntoLogistico p ON o.idPuntoLogistico = p.id "
                + "WHERE u.activo = 1 "
                + "ORDER BY nombre";
        return db.executeQueryPojo(EmpleadoComboDto.class, sql);
    }
}
