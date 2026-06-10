package giis.demo.tkrun.paqueteria.rutas;

public class ResultadoAsignacion {
    private final boolean exito;
    private final int idRuta;
    private final int numTramos;
    private final String motivoFallo;

    public ResultadoAsignacion(boolean exito, int idRuta, int numTramos, String motivoFallo) {
        this.exito       = exito;
        this.idRuta      = idRuta;
        this.numTramos   = numTramos;
        this.motivoFallo = motivoFallo;
    }

    public boolean isExito()       { return exito; }
    public int getIdRuta()         { return idRuta; }
    public int getNumTramos()      { return numTramos; }
    public String getMotivoFallo() { return motivoFallo; }
}
