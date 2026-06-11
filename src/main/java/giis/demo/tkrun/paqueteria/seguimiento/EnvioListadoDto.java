package giis.demo.tkrun.paqueteria.seguimiento;

import lombok.Data;

/** Fila del listado de envios del cliente. */
@Data
public class EnvioListadoDto {
    private int    id;
    private String codigo;
    private String fechaRegistro;
    private String destinatario;
    private String estado;
    private String fechaEstimada;
}
