package giis.demo.tkrun.paqueteria.seguimiento;

/** Fila del historial de eventos en el detalle del envio. */
public class EventoHistorialDto {
    private String fechaEvento;
    private String accion;
    private String responsable;
    private String punto;
    private String comentario;

    public String getFechaEvento()           { return fechaEvento; }
    public void   setFechaEvento(String v)   { this.fechaEvento = v; }
    public String getAccion()                { return accion; }
    public void   setAccion(String v)        { this.accion = v; }
    public String getResponsable()           { return responsable; }
    public void   setResponsable(String v)   { this.responsable = v; }
    public String getPunto()                 { return punto; }
    public void   setPunto(String v)         { this.punto = v; }
    public String getComentario()            { return comentario; }
    public void   setComentario(String v)    { this.comentario = v; }
}
