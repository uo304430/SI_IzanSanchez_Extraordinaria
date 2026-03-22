package giis.demo.tkrun.DTOs;

public class HistorialDTO {
    private Integer id;
    private Integer incidencia;
    private String fecha;
    private String accion;
    private Integer usuario;
    private String comentario;
    private Integer estado;

    public HistorialDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIncidencia() { return incidencia; }
    public void setIncidencia(Integer incidencia) { this.incidencia = incidencia; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public Integer getUsuario() { return usuario; }
    public void setUsuario(Integer usuario) { this.usuario = usuario; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
