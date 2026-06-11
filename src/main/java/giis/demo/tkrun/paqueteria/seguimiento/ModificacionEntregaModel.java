package giis.demo.tkrun.paqueteria.seguimiento;

import giis.demo.util.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModificacionEntregaModel {

    private static final Logger log = LoggerFactory.getLogger(ModificacionEntregaModel.class);
    private static final DateTimeFormatter FMT_DB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Database db;

    public ModificacionEntregaModel() {
        this.db = new Database();
    }

    /**
     * Carga el contexto necesario para el formulario: idPuntoDestino del envio,
     * id del ultimo tramo, direccion actual del destinatario, etc.
     */
    public ModificacionEntregaDto cargarContexto(int idEnvio) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT e.id, e.idPuntoDestino, pl.codigo, pl.direccion, pl.ciudad, "
                + "       pl.codigoPostal, pl.horarioAtencion, "
                + "       e.destinatarioDireccion || ', ' || e.destinatarioCiudad || ' ' || e.destinatarioCodigoPostal, "
                + "       e.estado, e.modalidadEntrega "
                + "FROM Envio e "
                + "JOIN PuntoLogistico pl ON e.idPuntoDestino = pl.id "
                + "WHERE e.id = ?",
                idEnvio);
        if (rows.isEmpty()) return null;

        Object[] r = rows.get(0);
        ModificacionEntregaDto dto = new ModificacionEntregaDto();
        dto.setIdEnvio(idEnvio);
        dto.setIdPuntoDestinoActual(((Number) r[1]).intValue());
        dto.setDireccionAnterior(str(r[7]));
        dto.setEstadoEnvio(str(r[8]));
        dto.setModalidadEntrega(str(r[9]));

        // Descripcion del punto destino para mostrar en el panel B (solo lectura)
        String descPunto = str(r[2]) + " - " + str(r[3]) + ", " + str(r[4]) + " " + str(r[5]);
        dto.setCodigoPuntoDestinoActual(descPunto);

        // Horario del punto destino guardado en nuevaCiudad (campo libre en el contexto de carga).
        // El controlador lo usa solo para poblar el panel B y lo sobreescribe con
        // los datos del formulario si el usuario elige la opcion A.
        dto.setNuevaCiudad(r[6] == null ? "Sin horario especificado" : str(r[6]));

        // Ultimo tramo (mayor ordenSecuencia)
        List<Object[]> tramos = db.executeQueryArray(
                "SELECT tr.id, tr.fechaPrevista "
                + "FROM TramoRuta tr "
                + "JOIN Ruta r ON tr.idRuta = r.id "
                + "WHERE r.idEnvio = ? "
                + "ORDER BY tr.ordenSecuencia DESC "
                + "LIMIT 1",
                idEnvio);
        if (!tramos.isEmpty()) {
            dto.setIdTramoEntrega(((Number) tramos.get(0)[0]).intValue());
            dto.setFechaPrevistaUltimoTramo(str(tramos.get(0)[1]));
        }

        log.debug("cargarContexto idEnvio={} -> tramoEntrega={}, puntoDestino={}",
                idEnvio, dto.getIdTramoEntrega(), dto.getIdPuntoDestinoActual());
        return dto;
    }

    /**
     * Devuelve [nombre, telefono] del destinatario del envio.
     */
    public String[] obtenerNombreTelefonoDestinatario(int idEnvio) {
        List<Object[]> rows = db.executeQueryArray(
                "SELECT destinatarioNombre, destinatarioTelefono FROM Envio WHERE id = ?",
                idEnvio);
        if (rows.isEmpty()) return new String[]{"", ""};
        return new String[]{str(rows.get(0)[0]), str(rows.get(0)[1])};
    }

    /**
     * Valida que el codigo postal nuevo pertenezca a la misma zona que el punto destino.
     * Compara los dos primeros digitos del CP nuevo con los CPs de los PuntoLogistico
     * de la misma zona que el punto destino del envio.
     * @return true si la validacion pasa (misma zona o CP valido en zona)
     */
    public boolean esMismaZona(int idEnvio, String cpNuevo) {
        if (cpNuevo == null || cpNuevo.length() < 2) return false;
        String prefijo = cpNuevo.substring(0, 2);

        List<Object[]> rows = db.executeQueryArray(
                "SELECT pl.codigoPostal "
                + "FROM PuntoLogistico pl "
                + "WHERE pl.idZona = (SELECT idZonaDestino FROM Envio WHERE id = ?) "
                + "AND pl.activo = 1",
                idEnvio);

        for (Object[] row : rows) {
            String cpPunto = str(row[0]);
            if (cpPunto.length() >= 2 && cpPunto.substring(0, 2).equals(prefijo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Aplica la modificacion en una unica transaccion.
     */
    public void aplicarModificacion(ModificacionEntregaDto dto, int idUsuarioResponsable) {
        String ahora = LocalDateTime.now().format(FMT_DB);
        try (Connection con = db.getConnection()) {
            con.setAutoCommit(false);
            try {
                if (dto.getOpcion() == 'A') {
                    aplicarOpcionA(con, dto);
                } else {
                    aplicarOpcionB(con, dto);
                }
                incrementarContador(con, dto.getIdEnvio());
                insertarHistorial(con, dto, idUsuarioResponsable, ahora);
                con.commit();
                log.info("Modificacion entrega confirmada para envio {}", dto.getIdEnvio());
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al aplicar modificacion de entrega: " + e.getMessage(), e);
        }
    }

    private void aplicarOpcionA(Connection con, ModificacionEntregaDto dto) throws SQLException {
        String nuevaDireccionCompleta = dto.getNuevaDireccion() + ", " + dto.getNuevaCiudad() + " " + dto.getNuevoCodigoPostal();

        // Actualizar el tramo de entrega
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE TramoRuta SET idPuntoDestino = NULL, direccionDestino = ? WHERE id = ?")) {
            ps.setString(1, nuevaDireccionCompleta);
            ps.setInt(2, dto.getIdTramoEntrega());
            ps.executeUpdate();
        }

        // Actualizar datos del envio (destinatario + direccion)
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE Envio SET destinatarioNombre = ?, destinatarioTelefono = ?, "
                + "destinatarioDireccion = ?, destinatarioCiudad = ?, destinatarioCodigoPostal = ? "
                + "WHERE id = ?")) {
            ps.setString(1, dto.getNuevoNombreDestinatario());
            ps.setString(2, dto.getNuevoTelefonoDestinatario());
            ps.setString(3, dto.getNuevaDireccion());
            ps.setString(4, dto.getNuevaCiudad());
            ps.setString(5, dto.getNuevoCodigoPostal());
            ps.setInt(6, dto.getIdEnvio());
            ps.executeUpdate();
        }
    }

    private void aplicarOpcionB(Connection con, ModificacionEntregaDto dto) throws SQLException {
        // Redirigir el ultimo tramo al punto destino del envio
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE TramoRuta SET idPuntoDestino = ?, direccionDestino = NULL WHERE id = ?")) {
            ps.setInt(1, dto.getIdPuntoDestinoActual());
            ps.setInt(2, dto.getIdTramoEntrega());
            ps.executeUpdate();
        }

        // Si la modalidad era DOMICILIO, cambiarla a OFICINA
        if ("DOMICILIO".equals(dto.getModalidadEntrega())) {
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Envio SET modalidadEntrega = 'OFICINA' WHERE id = ?")) {
                ps.setInt(1, dto.getIdEnvio());
                ps.executeUpdate();
            }

            // Actualizar fechaEstimadaEntrega con la fecha prevista del ultimo tramo
            if (dto.getFechaPrevistaUltimoTramo() != null && !dto.getFechaPrevistaUltimoTramo().isEmpty()) {
                String fechaSolo = dto.getFechaPrevistaUltimoTramo().substring(0, 10);
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE Envio SET fechaEstimadaEntrega = ? WHERE id = ?")) {
                    ps.setString(1, fechaSolo);
                    ps.setInt(2, dto.getIdEnvio());
                    ps.executeUpdate();
                }
            }
        }
    }

    private void incrementarContador(Connection con, int idEnvio) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE Envio SET modificacionesEntrega = modificacionesEntrega + 1 WHERE id = ?")) {
            ps.setInt(1, idEnvio);
            ps.executeUpdate();
        }
    }

    private void insertarHistorial(Connection con, ModificacionEntregaDto dto,
            int idUsuarioResponsable, String ahora) throws SQLException {
        String comentario;
        if (dto.getOpcion() == 'A') {
            String nueva = dto.getNuevaDireccion() + ", " + dto.getNuevaCiudad() + " " + dto.getNuevoCodigoPostal();
            comentario = "Direccion modificada: " + dto.getDireccionAnterior() + " -> " + nueva;
        } else {
            comentario = "Cambiado a recogida en " + dto.getCodigoPuntoDestinoActual();
        }

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO HistorialEvento "
                + "(idEnvio, fechaEvento, accion, idUsuarioResponsable, idPuntoLogistico, estadoResultante, comentario) "
                + "VALUES (?, ?, 'MODIFICACION_ENTREGA', ?, NULL, ?, ?)")) {
            ps.setInt(1, dto.getIdEnvio());
            ps.setString(2, ahora);
            ps.setInt(3, idUsuarioResponsable);
            ps.setString(4, dto.getEstadoEnvio());
            ps.setString(5, comentario);
            ps.executeUpdate();
        }
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
