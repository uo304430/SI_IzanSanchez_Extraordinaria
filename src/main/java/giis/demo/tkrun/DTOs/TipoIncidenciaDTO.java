package giis.demo.tkrun.DTOs;

/** DTO que representa una fila de la tabla Tipos (id, nombre) */
public class TipoIncidenciaDTO {
    private final int id;
    private final String nombre;

    public TipoIncidenciaDTO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
