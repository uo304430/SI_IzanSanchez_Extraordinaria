package giis.demo.tkrun.paqueteria.seguimiento;

import lombok.Data;

/** Fila del historial de eventos en el detalle del envio. */
@Data
public class EventoHistorialDto {
    private String fechaEvento;   // "dd/MM/yyyy HH:mm:ss"
    private String accion;
    private String responsable;   // "Sistema" o "ROL: Nombre"
    private String punto;
    private String comentario;
}
