package giis.demo.tkrun.paqueteria.envios;

import giis.demo.util.Database;
import giis.demo.util.UnexpectedException;
import giis.demo.tkrun.paqueteria.rutas.AsignacionRutaService;
import giis.demo.tkrun.paqueteria.rutas.ResultadoAsignacion;
import giis.demo.tkrun.paqueteria.util.CalculadoraTarifa;
import giis.demo.tkrun.paqueteria.util.ComboItem;
import giis.demo.tkrun.paqueteria.util.GeneradorCodigos;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnvioModel {

    private static final Logger log = LoggerFactory.getLogger(EnvioModel.class);
    private final Database db;

    public EnvioModel() {
        this.db = new Database();
    }

    /** Devuelve todas las zonas para poblar combos. */
    public List<ComboItem> getZonas() {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT id, codigo || ' - ' || descripcion FROM Zona ORDER BY id");
        return toComboItems(rows);
    }

    /** Devuelve todos los tipos de servicio activos para poblar combos. */
    public List<ComboItem> getTiposServicio() {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT id, codigo || ' - ' || descripcion FROM TipoServicio ORDER BY id");
        return toComboItems(rows);
    }

    /**
     * Devuelve los puntos logisticos activos.
     * @param filtroTipo 'OFICINA' o 'ALMACEN' para filtrar; null para traer todos.
     */
    public List<ComboItem> getPuntosLogisticos(String filtroTipo) {
        List<Object[]> rows;
        if (filtroTipo != null) {
            rows = db.executeQueryArray(
                    "SELECT id, codigo || ' — ' || ciudad FROM PuntoLogistico WHERE activo=1 AND tipo=? ORDER BY ciudad",
                    filtroTipo);
        } else {
            rows = db.executeQueryArray(
                    "SELECT id, codigo || ' — ' || ciudad FROM PuntoLogistico WHERE activo=1 ORDER BY ciudad");
        }
        return toComboItems(rows);
    }

    /** Delega en CalculadoraTarifa para obtener el precio vigente. */
    public Optional<BigDecimal> calcularCoste(int idTipoServicio, BigDecimal pesoKg,
                                              int idZonaOrigen, int idZonaDestino) {
        return new CalculadoraTarifa(db).buscarTarifaVigente(idTipoServicio, pesoKg, idZonaOrigen, idZonaDestino);
    }

    /**
     * Registra un envio en una unica transaccion: INSERT en Envio, Paquete e HistorialEvento.
     * Si cualquier paso falla se hace rollback completo.
     */
    public EnvioResumenDto registrarEnvio(EnvioCreacionDto dto, int idEmpleado, int idPuntoOrigen) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            QueryRunner runner = new QueryRunner();

            LocalDate hoy = LocalDate.now();
            String codigoEnvio = GeneradorCodigos.generarCodigoEnvio(conn, hoy);
            String codigoBarras = GeneradorCodigos.generarCodigoBarras(codigoEnvio);

            String sqlEnvio = "INSERT INTO Envio ("
                    + "codigo, canalOrigen, idTipoServicio,"
                    + "remitenteNombre, remitenteDni, remitenteTelefono,"
                    + "remitenteDireccion, remitenteCiudad, remitenteCodigoPostal, idZonaOrigen,"
                    + "destinatarioNombre, destinatarioTelefono,"
                    + "destinatarioDireccion, destinatarioCiudad, destinatarioCodigoPostal, idZonaDestino,"
                    + "modalidadRecogida, modalidadEntrega,"
                    + "idPuntoOrigen, idPuntoDestino,"
                    + "estado, costeCalculado, valorDeclarado, formaPago, pagado,"
                    + "idUsuarioCreador"
                    + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            runner.update(conn, sqlEnvio,
                    codigoEnvio, "OFICINA", dto.getIdTipoServicio(),
                    dto.getRemitenteNombre(), dto.getRemitenteDni(), dto.getRemitenteTelefono(),
                    emptyToNull(dto.getRemitenteDireccion()),
                    emptyToNull(dto.getRemitenteCiudad()),
                    emptyToNull(dto.getRemitenteCodigoPostal()),
                    dto.getIdZonaOrigen(),
                    dto.getDestinatarioNombre(), dto.getDestinatarioTelefono(),
                    dto.getDestinatarioDireccion(), dto.getDestinatarioCiudad(),
                    dto.getDestinatarioCodigoPostal(), dto.getIdZonaDestino(),
                    dto.getModalidadRecogida(), dto.getModalidadEntrega(),
                    idPuntoOrigen, dto.getIdPuntoDestino(),
                    "REGISTRADO", dto.getCosteCalculado(),
                    dto.getValorDeclarado() != null ? dto.getValorDeclarado() : BigDecimal.ZERO,
                    dto.getFormaPago(), 1,
                    idEmpleado
            );

            List<Object[]> idResult = runner.query(conn, "SELECT last_insert_rowid()", new ArrayListHandler());
            int idEnvio = ((Number) idResult.get(0)[0]).intValue();

            runner.update(conn,
                    "INSERT INTO Paquete (idEnvio, codigoBarras, descripcion, pesoDeclaradoKg, largoCm, anchoCm, altoCm, estadoFisico)"
                            + " VALUES (?,?,?,?,?,?,?,?)",
                    idEnvio, codigoBarras, dto.getDescripcionPaquete(), dto.getPesoKg(),
                    dto.getLargoCm(), dto.getAnchoCm(), dto.getAltoCm(), "CORRECTO");

            runner.update(conn,
                    "INSERT INTO HistorialEvento (idEnvio, accion, idUsuarioResponsable, idPuntoLogistico, estadoResultante, comentario)"
                            + " VALUES (?,?,?,?,?,?)",
                    idEnvio, "REGISTRO_INICIAL", idEmpleado, idPuntoOrigen,
                    "REGISTRADO", "Envio registrado en oficina");

            ResultadoAsignacion asignacion = new AsignacionRutaService().asignarRuta(idEnvio, conn);

            conn.commit();
            log.info("Envio registrado: {} codigoBarras={}", codigoEnvio, codigoBarras);
            EnvioResumenDto resumen = new EnvioResumenDto(codigoEnvio, codigoBarras, dto.getCosteCalculado());
            resumen.setAvisoSinRuta(!asignacion.isExito());
            return resumen;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { log.error("Error en rollback", ex); }
            }
            throw new UnexpectedException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private List<ComboItem> toComboItems(List<Object[]> rows) {
        List<ComboItem> items = new ArrayList<>();
        for (Object[] row : rows)
            items.add(new ComboItem(((Number) row[0]).intValue(), row[1].toString()));
        return items;
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
