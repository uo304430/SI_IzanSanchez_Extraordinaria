package giis.demo.tkrun.paqueteria.seguimiento;

import lombok.Data;

/** Fila de la tabla de ruta en el detalle del envio. */
@Data
public class TramoListadoDto {
    private int    orden;
    private String tipo;
    private String origen;
    private String destino;
    private String vehiculo;
    private String fechaPrevista;  // formateada "dd/MM/yyyy HH:mm"
    private String fechaReal;      // formateada o "-"
    private String estado;
}
