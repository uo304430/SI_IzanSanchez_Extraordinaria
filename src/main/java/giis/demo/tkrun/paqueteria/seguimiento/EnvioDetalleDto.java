package giis.demo.tkrun.paqueteria.seguimiento;

import lombok.Data;

/** Datos generales del envio para la vista de detalle. */
@Data
public class EnvioDetalleDto {
    private int    id;
    private String codigo;
    private String estado;
    private String fechaRegistro;
    private String tipoServicio;
    private String modalidadEntrega;
    private String coste;              // formateado "X,XX EUR"
    private String valorDeclarado;     // formateado
    private String destinatarioNombre;
    private String destinatarioTelefono;
    private String direccionDestinatario;
    private String fechaEstimada;
    private String fechaEntregaReal;   // "-" si aun no entregado
    private int    modificacionesEntrega;
}
