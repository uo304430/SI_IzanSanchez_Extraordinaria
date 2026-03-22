package giis.demo.tkrun.Entities;

public class IncidenciaEntity {
    private Integer id;
    private Integer tipo;
    private String descripcion;
    private Integer localizacion;
    private Integer usuario;
    private Integer tecnico;
    private String coste;
    private String descrReparacion;
    private String fecha;
    private Integer estado;
    private Boolean validacion;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTipo() { return tipo; }
    public void setTipo(Integer tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getLocalizacion() { return localizacion; }
    public void setLocalizacion(Integer localizacion) { this.localizacion = localizacion; }
    public Integer getUsuario() { return usuario; }
    public void setUsuario(Integer usuario) { this.usuario = usuario; }
    public Integer getTecnico() { return tecnico; }
    public void setTecnico(Integer tecnico) { this.tecnico = tecnico; }
    public String getCoste() { return coste; }
    public void setCoste(String coste) { this.coste = coste; }
    public String getDescrReparacion() { return descrReparacion; }
    public void setDescrReparacion(String descrReparacion) { this.descrReparacion = descrReparacion; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
    public Boolean getValidacion() { return validacion; }
    public void setValidacion(Boolean validacion) { this.validacion = validacion; }
}