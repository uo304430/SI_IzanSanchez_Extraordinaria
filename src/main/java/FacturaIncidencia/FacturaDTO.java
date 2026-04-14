package FacturaIncidencia;

import java.util.*;

public class FacturaDTO {
    private Integer id;
    private String numero; // nº único de factura
    private String emisor; // nombre del emisor (Gestor Económico)
    private String fecha;  // fecha ISO
    private String costeTotal;
    private String descripcionTecnica;
    private Integer incidenciaId;
    private List<ConceptoDTO> conceptos = new ArrayList<>();

    public FacturaDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getEmisor() { return emisor; }
    public void setEmisor(String emisor) { this.emisor = emisor; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getCosteTotal() { return costeTotal; }
    public void setCosteTotal(String costeTotal) { this.costeTotal = costeTotal; }

    public String getDescripcionTecnica() { return descripcionTecnica; }
    public void setDescripcionTecnica(String descripcionTecnica) { this.descripcionTecnica = descripcionTecnica; }

    public Integer getIncidenciaId() { return incidenciaId; }
    public void setIncidenciaId(Integer incidenciaId) { this.incidenciaId = incidenciaId; }

    public List<ConceptoDTO> getConceptos() { return conceptos; }
    public void setConceptos(List<ConceptoDTO> conceptos) { this.conceptos = conceptos; }
    public void addConcepto(ConceptoDTO c) { if (this.conceptos==null) this.conceptos = new ArrayList<>(); this.conceptos.add(c); }
}