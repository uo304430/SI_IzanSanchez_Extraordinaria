package giis.demo.tkrun.DTOs;

import java.time.LocalDateTime;
import giis.demo.tkrun.CiudadanoConsulataIncidencias.ConsultaModel;

/** Modelo de incidencia convertido a DTO */
public class IncidenciaDTO {
    private Integer id;
    private Integer tipo;
    private String descripcion;
    private Integer localizacion;
    private UsuarioDTO ciudadano;
    private UsuarioDTO tecnico;
    private LocalDateTime fechaHoraRegistro;
    private Integer estado;
    private String coste;
    private String descripcionReparacion;
    private boolean validacion;

    public IncidenciaDTO(Integer id, Integer tipo, String descripcion, Integer localizacion, UsuarioDTO ciudadano,
            LocalDateTime fechaHoraRegistro, Integer estado) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.ciudadano = ciudadano;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public Integer getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Integer getLocalizacion() { return localizacion; }
    public UsuarioDTO getCiudadano() { return ciudadano; }
    public UsuarioDTO getTecnico() { return tecnico; }
    public LocalDateTime getFechaHoraRegistro() { return fechaHoraRegistro; }
    public Integer getEstado() { return estado; }
    public String getCoste() { return coste; }
    public String getDescripcionReparacion() { return descripcionReparacion; }
    public boolean isValidacion() { return validacion; }

    public void setTecnico(UsuarioDTO tecnico) { this.tecnico = tecnico; }
    public void setEstado(Integer estado) { this.estado = estado; }
    public void setCoste(String coste) { this.coste = coste; }
    public void setDescripcionReparacion(String descripcionReparacion) { this.descripcionReparacion = descripcionReparacion; }
    public void setValidacion(boolean validacion) { this.validacion = validacion; }

    // Derived getter to expose the human-readable estado name for UI/table use
    public String getEstadoNombre() {
        return ConsultaModel.nombreDeEstado(this.estado);
    }

    // Derived getter to expose the human-readable tipo name for UI/table use
    public String getTipoNombre() {
        return ConsultaModel.nombreDeTipo(this.tipo);
    }
}
