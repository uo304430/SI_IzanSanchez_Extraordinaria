package giis.demo.tkrun.paqueteria.almacen;

import giis.demo.util.ApplicationException;
import giis.demo.util.Database;
import giis.demo.util.UnexpectedException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AlmacenModel {

    private static final Logger log = LoggerFactory.getLogger(AlmacenModel.class);
    private final Database db;

    public AlmacenModel() {
        this.db = new Database();
    }

    public AlmacenModel(Database db) {
        this.db = db;
    }

    /**
     * Busca el paquete por codigo de barras y valida que existe un tramo coherente
     * con el tipo de operacion en el almacen del operario.
     *
     * @throws ApplicationException con mensaje especifico si la validacion falla
     */
    public PaqueteVerificacionDto buscarPaquete(String codigoBarras, String tipoOperacion,
                                                int idAlmacen, int idOperario) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT p.id, p.idEnvio, e.codigo, e.destinatarioNombre, p.descripcion, "
                + "COALESCE(p.pesoUltimaVerificacionKg, p.pesoDeclaradoKg), p.pesoDeclaradoKg "
                + "FROM Paquete p JOIN Envio e ON p.idEnvio = e.id "
                + "WHERE p.codigoBarras = ?",
                codigoBarras);

        if (rows.isEmpty()) {
            throw new ApplicationException("Codigo de barras no encontrado");
        }

        Object[] row      = rows.get(0);
        int idPaquete     = ((Number) row[0]).intValue();
        int idEnvio       = ((Number) row[1]).intValue();
        String codigoEnvio    = row[2].toString();
        String destinatario   = row[3].toString();
        String descripcion    = row[4].toString();
        double pesoRef        = ((Number) row[5]).doubleValue();
        double pesoDeclarado  = ((Number) row[6]).doubleValue();

        // Condicion segun tipo de operacion
        String condEstado = "ENTRADA".equals(tipoOperacion) ? "PLANIFICADO" : "ALMACENADO";
        String condPunto  = "ENTRADA".equals(tipoOperacion) ? "tr.idPuntoDestino" : "tr.idPuntoOrigen";

        List<Object[]> tramoRows = db.executeQueryArray(
                "SELECT tr.id FROM TramoRuta tr "
                + "JOIN Ruta r ON tr.idRuta = r.id "
                + "WHERE r.idEnvio = ? AND " + condPunto + " = ? AND tr.estado = ?",
                idEnvio, idAlmacen, condEstado);

        if (tramoRows.isEmpty()) {
            // Determinar si hay algun tramo del envio en este almacen (para mensaje mas preciso)
            List<Object[]> cualquierTramo = db.executeQueryArray(
                    "SELECT tr.id FROM TramoRuta tr "
                    + "JOIN Ruta r ON tr.idRuta = r.id "
                    + "WHERE r.idEnvio = ? AND (tr.idPuntoOrigen = ? OR tr.idPuntoDestino = ?)",
                    idEnvio, idAlmacen, idAlmacen);

            String motivo;
            String mensaje;
            if (cualquierTramo.isEmpty()) {
                motivo  = "Paquete no tiene tramos en el almacen " + idAlmacen;
                mensaje = "Este paquete no tiene tramos pendientes en este almacen";
            } else {
                motivo  = "Estado de tramo invalido para operacion " + tipoOperacion;
                mensaje = "El paquete no esta en estado valido para esta operacion";
            }

            db.executeUpdate(
                    "INSERT INTO HistorialEvento "
                    + "(idEnvio, accion, idUsuarioResponsable, idPuntoLogistico, comentario) "
                    + "VALUES (?,?,?,?,?)",
                    idEnvio, "VERIFICACION_FALLIDA", idOperario, idAlmacen, motivo);

            throw new ApplicationException(mensaje);
        }

        int idTramo = ((Number) tramoRows.get(0)[0]).intValue();

        PaqueteVerificacionDto dto = new PaqueteVerificacionDto();
        dto.setIdPaquete(idPaquete);
        dto.setIdEnvio(idEnvio);
        dto.setCodigoEnvio(codigoEnvio);
        dto.setIdTramo(idTramo);
        dto.setDestinatario(destinatario);
        dto.setDescripcion(descripcion);
        dto.setPesoRefKg(pesoRef);
        dto.setPesoDeclaradoKg(pesoDeclarado);
        return dto;
    }

    /**
     * Registra la operacion de carga o descarga en una unica transaccion:
     * actualiza Paquete, TramoRuta, Envio, inserta HistorialEvento y,
     * si procede, genera una Incidencia.
     */
    public ResultadoOperacionDto registrarOperacion(OperacionAlmacenDto op) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            QueryRunner runner = new QueryRunner();

            // a) Calcular discrepancia usando BigDecimal para evitar errores de punto flotante
            BigDecimal medido     = BigDecimal.valueOf(op.getPesoMedidoKg());
            BigDecimal referencia = BigDecimal.valueOf(op.getPesoRefKg());
            BigDecimal diferencia = medido.subtract(referencia).abs()
                    .divide(referencia, 10, RoundingMode.HALF_UP);
            boolean discrepancia = diferencia.compareTo(new BigDecimal("0.05")) > 0;

            // b) UPDATE Paquete
            runner.update(conn,
                    "UPDATE Paquete SET pesoUltimaVerificacionKg = ?, estadoFisico = ? WHERE id = ?",
                    op.getPesoMedidoKg(), op.getInspeccionVisual(), op.getIdPaquete());

            // c) UPDATE TramoRuta
            String nuevoEstadoTramo = "ENTRADA".equals(op.getTipoOperacion()) ? "ALMACENADO" : "EN_TRANSITO";
            runner.update(conn,
                    "UPDATE TramoRuta SET estado = ?, fechaReal = CURRENT_TIMESTAMP WHERE id = ?",
                    nuevoEstadoTramo, op.getIdTramo());

            // d) UPDATE Envio
            String nuevoEstadoEnvio = determinarEstadoEnvio(conn, op);
            runner.update(conn,
                    "UPDATE Envio SET estado = ? WHERE id = ?",
                    nuevoEstadoEnvio, op.getIdEnvio());

            // e) INSERT HistorialEvento
            String accion = "ENTRADA".equals(op.getTipoOperacion()) ? "DESCARGA_EN_ALMACEN" : "CARGA_EN_ALMACEN";
            String comentario = String.format("Peso medido: %.2f kg. Inspeccion: %s.",
                    op.getPesoMedidoKg(), op.getInspeccionVisual());
            if (discrepancia) comentario += " Discrepancia detectada.";

            String datosAdicionales = String.format(
                    "{\"pesoMedido\":%.2f,\"inspeccion\":\"%s\",\"discrepancia\":%b}",
                    op.getPesoMedidoKg(), op.getInspeccionVisual(), discrepancia);

            runner.update(conn,
                    "INSERT INTO HistorialEvento "
                    + "(idEnvio, accion, idUsuarioResponsable, idPuntoLogistico, "
                    + "estadoResultante, comentario, datosAdicionales) VALUES (?,?,?,?,?,?,?)",
                    op.getIdEnvio(), accion, op.getIdOperario(), op.getIdAlmacen(),
                    nuevoEstadoEnvio, comentario, datosAdicionales);

            // f) Generar Incidencia si procede
            boolean incidenciaGenerada = false;
            String tipoIncidencia = null;
            if ("DANO_GRAVE".equals(op.getInspeccionVisual()) || discrepancia) {
                // DANO_GRAVE tiene prioridad sobre DISCREPANCIA_PESO
                tipoIncidencia = "DANO_GRAVE".equals(op.getInspeccionVisual())
                        ? "DANO_GRAVE" : "DISCREPANCIA_PESO";
                String descIncidencia = buildDescripcionIncidencia(op, discrepancia, tipoIncidencia);
                runner.update(conn,
                        "INSERT INTO Incidencia "
                        + "(idEnvio, tipo, descripcion, idUsuarioGenerador, idPuntoLogistico, estado) "
                        + "VALUES (?,?,?,?,?,'ABIERTA')",
                        op.getIdEnvio(), tipoIncidencia, descIncidencia,
                        op.getIdOperario(), op.getIdAlmacen());
                incidenciaGenerada = true;
            }

            conn.commit();
            log.info("Operacion {} registrada: envio={} tramo={} discrepancia={}",
                    op.getTipoOperacion(), op.getIdEnvio(), op.getIdTramo(), discrepancia);

            ResultadoOperacionDto resultado = new ResultadoOperacionDto();
            resultado.setExito(true);
            resultado.setDiscrepancia(discrepancia);
            resultado.setIncidenciaGenerada(incidenciaGenerada);
            resultado.setTipoIncidencia(tipoIncidencia);
            return resultado;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { log.error("Error en rollback", ex); }
            }
            throw new UnexpectedException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Determina el nuevo estado del Envio tras la operacion.
     * SALIDA + siguiente tramo de tipo ENTREGA → EN_REPARTO.
     * Cualquier otro caso → EN_TRANSITO.
     */
    private String determinarEstadoEnvio(Connection conn, OperacionAlmacenDto op) throws SQLException {
        if (!"SALIDA".equals(op.getTipoOperacion())) {
            return "EN_TRANSITO";
        }
        QueryRunner runner = new QueryRunner();
        List<Object[]> tramoInfo = runner.query(conn,
                "SELECT idRuta, ordenSecuencia FROM TramoRuta WHERE id = ?",
                new ArrayListHandler(), op.getIdTramo());

        if (tramoInfo.isEmpty()) return "EN_TRANSITO";

        int idRuta = ((Number) tramoInfo.get(0)[0]).intValue();
        int orden  = ((Number) tramoInfo.get(0)[1]).intValue();

        List<Object[]> sigTramo = runner.query(conn,
                "SELECT tipo FROM TramoRuta WHERE idRuta = ? AND ordenSecuencia = ?",
                new ArrayListHandler(), idRuta, orden + 1);

        if (!sigTramo.isEmpty() && "ENTREGA".equals(sigTramo.get(0)[0].toString())) {
            return "EN_REPARTO";
        }
        return "EN_TRANSITO";
    }

    private String buildDescripcionIncidencia(OperacionAlmacenDto op, boolean discrepancia,
                                               String tipoIncidencia) {
        if ("DANO_GRAVE".equals(tipoIncidencia)) {
            String desc = String.format("Dano grave detectado en inspeccion visual. "
                    + "Peso medido: %.2f kg.", op.getPesoMedidoKg());
            if (discrepancia) {
                desc += String.format(" Ademas, discrepancia de peso: %.2f kg medido vs %.2f kg referencia.",
                        op.getPesoMedidoKg(), op.getPesoRefKg());
            }
            return desc;
        }
        return String.format("Discrepancia de peso superior al 5%%: "
                + "%.2f kg medido vs %.2f kg referencia.",
                op.getPesoMedidoKg(), op.getPesoRefKg());
    }
}
