package giis.demo.tkrun.paqueteria.almacen;

/** Datos del formulario de carga/descarga enviados al modelo. */
public class OperacionAlmacenDto {

    private int idPaquete;
    private int idEnvio;
    private int idTramo;
    private String tipoOperacion;   // "ENTRADA" o "SALIDA"
    private String inspeccionVisual; // "CORRECTO", "DANO_LEVE", "DANO_GRAVE"
    private double pesoMedidoKg;
    private double pesoRefKg;
    private int idAlmacen;
    private int idOperario;

    public int getIdPaquete()          { return idPaquete; }
    public void setIdPaquete(int v)    { this.idPaquete = v; }

    public int getIdEnvio()            { return idEnvio; }
    public void setIdEnvio(int v)      { this.idEnvio = v; }

    public int getIdTramo()            { return idTramo; }
    public void setIdTramo(int v)      { this.idTramo = v; }

    public String getTipoOperacion()         { return tipoOperacion; }
    public void setTipoOperacion(String v)   { this.tipoOperacion = v; }

    public String getInspeccionVisual()        { return inspeccionVisual; }
    public void setInspeccionVisual(String v)  { this.inspeccionVisual = v; }

    public double getPesoMedidoKg()        { return pesoMedidoKg; }
    public void setPesoMedidoKg(double v)  { this.pesoMedidoKg = v; }

    public double getPesoRefKg()           { return pesoRefKg; }
    public void setPesoRefKg(double v)     { this.pesoRefKg = v; }

    public int getIdAlmacen()          { return idAlmacen; }
    public void setIdAlmacen(int v)    { this.idAlmacen = v; }

    public int getIdOperario()         { return idOperario; }
    public void setIdOperario(int v)   { this.idOperario = v; }
}
