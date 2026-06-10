package giis.demo.tkrun.paqueteria.rutas;

import giis.demo.util.Database;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Acceso a datos para la asignacion de rutas. Todos los metodos usan la conexion pasada por parametro. */
public class RutaModel {

    private static final Logger log = LoggerFactory.getLogger(RutaModel.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @SuppressWarnings("unused")
    private final Database db;

    public RutaModel() {
        this.db = new Database();
    }

    public EnvioDatosAsignacion getEnvioDatos(int idEnvio, Connection conn) throws SQLException {
        String sql = "SELECT id, idPuntoOrigen, idPuntoDestino, idZonaOrigen, idZonaDestino, "
                + "modalidadRecogida, modalidadEntrega, idTipoServicio, "
                + "remitenteDireccion, remitenteCiudad, remitenteCodigoPostal, "
                + "destinatarioDireccion, destinatarioCiudad, destinatarioCodigoPostal "
                + "FROM Envio WHERE id = ?";
        QueryRunner runner = new QueryRunner();
        List<EnvioDatosAsignacion> list = runner.query(conn, sql,
                new BeanListHandler<>(EnvioDatosAsignacion.class), idEnvio);
        return list.isEmpty() ? null : list.get(0);
    }

    public PaqueteDatos getPaqueteDatos(int idEnvio, Connection conn) throws SQLException {
        String sql = "SELECT pesoDeclaradoKg, largoCm, anchoCm, altoCm FROM Paquete WHERE idEnvio = ?";
        QueryRunner runner = new QueryRunner();
        List<PaqueteDatos> list = runner.query(conn, sql,
                new BeanListHandler<>(PaqueteDatos.class), idEnvio);
        return list.isEmpty() ? null : list.get(0);
    }

    public String getCodigoTipoServicio(int idTipoServicio, Connection conn) throws SQLException {
        QueryRunner runner = new QueryRunner();
        List<Object[]> r = runner.query(conn, "SELECT codigo FROM TipoServicio WHERE id = ?",
                new ArrayListHandler(), idTipoServicio);
        return r.isEmpty() ? "ESTANDAR" : r.get(0)[0].toString();
    }

    /**
     * Devuelve el id del primer almacen activo (por id) que actua como hub de transito.
     * Retorna -1 si no hay ninguno disponible.
     */
    public int buscarAlmacenHub(Connection conn) throws SQLException {
        QueryRunner runner = new QueryRunner();
        List<Object[]> r = runner.query(conn,
                "SELECT id FROM PuntoLogistico WHERE tipo = 'ALMACEN' AND activo = 1 ORDER BY id LIMIT 1",
                new ArrayListHandler());
        return r.isEmpty() ? -1 : ((Number) r.get(0)[0]).intValue();
    }

    /**
     * Busca el vehiculo compatible de menor capacidad que cubra el paquete y este en la base indicada.
     * Retorna null si no hay vehiculo disponible.
     */
    public Integer buscarVehiculoCompatible(int idPuntoBase, double pesoKg, long volumenCm3,
                                            Connection conn) throws SQLException {
        String sql = "SELECT id FROM Vehiculo "
                + "WHERE estado = 'ACTIVO' "
                + "  AND capacidadPesoKg >= ? "
                + "  AND CAST(capacidadVolumenM3 * 1000000 AS INTEGER) >= ? "
                + "  AND idBaseOperativa = ? "
                + "ORDER BY capacidadPesoKg ASC LIMIT 1";
        QueryRunner runner = new QueryRunner();
        List<Object[]> r = runner.query(conn, sql, new ArrayListHandler(), pesoKg, volumenCm3, idPuntoBase);
        return r.isEmpty() ? null : ((Number) r.get(0)[0]).intValue();
    }

    /** Crea un registro en Ruta y devuelve su id. */
    public int insertarRuta(int idEnvio, Connection conn) throws SQLException {
        QueryRunner runner = new QueryRunner();
        runner.update(conn,
                "INSERT INTO Ruta (idEnvio, fechaPlanificacion, estado) VALUES (?, datetime('now'), 'PLANIFICADA')",
                idEnvio);
        List<Object[]> r = runner.query(conn, "SELECT last_insert_rowid()", new ArrayListHandler());
        return ((Number) r.get(0)[0]).intValue();
    }

    /** Inserta un tramo en TramoRuta. */
    public void insertarTramo(int idRuta, TramoCalculado tramo, Connection conn) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String fechaStr = tramo.getFechaPrevista().format(FMT);
        runner.update(conn,
                "INSERT INTO TramoRuta (idRuta, ordenSecuencia, tipo, idPuntoOrigen, idPuntoDestino, "
                + "direccionOrigen, direccionDestino, idVehiculo, fechaPrevista, estado) "
                + "VALUES (?,?,?,?,?,?,?,?,?,'PLANIFICADO')",
                idRuta, tramo.getOrdenSecuencia(), tramo.getTipo(),
                tramo.getIdPuntoOrigen(), tramo.getIdPuntoDestino(),
                tramo.getDireccionOrigen(), tramo.getDireccionDestino(),
                tramo.getIdVehiculo(), fechaStr);
    }

    /** Actualiza el estado del envio y, opcionalmente, la fecha estimada de entrega. */
    public void actualizarEstadoEnvio(int idEnvio, String estado, String fechaEstimada,
                                      Connection conn) throws SQLException {
        QueryRunner runner = new QueryRunner();
        if (fechaEstimada != null) {
            runner.update(conn,
                    "UPDATE Envio SET estado = ?, fechaEstimadaEntrega = ? WHERE id = ?",
                    estado, fechaEstimada, idEnvio);
        } else {
            runner.update(conn, "UPDATE Envio SET estado = ? WHERE id = ?", estado, idEnvio);
        }
    }

    /** Inserta un evento en HistorialEvento. */
    public void insertarHistorial(int idEnvio, String accion, String estadoResultante,
                                  String comentario, Connection conn) throws SQLException {
        QueryRunner runner = new QueryRunner();
        runner.update(conn,
                "INSERT INTO HistorialEvento (idEnvio, accion, estadoResultante, comentario) VALUES (?,?,?,?)",
                idEnvio, accion, estadoResultante, comentario);
    }
}
