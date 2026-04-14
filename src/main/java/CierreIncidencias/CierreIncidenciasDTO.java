package CierreIncidencias;

public class CierreIncidenciasDTO {
    private Integer id;
    private Integer tipo;
    private String descripcion;
    private String fecha;
    private String coste;
    private String tecnico;
    private Integer estado;

    public CierreIncidenciasDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTipo() { return tipo; }
    public void setTipo(Integer tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getCoste() { return coste; }
    public void setCoste(String coste) { this.coste = coste; }

    public String getTecnico() { return tecnico; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}