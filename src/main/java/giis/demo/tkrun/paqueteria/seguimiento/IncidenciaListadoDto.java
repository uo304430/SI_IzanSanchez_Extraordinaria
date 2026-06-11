package giis.demo.tkrun.paqueteria.seguimiento;

import lombok.Data;

/** Fila de la seccion de incidencias en el detalle del envio. */
@Data
public class IncidenciaListadoDto {
    private String tipo;
    private String fechaApertura;  // "dd/MM/yyyy HH:mm"
    private String descripcion;
    private String estado;
}
