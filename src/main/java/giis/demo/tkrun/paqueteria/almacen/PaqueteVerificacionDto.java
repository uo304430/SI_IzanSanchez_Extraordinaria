package giis.demo.tkrun.paqueteria.almacen;

/** Datos del paquete encontrado tras la busqueda por codigo de barras. */
public class PaqueteVerificacionDto {

    private int idPaquete;
    private int idEnvio;
    private String codigoEnvio;
    private int idTramo;
    private String destinatario;
    private String descripcion;
    private double pesoRefKg;       // ultimo peso verificado o declarado si nunca se verifico
    private double pesoDeclaradoKg;

    public int getIdPaquete()        { return idPaquete; }
    public void setIdPaquete(int v)  { this.idPaquete = v; }

    public int getIdEnvio()          { return idEnvio; }
    public void setIdEnvio(int v)    { this.idEnvio = v; }

    public String getCodigoEnvio()         { return codigoEnvio; }
    public void setCodigoEnvio(String v)   { this.codigoEnvio = v; }

    public int getIdTramo()          { return idTramo; }
    public void setIdTramo(int v)    { this.idTramo = v; }

    public String getDestinatario()        { return destinatario; }
    public void setDestinatario(String v)  { this.destinatario = v; }

    public String getDescripcion()         { return descripcion; }
    public void setDescripcion(String v)   { this.descripcion = v; }

    public double getPesoRefKg()       { return pesoRefKg; }
    public void setPesoRefKg(double v) { this.pesoRefKg = v; }

    public double getPesoDeclaradoKg()       { return pesoDeclaradoKg; }
    public void setPesoDeclaradoKg(double v) { this.pesoDeclaradoKg = v; }
}
