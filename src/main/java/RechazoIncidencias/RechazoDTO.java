package RechazoIncidencias;

public class RechazoDTO {
    private String id;
    private String motivo;

    public RechazoDTO(String id, String motivo) {
        this.id = id;
        this.motivo = motivo;
    }

    public String getId() { return id; }
    public String getMotivo() { return motivo; }
}