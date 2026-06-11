package giis.demo.tkrun.paqueteria.seguimiento;

/** Fila de la tabla de ruta en el detalle del envio. */
public class TramoListadoDto {
    private int    orden;
    private String tipo;
    private String origen;
    private String destino;
    private String vehiculo;
    private String fechaPrevista;
    private String fechaReal;
    private String estado;

    public int    getOrden()                  { return orden; }
    public void   setOrden(int v)             { this.orden = v; }
    public String getTipo()                   { return tipo; }
    public void   setTipo(String v)           { this.tipo = v; }
    public String getOrigen()                 { return origen; }
    public void   setOrigen(String v)         { this.origen = v; }
    public String getDestino()                { return destino; }
    public void   setDestino(String v)        { this.destino = v; }
    public String getVehiculo()               { return vehiculo; }
    public void   setVehiculo(String v)       { this.vehiculo = v; }
    public String getFechaPrevista()          { return fechaPrevista; }
    public void   setFechaPrevista(String v)  { this.fechaPrevista = v; }
    public String getFechaReal()              { return fechaReal; }
    public void   setFechaReal(String v)      { this.fechaReal = v; }
    public String getEstado()                 { return estado; }
    public void   setEstado(String v)         { this.estado = v; }
}
