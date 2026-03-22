package EstadisticasIncidencias;

/**
 * Data Transfer Object para el informe de incidencias.
 * Los nombres de los atributos coinciden con los alias del SELECT en el Model
 * para que la librería Database realice el mapeo automático.
 */
public class IncidenciaReporteDTO {
    private String id;
    private String fecha;
    private String descripcion;
    private String estado;
    private String tipo;    
    private String zona;    

    public IncidenciaReporteDTO() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
}