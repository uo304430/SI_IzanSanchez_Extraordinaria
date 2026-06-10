package giis.demo.tkrun.paqueteria.util;

import java.math.BigDecimal;

public class TarifaDto {
    private int id;
    private BigDecimal precio;
    private String fechaInicioVigencia;
    private String fechaFinVigencia;

    public int getId() { return id; }
    public void setId(int v) { this.id = v; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal v) { this.precio = v; }
    public String getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(String v) { this.fechaInicioVigencia = v; }
    public String getFechaFinVigencia() { return fechaFinVigencia; }
    public void setFechaFinVigencia(String v) { this.fechaFinVigencia = v; }
}
