package InformeEconomico;

/**
 * DTO para el informe económico. Nombres de atributos coinciden con los aliases
 * usados en las consultas del Model para permitir el mapeo automático.
 */
public class InformeEconomicoDTO {
    private String tipo;
    private String numeroIncidencias;
    private String costeMedio;
    private String costeTotal;

    public InformeEconomicoDTO() {}

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNumeroIncidencias() { return numeroIncidencias; }
    public void setNumeroIncidencias(String numeroIncidencias) { this.numeroIncidencias = numeroIncidencias; }

    public String getCosteMedio() { return costeMedio; }
    public void setCosteMedio(String costeMedio) { this.costeMedio = costeMedio; }

    public String getCosteTotal() { return costeTotal; }
    public void setCosteTotal(String costeTotal) { this.costeTotal = costeTotal; }
}