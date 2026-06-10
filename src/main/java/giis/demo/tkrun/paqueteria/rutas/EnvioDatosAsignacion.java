package giis.demo.tkrun.paqueteria.rutas;

/** DTO de lectura para los datos del Envio necesarios en la asignacion de ruta. */
public class EnvioDatosAsignacion {
    private int id;
    private int idPuntoOrigen;
    private int idPuntoDestino;
    private int idZonaOrigen;
    private int idZonaDestino;
    private String modalidadRecogida;
    private String modalidadEntrega;
    private int idTipoServicio;
    private String remitenteDireccion;
    private String remitenteCiudad;
    private String remitenteCodigoPostal;
    private String destinatarioDireccion;
    private String destinatarioCiudad;
    private String destinatarioCodigoPostal;

    public int getId()                               { return id; }
    public void setId(int v)                         { this.id = v; }
    public int getIdPuntoOrigen()                    { return idPuntoOrigen; }
    public void setIdPuntoOrigen(int v)              { this.idPuntoOrigen = v; }
    public int getIdPuntoDestino()                   { return idPuntoDestino; }
    public void setIdPuntoDestino(int v)             { this.idPuntoDestino = v; }
    public int getIdZonaOrigen()                     { return idZonaOrigen; }
    public void setIdZonaOrigen(int v)               { this.idZonaOrigen = v; }
    public int getIdZonaDestino()                    { return idZonaDestino; }
    public void setIdZonaDestino(int v)              { this.idZonaDestino = v; }
    public String getModalidadRecogida()             { return modalidadRecogida; }
    public void setModalidadRecogida(String v)       { this.modalidadRecogida = v; }
    public String getModalidadEntrega()              { return modalidadEntrega; }
    public void setModalidadEntrega(String v)        { this.modalidadEntrega = v; }
    public int getIdTipoServicio()                   { return idTipoServicio; }
    public void setIdTipoServicio(int v)             { this.idTipoServicio = v; }
    public String getRemitenteDireccion()            { return remitenteDireccion; }
    public void setRemitenteDireccion(String v)      { this.remitenteDireccion = v; }
    public String getRemitenteCiudad()               { return remitenteCiudad; }
    public void setRemitenteCiudad(String v)         { this.remitenteCiudad = v; }
    public String getRemitenteCodigoPostal()         { return remitenteCodigoPostal; }
    public void setRemitenteCodigoPostal(String v)   { this.remitenteCodigoPostal = v; }
    public String getDestinatarioDireccion()         { return destinatarioDireccion; }
    public void setDestinatarioDireccion(String v)   { this.destinatarioDireccion = v; }
    public String getDestinatarioCiudad()            { return destinatarioCiudad; }
    public void setDestinatarioCiudad(String v)      { this.destinatarioCiudad = v; }
    public String getDestinatarioCodigoPostal()      { return destinatarioCodigoPostal; }
    public void setDestinatarioCodigoPostal(String v){ this.destinatarioCodigoPostal = v; }
}
