package giis.demo.tkrun.DTOs;

public class IncidenciaTecnicoDTO {
    private Integer id;
    private Integer incidencia;
    private Integer tecnico;

    public IncidenciaTecnicoDTO() {}

    public IncidenciaTecnicoDTO(Integer id, Integer incidencia, Integer tecnico) {
        this.id = id;
        this.incidencia = incidencia;
        this.tecnico = tecnico;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIncidencia() { return incidencia; }
    public void setIncidencia(Integer incidencia) { this.incidencia = incidencia; }

    public Integer getTecnico() { return tecnico; }
    public void setTecnico(Integer tecnico) { this.tecnico = tecnico; }

}
