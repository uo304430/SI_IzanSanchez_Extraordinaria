package giis.demo.tkrun.paqueteria.login;

/** DTO para poblar el combo de usuarios (empleados y operarios) en el login. */
public class EmpleadoComboDto {
    private int idUsuario;
    private String nombre;
    private String codigoPunto;
    private int idPuntoLogistico;
    private String rol; // "EMPLEADO" o "OPERARIO"

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int v) { this.idUsuario = v; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }
    public String getCodigoPunto() { return codigoPunto; }
    public void setCodigoPunto(String v) { this.codigoPunto = v; }
    public int getIdPuntoLogistico() { return idPuntoLogistico; }
    public void setIdPuntoLogistico(int v) { this.idPuntoLogistico = v; }
    public String getRol() { return rol; }
    public void setRol(String v) { this.rol = v; }

    @Override
    public String toString() {
        String tipo = "OPERARIO".equals(rol) ? "operario" : "empleado";
        return nombre + " — " + codigoPunto + " (" + tipo + ")";
    }
}
