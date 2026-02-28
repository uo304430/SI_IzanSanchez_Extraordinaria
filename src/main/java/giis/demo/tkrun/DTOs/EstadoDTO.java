package giis.demo.tkrun.DTOs;

/** DTO que representa una fila de la tabla Estados (id, nombre) */
public class EstadoDTO {
    private final int id;
    private final String nombre;

    public EstadoDTO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
