package giis.demo.tkrun.paqueteria.seguimiento;

/** Fila de la tabla de listado de envios del cliente. */
public class EnvioListadoDto {
    private int    id;
    private String codigo;
    private String fechaRegistro;
    private String destinatario;
    private String estado;
    private String fechaEstimada;

    public int    getId()                    { return id; }
    public void   setId(int v)               { this.id = v; }
    public String getCodigo()                { return codigo; }
    public void   setCodigo(String v)        { this.codigo = v; }
    public String getFechaRegistro()         { return fechaRegistro; }
    public void   setFechaRegistro(String v) { this.fechaRegistro = v; }
    public String getDestinatario()          { return destinatario; }
    public void   setDestinatario(String v)  { this.destinatario = v; }
    public String getEstado()                { return estado; }
    public void   setEstado(String v)        { this.estado = v; }
    public String getFechaEstimada()         { return fechaEstimada; }
    public void   setFechaEstimada(String v) { this.fechaEstimada = v; }
}
