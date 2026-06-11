package giis.demo.tkrun.paqueteria.almacen;

/** Resultado de registrar una operacion de carga/descarga. */
public class ResultadoOperacionDto {

    private boolean exito;
    private boolean discrepancia;
    private boolean incidenciaGenerada;
    private String tipoIncidencia; // null si no se genero incidencia

    public boolean isExito()              { return exito; }
    public void setExito(boolean v)       { this.exito = v; }

    public boolean isDiscrepancia()          { return discrepancia; }
    public void setDiscrepancia(boolean v)   { this.discrepancia = v; }

    public boolean isIncidenciaGenerada()        { return incidenciaGenerada; }
    public void setIncidenciaGenerada(boolean v) { this.incidenciaGenerada = v; }

    public String getTipoIncidencia()         { return tipoIncidencia; }
    public void setTipoIncidencia(String v)   { this.tipoIncidencia = v; }
}
