package giis.demo.tkrun.paqueteria.login;

/** DTO para poblar el combo de empleados en el login. */
public class EmpleadoComboDto {
    private int idUsuario;
    private String nombre;
    private String codigoPunto;
    private int idPuntoLogistico;

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int v) { this.idUsuario = v; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }
    public String getCodigoPunto() { return codigoPunto; }
    public void setCodigoPunto(String v) { this.codigoPunto = v; }
    public int getIdPuntoLogistico() { return idPuntoLogistico; }
    public void setIdPuntoLogistico(int v) { this.idPuntoLogistico = v; }

    @Override
    public String toString() {
        return nombre + " — " + codigoPunto;
    }
}
