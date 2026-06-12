package giis.demo.tkrun.paqueteria.entregas;

public class ResultadoEntregaDto {
    private boolean exito;
    private int     numeroIntento;
    private boolean esCuartoFallo;
    private String  codigoPuntoDestino;

    public boolean isExito()                    { return exito; }
    public void    setExito(boolean v)          { this.exito = v; }
    public int     getNumeroIntento()           { return numeroIntento; }
    public void    setNumeroIntento(int v)      { this.numeroIntento = v; }
    public boolean isEsCuartoFallo()            { return esCuartoFallo; }
    public void    setEsCuartoFallo(boolean v)  { this.esCuartoFallo = v; }
    public String  getCodigoPuntoDestino()          { return codigoPuntoDestino; }
    public void    setCodigoPuntoDestino(String v)  { this.codigoPuntoDestino = v; }
}
