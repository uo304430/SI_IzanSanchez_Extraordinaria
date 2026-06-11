package giis.demo.tkrun.paqueteria.seguimiento;

/** Fila de la seccion de incidencias en el detalle del envio. */
public class IncidenciaListadoDto {
    private String tipo;
    private String fechaApertura;
    private String descripcion;
    private String estado;

    public String getTipo()                  { return tipo; }
    public void   setTipo(String v)          { this.tipo = v; }
    public String getFechaApertura()         { return fechaApertura; }
    public void   setFechaApertura(String v) { this.fechaApertura = v; }
    public String getDescripcion()           { return descripcion; }
    public void   setDescripcion(String v)   { this.descripcion = v; }
    public String getEstado()                { return estado; }
    public void   setEstado(String v)        { this.estado = v; }
}
