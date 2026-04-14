package Izan_33804;

public class IncidenciaDisplayDTO {
    private String id;
    private String descripcion;
    private String estado;
    private String tipo;
    private String localizacion;
    private String usuario;
    private String tecnico;
    private String coste;
    private String descrReparacion;
    private String fecha;
    private String validacion;

    public IncidenciaDisplayDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getTecnico() { return tecnico; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }

    public String getCoste() { return coste; }
    public void setCoste(String coste) { this.coste = coste; }

    public String getDescrReparacion() { return descrReparacion; }
    public void setDescrReparacion(String descrReparacion) { this.descrReparacion = descrReparacion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getValidacion() { return validacion; }
    public void setValidacion(String validacion) { this.validacion = validacion; }
}