package giis.demo.tkrun.paqueteria.login;

/** DTO para poblar el combo de usuarios (empleados, operarios y clientes) en el login. */
public class EmpleadoComboDto {
    private int idUsuario;
    private String nombre;
    private String codigoPunto;
    private int idPuntoLogistico;
    private String rol; // "EMPLEADO", "OPERARIO" o "CLIENTE"
    private String dni; // usado para mostrar clientes

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
    public String getDni() { return dni; }
    public void setDni(String v) { this.dni = v; }

    @Override
    public String toString() {
        if ("CLIENTE".equals(rol)) {
            return nombre + " — DNI " + dni + " (cliente)";
        }
        String tipo = "OPERARIO".equals(rol) ? "operario" : "empleado";
        return nombre + " — " + codigoPunto + " (" + tipo + ")";
    }
}
