package giis.demo.tkrun.paqueteria.seguimiento;

import giis.demo.util.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class SeguimientoModel {

    private static final Logger log = LoggerFactory.getLogger(SeguimientoModel.class);
    private static final DateTimeFormatter FMT_ENTRADA       = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FMT_ENTRADA_DATE  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_COMPACTO      = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_COMPLETO      = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FMT_FECHA         = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Database db;

    public SeguimientoModel() {
        this.db = new Database();
    }

    // -------------------------------------------------------------------------
    // Listado de envios del cliente
    // -------------------------------------------------------------------------

    public List<EnvioListadoDto> getEnviosCliente(int idCliente, String filtroEstado) {
        String sql = "SELECT e.id, e.codigo, e.fechaCreacion, e.destinatarioNombre, e.estado, e.fechaEstimadaEntrega "
                + "FROM Envio e WHERE e.idCliente = ?";
        Object[] params;
        if (filtroEstado != null && !filtroEstado.isEmpty()) {
            sql += " AND e.estado = ?";
            sql += " ORDER BY e.fechaCreacion DESC";
            params = new Object[]{idCliente, filtroEstado};
        } else {
            sql += " ORDER BY e.fechaCreacion DESC";
            params = new Object[]{idCliente};
        }
        List<Object[]> rows = db.executeQueryArray(sql, params);
        List<EnvioListadoDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            EnvioListadoDto dto = new EnvioListadoDto();
            dto.setId(((Number) row[0]).intValue());
            dto.setCodigo(str(row[1]));
            dto.setFechaRegistro(formatDatetime(row[2], FMT_COMPACTO));
            dto.setDestinatario(str(row[3]));
            dto.setEstado(str(row[4]));
            dto.setFechaEstimada(formatDate(row[5]));
            result.add(dto);
        }
        log.debug("getEnviosCliente idCliente={} filtro={} -> {} resultados", idCliente, filtroEstado, result.size());
        return result;
    }

    /**
     * Busca el id del envio por su codigo si pertenece al cliente dado.
     * @return id del envio, o -1 si no existe o no es del cliente.
     */
    public int buscarEnvioPorCodigo(String codigo, int idCliente) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT e.id FROM Envio e WHERE e.codigo = ? AND e.idCliente = ?",
                codigo, idCliente);
        if (rows.isEmpty()) return -1;
        return ((Number) rows.get(0)[0]).intValue();
    }

    // -------------------------------------------------------------------------
    // Detalle del envio
    // -------------------------------------------------------------------------

    public EnvioDetalleDto getEnvioDetalle(int idEnvio) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT e.id, e.codigo, e.estado, e.fechaCreacion, "
                + "ts.descripcion, e.modalidadEntrega, e.costeCalculado, e.valorDeclarado, "
                + "e.destinatarioNombre, e.destinatarioTelefono, "
                + "e.destinatarioDireccion || ', ' || e.destinatarioCiudad || ' ' || e.destinatarioCodigoPostal, "
                + "e.fechaEstimadaEntrega, e.fechaEntregaReal, e.modificacionesEntrega "
                + "FROM Envio e "
                + "JOIN TipoServicio ts ON e.idTipoServicio = ts.id "
                + "WHERE e.id = ?",
                idEnvio);
        if (rows.isEmpty()) return null;
        Object[] r = rows.get(0);
        EnvioDetalleDto dto = new EnvioDetalleDto();
        dto.setId(((Number) r[0]).intValue());
        dto.setCodigo(str(r[1]));
        dto.setEstado(str(r[2]));
        dto.setFechaRegistro(formatDatetime(r[3], FMT_COMPACTO));
        dto.setTipoServicio(str(r[4]));
        dto.setModalidadEntrega(str(r[5]));
        dto.setCoste(formatImporte(r[6]));
        dto.setValorDeclarado(formatImporte(r[7]));
        dto.setDestinatarioNombre(str(r[8]));
        dto.setDestinatarioTelefono(str(r[9]));
        dto.setDireccionDestinatario(str(r[10]));
        dto.setFechaEstimada(formatDate(r[11]));
        dto.setFechaEntregaReal(r[12] == null ? "-" : formatDatetime(r[12], FMT_COMPACTO));
        dto.setModificacionesEntrega(r[13] == null ? 0 : ((Number) r[13]).intValue());
        return dto;
    }

    public List<TramoListadoDto> getTramosDeEnvio(int idEnvio) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT tr.ordenSecuencia, tr.tipo, "
                + "CASE WHEN tr.idPuntoOrigen IS NULL "
                + "     THEN 'Domicilio: ' || COALESCE(tr.direccionOrigen, '') "
                + "     ELSE po.codigo END, "
                + "CASE WHEN tr.idPuntoDestino IS NULL "
                + "     THEN 'Domicilio: ' || COALESCE(tr.direccionDestino, '') "
                + "     ELSE pd.codigo END, "
                + "COALESCE(v.matricula, '-'), "
                + "tr.fechaPrevista, tr.fechaReal, tr.estado "
                + "FROM TramoRuta tr "
                + "JOIN Ruta r ON tr.idRuta = r.id "
                + "LEFT JOIN PuntoLogistico po ON tr.idPuntoOrigen = po.id "
                + "LEFT JOIN PuntoLogistico pd ON tr.idPuntoDestino = pd.id "
                + "LEFT JOIN Vehiculo v ON tr.idVehiculo = v.id "
                + "WHERE r.idEnvio = ? "
                + "ORDER BY tr.ordenSecuencia",
                idEnvio);
        List<TramoListadoDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            TramoListadoDto dto = new TramoListadoDto();
            dto.setOrden(((Number) row[0]).intValue());
            dto.setTipo(str(row[1]));
            dto.setOrigen(str(row[2]));
            dto.setDestino(str(row[3]));
            dto.setVehiculo(str(row[4]));
            dto.setFechaPrevista(formatDatetime(row[5], FMT_COMPACTO));
            dto.setFechaReal(row[6] == null ? "-" : formatDatetime(row[6], FMT_COMPACTO));
            dto.setEstado(str(row[7]));
            result.add(dto);
        }
        return result;
    }

    public List<EventoHistorialDto> getHistorialDeEnvio(int idEnvio) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT he.fechaEvento, he.accion, "
                + "CASE WHEN he.idUsuarioResponsable IS NULL THEN 'Sistema' "
                + "     ELSE rl.nombre || ': ' || u.nombre END, "
                + "COALESCE(pl.codigo, '-'), "
                + "COALESCE(he.comentario, '-') "
                + "FROM HistorialEvento he "
                + "LEFT JOIN Usuario u  ON he.idUsuarioResponsable = u.id "
                + "LEFT JOIN Rol rl     ON u.idRol = rl.id "
                + "LEFT JOIN PuntoLogistico pl ON he.idPuntoLogistico = pl.id "
                + "WHERE he.idEnvio = ? "
                + "ORDER BY he.fechaEvento DESC",
                idEnvio);
        List<EventoHistorialDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            EventoHistorialDto dto = new EventoHistorialDto();
            dto.setFechaEvento(formatDatetime(row[0], FMT_COMPLETO));
            dto.setAccion(str(row[1]));
            dto.setResponsable(str(row[2]));
            dto.setPunto(str(row[3]));
            dto.setComentario(str(row[4]));
            result.add(dto);
        }
        return result;
    }

    public List<IncidenciaListadoDto> getIncidenciasDeEnvio(int idEnvio) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT i.tipo, i.fechaApertura, i.descripcion, i.estado "
                + "FROM Incidencia i "
                + "WHERE i.idEnvio = ? "
                + "ORDER BY i.fechaApertura",
                idEnvio);
        List<IncidenciaListadoDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            IncidenciaListadoDto dto = new IncidenciaListadoDto();
            dto.setTipo(str(row[0]));
            dto.setFechaApertura(formatDatetime(row[1], FMT_COMPACTO));
            dto.setDescripcion(str(row[2]));
            dto.setEstado(str(row[3]));
            result.add(dto);
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private String formatDatetime(Object o, DateTimeFormatter fmt) {
        if (o == null) return "-";
        String s = o.toString().trim();
        if (s.isEmpty()) return "-";
        try {
            return LocalDateTime.parse(s, FMT_ENTRADA).format(fmt);
        } catch (DateTimeParseException e1) {
            // SQLite may return just "yyyy-MM-dd" for some datetime fields
            try {
                return LocalDate.parse(s, FMT_ENTRADA_DATE).atStartOfDay().format(fmt);
            } catch (DateTimeParseException e2) {
                return s;
            }
        }
    }

    private String formatDate(Object o) {
        if (o == null) return "-";
        String s = o.toString().trim();
        if (s.isEmpty()) return "-";
        try {
            return LocalDate.parse(s, FMT_ENTRADA_DATE).format(FMT_FECHA);
        } catch (DateTimeParseException e) {
            return s;
        }
    }

    private String formatImporte(Object o) {
        if (o == null) return "0,00 EUR";
        try {
            double val = Double.parseDouble(o.toString());
            return String.format("%.2f", val).replace('.', ',') + " EUR";
        } catch (NumberFormatException e) {
            return o.toString();
        }
    }
}
