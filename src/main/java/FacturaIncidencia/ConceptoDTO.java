package FacturaIncidencia;

public class ConceptoDTO {
    private Integer id;
    private Integer facturaId;
    private String descripcion;
    private String importe; // como string para mantener compatibilidad con otras tablas

    public ConceptoDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFacturaId() { return facturaId; }
    public void setFacturaId(Integer facturaId) { this.facturaId = facturaId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImporte() { return importe; }
    public void setImporte(String importe) { this.importe = importe; }
}
