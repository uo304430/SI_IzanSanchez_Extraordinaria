package giis.demo.tkrun.paqueteria.seguimiento;

/** Datos recogidos del formulario de modificacion de entrega. */
public class ModificacionEntregaDto {

    /** 'A' = cambio de direccion, 'B' = recogida en punto destino. */
    private char opcion;

    // Opcion A
    private String nuevoNombreDestinatario;
    private String nuevoTelefonoDestinatario;
    private String nuevaDireccion;
    private String nuevaCiudad;
    private String nuevoCodigoPostal;

    // Datos de contexto (cargados desde BD, no editados por el usuario)
    private int    idEnvio;
    private int    idTramoEntrega;
    private int    idPuntoDestinoActual;
    private String codigoPuntoDestinoActual;
    private String direccionAnterior;
    private String estadoEnvio;
    private String modalidadEntrega;
    private String fechaPrevistaUltimoTramo;

    public char   getOpcion()                          { return opcion; }
    public void   setOpcion(char v)                    { this.opcion = v; }

    public String getNuevoNombreDestinatario()         { return nuevoNombreDestinatario; }
    public void   setNuevoNombreDestinatario(String v) { this.nuevoNombreDestinatario = v; }

    public String getNuevoTelefonoDestinatario()         { return nuevoTelefonoDestinatario; }
    public void   setNuevoTelefonoDestinatario(String v) { this.nuevoTelefonoDestinatario = v; }

    public String getNuevaDireccion()                  { return nuevaDireccion; }
    public void   setNuevaDireccion(String v)          { this.nuevaDireccion = v; }

    public String getNuevaCiudad()                     { return nuevaCiudad; }
    public void   setNuevaCiudad(String v)             { this.nuevaCiudad = v; }

    public String getNuevoCodigoPostal()               { return nuevoCodigoPostal; }
    public void   setNuevoCodigoPostal(String v)       { this.nuevoCodigoPostal = v; }

    public int    getIdEnvio()                         { return idEnvio; }
    public void   setIdEnvio(int v)                    { this.idEnvio = v; }

    public int    getIdTramoEntrega()                  { return idTramoEntrega; }
    public void   setIdTramoEntrega(int v)             { this.idTramoEntrega = v; }

    public int    getIdPuntoDestinoActual()            { return idPuntoDestinoActual; }
    public void   setIdPuntoDestinoActual(int v)       { this.idPuntoDestinoActual = v; }

    public String getCodigoPuntoDestinoActual()        { return codigoPuntoDestinoActual; }
    public void   setCodigoPuntoDestinoActual(String v){ this.codigoPuntoDestinoActual = v; }

    public String getDireccionAnterior()               { return direccionAnterior; }
    public void   setDireccionAnterior(String v)       { this.direccionAnterior = v; }

    public String getEstadoEnvio()                     { return estadoEnvio; }
    public void   setEstadoEnvio(String v)             { this.estadoEnvio = v; }

    public String getModalidadEntrega()                { return modalidadEntrega; }
    public void   setModalidadEntrega(String v)        { this.modalidadEntrega = v; }

    public String getFechaPrevistaUltimoTramo()        { return fechaPrevistaUltimoTramo; }
    public void   setFechaPrevistaUltimoTramo(String v){ this.fechaPrevistaUltimoTramo = v; }
}
