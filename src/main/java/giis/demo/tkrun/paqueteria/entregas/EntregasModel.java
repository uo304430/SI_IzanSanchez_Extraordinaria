package giis.demo.tkrun.paqueteria.entregas;

import giis.demo.util.ApplicationException;
import giis.demo.util.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EntregasModel {

    private static final Logger log = LoggerFactory.getLogger(EntregasModel.class);
    private static final DateTimeFormatter FMT_DB      = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FMT_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE_IN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Database db;

    public EntregasModel()            { this.db = new Database(); }
    public EntregasModel(Database db) { this.db = db; }

    // -------------------------------------------------------------------------
    // Listado de entregas asignadas al vehiculo del transportista
    // -------------------------------------------------------------------------

    public List<EntregaListadoDto> getEntregasAsignadas(int idVehiculo) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT tr.id, e.id, e.codigo, e.destinatarioNombre, e.destinatarioTelefono, "
                + "CASE WHEN tr.direccionDestino IS NOT NULL THEN tr.direccionDestino "
                + "     ELSE pl.codigo END, "
                + "tr.fechaPrevista, "
                + "COALESCE(ie.cnt, 0) + 1, "
                + "COALESCE(p.descripcion, '-') "
                + "FROM TramoRuta tr "
                + "JOIN Ruta r ON tr.idRuta = r.id "
                + "JOIN Envio e ON r.idEnvio = e.id "
                + "LEFT JOIN PuntoLogistico pl ON tr.idPuntoDestino = pl.id "
                + "LEFT JOIN Paquete p ON p.idEnvio = e.id "
                + "LEFT JOIN (SELECT idEnvio, COUNT(*) AS cnt FROM IntentoEntrega GROUP BY idEnvio) ie "
                + "       ON ie.idEnvio = e.id "
                + "WHERE tr.tipo = 'ENTREGA' "
                + "  AND tr.idVehiculo = ? "
                + "  AND tr.estado IN ('PLANIFICADO','FALLIDO') "
                + "ORDER BY tr.fechaPrevista",
                idVehiculo);

        List<EntregaListadoDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            EntregaListadoDto dto = new EntregaListadoDto();
            dto.setIdTramo(((Number) row[0]).intValue());
            dto.setIdEnvio(((Number) row[1]).intValue());
            dto.setCodigoEnvio(str(row[2]));
            dto.setDestinatario(str(row[3]));
            dto.setTelefono(str(row[4]));
            dto.setDireccion(str(row[5]));
            dto.setFechaPrevista(formatDatetime(row[6]));
            dto.setIntentoActual(((Number) row[7]).intValue());
            dto.setDescripcionPaquete(str(row[8]));
            result.add(dto);
        }
        log.debug("getEntregasAsignadas idVehiculo={} -> {} entregas", idVehiculo, result.size());
        return result;
    }

    // -------------------------------------------------------------------------
    // Contar intentos previos de un envio
    // -------------------------------------------------------------------------

    public int contarIntentosPrevios(int idEnvio) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT COUNT(*) FROM IntentoEntrega WHERE idEnvio = ?", idEnvio);
        return ((Number) rows.get(0)[0]).intValue();
    }

    // -------------------------------------------------------------------------
    // Entrega completada
    // -------------------------------------------------------------------------

    public ResultadoEntregaDto registrarEntregaCompletada(int idEnvio, int idTramo, int idTransportista) {
        String ahora = LocalDateTime.now().format(FMT_DB);
        int numeroIntento = contarIntentosPrevios(idEnvio) + 1;

        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);
            try {
                insertarIntentoEntrega(con, idEnvio, idTramo, numeroIntento, idTransportista, ahora, "COMPLETADO", null, null);

                ejecutar(con, "UPDATE TramoRuta SET estado='COMPLETADO', fechaReal=? WHERE id=?", ahora, idTramo);
                ejecutar(con, "UPDATE Ruta SET estado='COMPLETADA' WHERE idEnvio=?", idEnvio);
                ejecutar(con, "UPDATE Envio SET estado='ENTREGADO', fechaEntregaReal=? WHERE id=?", ahora, idEnvio);

                String comentarioH = "Entrega completada en intento " + numeroIntento + " de 4";
                insertarHistorial(con, idEnvio, ahora, "ENTREGA_COMPLETADA", idTransportista, "ENTREGADO", comentarioH);

                String emailCliente = obtenerEmailCliente(con, idEnvio);
                String codigoEnvio  = obtenerCodigoEnvio(con, idEnvio);
                String cuerpo = "Su envio " + codigoEnvio + " ha sido entregado correctamente. Fecha: " + ahora;
                insertarAvisoCliente(con, idEnvio, "ENTREGADO", emailCliente,
                        "Su envio " + codigoEnvio + " ha sido entregado", cuerpo);

                con.commit();
                log.info("Entrega completada: idEnvio={}, intento={}", idEnvio, numeroIntento);
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar entrega completada: " + e.getMessage(), e);
        }

        ResultadoEntregaDto res = new ResultadoEntregaDto();
        res.setExito(true);
        res.setNumeroIntento(numeroIntento);
        res.setEsCuartoFallo(false);
        return res;
    }

    // -------------------------------------------------------------------------
    // Entrega fallida
    // -------------------------------------------------------------------------

    public ResultadoEntregaDto registrarEntregaFallida(int idEnvio, int idTramo, int idTransportista,
                                                        String motivo, String comentario) {
        if (motivo == null || motivo.isEmpty())
            throw new ApplicationException("El motivo del fallo es obligatorio.");
        if ("OTROS".equals(motivo) && (comentario == null || comentario.trim().isEmpty()))
            throw new ApplicationException("Debe indicar el motivo en el comentario cuando selecciona 'OTROS'.");

        String ahora = LocalDateTime.now().format(FMT_DB);
        int numeroIntento = contarIntentosPrevios(idEnvio) + 1;
        boolean esCuartoFallo = (numeroIntento == 4);

        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);
            try {
                insertarIntentoEntrega(con, idEnvio, idTramo, numeroIntento, idTransportista, ahora,
                        "FALLIDO", motivo, comentario);

                if (esCuartoFallo) {
                    ejecutar(con, "UPDATE Envio SET estado='DEPOSITADO_EN_PUNTO' WHERE id=?", idEnvio);
                    ejecutar(con, "UPDATE TramoRuta SET estado='FALLIDO', fechaReal=? WHERE id=?", ahora, idTramo);
                } else {
                    ejecutar(con, "UPDATE Envio SET estado='PENDIENTE_REENTREGA' WHERE id=?", idEnvio);
                    ejecutar(con, "UPDATE TramoRuta SET estado='FALLIDO' WHERE id=?", idTramo);
                }

                String estadoResultante = esCuartoFallo ? "DEPOSITADO_EN_PUNTO" : "PENDIENTE_REENTREGA";
                String comentarioH = "Intento " + numeroIntento + " de 4 fallido — " + motivo
                        + (comentario != null && !comentario.trim().isEmpty() ? ": " + comentario.trim() : "");
                insertarHistorial(con, idEnvio, ahora, "INTENTO_ENTREGA", idTransportista, estadoResultante, comentarioH);

                String emailCliente = obtenerEmailCliente(con, idEnvio);
                String codigoEnvio  = obtenerCodigoEnvio(con, idEnvio);
                String codigoPunto  = obtenerCodigoPuntoDestino(con, idEnvio);
                if (esCuartoFallo) {
                    String[] infoPunto = obtenerInfoPuntoDestino(con, idEnvio);
                    String cuerpo = "Tras varios intentos de entrega sin exito, su envio queda disponible para "
                            + "recogida en " + infoPunto[0] + ", " + infoPunto[1]
                            + ", horario " + infoPunto[2] + ".";
                    insertarAvisoCliente(con, idEnvio, "DEPOSITADO", emailCliente,
                            "Envio " + codigoEnvio + " disponible para recogida", cuerpo);
                } else {
                    String cuerpo = "Hemos intentado entregar su envio en la direccion indicada pero no ha sido "
                            + "posible. Motivo: " + motivo + ". Se realizara un nuevo intento.";
                    insertarAvisoCliente(con, idEnvio, "INTENTO_FALLIDO", emailCliente,
                            "Intento de entrega fallido — su envio " + codigoEnvio, cuerpo);
                }

                con.commit();
                log.info("Entrega fallida: idEnvio={}, intento={}, motivo={}", idEnvio, numeroIntento, motivo);

                ResultadoEntregaDto res = new ResultadoEntregaDto();
                res.setExito(false);
                res.setNumeroIntento(numeroIntento);
                res.setEsCuartoFallo(esCuartoFallo);
                res.setCodigoPuntoDestino(codigoPunto);
                return res;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar entrega fallida: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers de transaccion
    // -------------------------------------------------------------------------

    private void insertarIntentoEntrega(Connection con, int idEnvio, int idTramo, int numeroIntento,
            int idTransportista, String ahora, String resultado, String motivo, String comentario)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO IntentoEntrega (idEnvio, idTramoEntrega, numeroIntento, idTransportista, "
                + "fechaIntento, resultado, motivoFallo, comentario) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setInt(1, idEnvio);
            ps.setInt(2, idTramo);
            ps.setInt(3, numeroIntento);
            ps.setInt(4, idTransportista);
            ps.setString(5, ahora);
            ps.setString(6, resultado);
            if (motivo != null) ps.setString(7, motivo); else ps.setNull(7, java.sql.Types.VARCHAR);
            if (comentario != null && !comentario.trim().isEmpty()) ps.setString(8, comentario.trim());
            else ps.setNull(8, java.sql.Types.VARCHAR);
            ps.executeUpdate();
        }
    }

    private void insertarHistorial(Connection con, int idEnvio, String ahora, String accion,
            int idTransportista, String estadoResultante, String comentario) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO HistorialEvento (idEnvio, fechaEvento, accion, idUsuarioResponsable, "
                + "idPuntoLogistico, estadoResultante, comentario) VALUES (?,?,?,?,NULL,?,?)")) {
            ps.setInt(1, idEnvio);
            ps.setString(2, ahora);
            ps.setString(3, accion);
            ps.setInt(4, idTransportista);
            ps.setString(5, estadoResultante);
            ps.setString(6, comentario);
            ps.executeUpdate();
        }
    }

    private void insertarAvisoCliente(Connection con, int idEnvio, String tipoEvento,
            String email, String asunto, String cuerpo) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO AvisoCliente (idEnvio, tipoEvento, canal, destinatarioEmail, "
                + "asunto, cuerpo, estado, intentosEnvio) VALUES (?,?,'EMAIL',?,?,?,'ENVIADO',1)")) {
            ps.setInt(1, idEnvio);
            ps.setString(2, tipoEvento);
            if (email != null && !email.isEmpty()) ps.setString(3, email);
            else ps.setNull(3, java.sql.Types.VARCHAR);
            ps.setString(4, asunto);
            ps.setString(5, cuerpo);
            ps.executeUpdate();
        }
    }

    private void ejecutar(Connection con, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++)
                if (params[i] instanceof Integer) ps.setInt(i + 1, (Integer) params[i]);
                else ps.setString(i + 1, (String) params[i]);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers de consulta
    // -------------------------------------------------------------------------

    private String obtenerEmailCliente(Connection con, int idEnvio) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT u.email FROM Envio e LEFT JOIN Usuario u ON e.idCliente = u.id WHERE e.id = ?")) {
            ps.setInt(1, idEnvio);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private String obtenerCodigoEnvio(Connection con, int idEnvio) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT codigo FROM Envio WHERE id = ?")) {
            ps.setInt(1, idEnvio);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return String.valueOf(idEnvio);
    }

    private String obtenerCodigoPuntoDestino(Connection con, int idEnvio) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT pl.codigo FROM PuntoLogistico pl JOIN Envio e ON e.idPuntoDestino = pl.id WHERE e.id = ?")) {
            ps.setInt(1, idEnvio);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return "";
    }

    private String[] obtenerInfoPuntoDestino(Connection con, int idEnvio) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT pl.codigo, pl.direccion || ', ' || pl.ciudad, COALESCE(pl.horarioAtencion,'Sin horario') "
                + "FROM PuntoLogistico pl JOIN Envio e ON e.idPuntoDestino = pl.id WHERE e.id = ?")) {
            ps.setInt(1, idEnvio);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new String[]{rs.getString(1), rs.getString(2), rs.getString(3)};
            }
        }
        return new String[]{"", "", ""};
    }

    private String str(Object o)  { return o == null ? "" : o.toString(); }

    private String formatDatetime(Object o) {
        if (o == null) return "-";
        String s = o.toString().trim();
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(FMT_DISPLAY);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(s, FMT_DATE_IN).atStartOfDay().format(FMT_DISPLAY);
            } catch (DateTimeParseException e2) {
                return s;
            }
        }
    }
}
