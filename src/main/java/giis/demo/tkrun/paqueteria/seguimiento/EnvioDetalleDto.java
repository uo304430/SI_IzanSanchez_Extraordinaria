package giis.demo.tkrun.paqueteria.seguimiento;

/** Datos generales del envio para la vista de detalle. */
public class EnvioDetalleDto {
    private int    id;
    private String codigo;
    private String estado;
    private String fechaRegistro;
    private String tipoServicio;
    private String modalidadEntrega;
    private String coste;
    private String valorDeclarado;
    private String destinatarioNombre;
    private String destinatarioTelefono;
    private String direccionDestinatario;
    private String fechaEstimada;
    private String fechaEntregaReal;
    private int    modificacionesEntrega;

    public int    getId()                           { return id; }
    public void   setId(int v)                      { this.id = v; }
    public String getCodigo()                       { return codigo; }
    public void   setCodigo(String v)               { this.codigo = v; }
    public String getEstado()                       { return estado; }
    public void   setEstado(String v)               { this.estado = v; }
    public String getFechaRegistro()                { return fechaRegistro; }
    public void   setFechaRegistro(String v)        { this.fechaRegistro = v; }
    public String getTipoServicio()                 { return tipoServicio; }
    public void   setTipoServicio(String v)         { this.tipoServicio = v; }
    public String getModalidadEntrega()             { return modalidadEntrega; }
    public void   setModalidadEntrega(String v)     { this.modalidadEntrega = v; }
    public String getCoste()                        { return coste; }
    public void   setCoste(String v)                { this.coste = v; }
    public String getValorDeclarado()               { return valorDeclarado; }
    public void   setValorDeclarado(String v)       { this.valorDeclarado = v; }
    public String getDestinatarioNombre()           { return destinatarioNombre; }
    public void   setDestinatarioNombre(String v)   { this.destinatarioNombre = v; }
    public String getDestinatarioTelefono()         { return destinatarioTelefono; }
    public void   setDestinatarioTelefono(String v) { this.destinatarioTelefono = v; }
    public String getDireccionDestinatario()        { return direccionDestinatario; }
    public void   setDireccionDestinatario(String v){ this.direccionDestinatario = v; }
    public String getFechaEstimada()                { return fechaEstimada; }
    public void   setFechaEstimada(String v)        { this.fechaEstimada = v; }
    public String getFechaEntregaReal()             { return fechaEntregaReal; }
    public void   setFechaEntregaReal(String v)     { this.fechaEntregaReal = v; }
    public int    getModificacionesEntrega()        { return modificacionesEntrega; }
    public void   setModificacionesEntrega(int v)   { this.modificacionesEntrega = v; }
}
