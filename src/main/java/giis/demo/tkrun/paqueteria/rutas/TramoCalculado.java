package giis.demo.tkrun.paqueteria.rutas;

import java.time.LocalDateTime;

/** Tramo de ruta calculado internamente antes de persistir. */
public class TramoCalculado {
    private int ordenSecuencia;
    private String tipo;             // RECOGIDA, INTERMEDIO, ENTREGA
    private Integer idPuntoOrigen;   // null para RECOGIDA domicilio
    private Integer idPuntoDestino;  // null para ENTREGA domicilio
    private String direccionOrigen;  // rellena cuando idPuntoOrigen es null
    private String direccionDestino; // rellena cuando idPuntoDestino es null
    private Integer idVehiculo;
    private LocalDateTime fechaPrevista;

    public int getOrdenSecuencia()            { return ordenSecuencia; }
    public void setOrdenSecuencia(int v)      { this.ordenSecuencia = v; }
    public String getTipo()                   { return tipo; }
    public void setTipo(String v)             { this.tipo = v; }
    public Integer getIdPuntoOrigen()         { return idPuntoOrigen; }
    public void setIdPuntoOrigen(Integer v)   { this.idPuntoOrigen = v; }
    public Integer getIdPuntoDestino()        { return idPuntoDestino; }
    public void setIdPuntoDestino(Integer v)  { this.idPuntoDestino = v; }
    public String getDireccionOrigen()        { return direccionOrigen; }
    public void setDireccionOrigen(String v)  { this.direccionOrigen = v; }
    public String getDireccionDestino()       { return direccionDestino; }
    public void setDireccionDestino(String v) { this.direccionDestino = v; }
    public Integer getIdVehiculo()            { return idVehiculo; }
    public void setIdVehiculo(Integer v)      { this.idVehiculo = v; }
    public LocalDateTime getFechaPrevista()          { return fechaPrevista; }
    public void setFechaPrevista(LocalDateTime v)    { this.fechaPrevista = v; }
}
