package giis.demo.tkrun.paqueteria.login;

/** Singleton que mantiene la identidad del empleado que ha iniciado sesion. */
public class SesionUsuario {

    private static SesionUsuario instancia;

    private int idUsuario;
    private String nombre;
    private int idPuntoLogistico;
    private String codigoPunto;

    private SesionUsuario() {}

    public static SesionUsuario getInstance() {
        if (instancia == null)
            instancia = new SesionUsuario();
        return instancia;
    }

    public void iniciar(int idUsuario, String nombre, int idPuntoLogistico, String codigoPunto) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.idPuntoLogistico = idPuntoLogistico;
        this.codigoPunto = codigoPunto;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public int getIdPuntoLogistico() { return idPuntoLogistico; }
    public String getCodigoPunto() { return codigoPunto; }
}
