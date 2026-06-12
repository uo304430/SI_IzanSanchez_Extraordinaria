package giis.demo.tkrun.paqueteria.entregas;

public class EntregaListadoDto {
    private int    idTramo;
    private int    idEnvio;
    private String codigoEnvio;
    private String destinatario;
    private String telefono;
    private String direccion;
    private String fechaPrevista;
    private int    intentoActual;
    private String descripcionPaquete;

    public int    getIdTramo()              { return idTramo; }
    public void   setIdTramo(int v)         { this.idTramo = v; }
    public int    getIdEnvio()              { return idEnvio; }
    public void   setIdEnvio(int v)         { this.idEnvio = v; }
    public String getCodigoEnvio()          { return codigoEnvio; }
    public void   setCodigoEnvio(String v)  { this.codigoEnvio = v; }
    public String getDestinatario()         { return destinatario; }
    public void   setDestinatario(String v) { this.destinatario = v; }
    public String getTelefono()             { return telefono; }
    public void   setTelefono(String v)     { this.telefono = v; }
    public String getDireccion()            { return direccion; }
    public void   setDireccion(String v)    { this.direccion = v; }
    public String getFechaPrevista()        { return fechaPrevista; }
    public void   setFechaPrevista(String v){ this.fechaPrevista = v; }
    public int    getIntentoActual()        { return intentoActual; }
    public void   setIntentoActual(int v)   { this.intentoActual = v; }
    public String getDescripcionPaquete()           { return descripcionPaquete; }
    public void   setDescripcionPaquete(String v)   { this.descripcionPaquete = v; }
}
