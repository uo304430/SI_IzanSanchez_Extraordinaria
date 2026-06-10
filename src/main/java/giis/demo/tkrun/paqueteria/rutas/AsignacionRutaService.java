package giis.demo.tkrun.paqueteria.rutas;

import giis.demo.util.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de asignacion automatica de ruta y vehiculos para un envio.
 * Recibe la conexion activa de EnvioModel y NO realiza commit/rollback.
 * Si la asignacion falla por falta de vehiculo, actualiza el estado del
 * envio a PENDIENTE_ASIGNACION y retorna exito=false sin lanzar excepcion.
 */
public class AsignacionRutaService {

    private static final Logger log = LoggerFactory.getLogger(AsignacionRutaService.class);

    private final RutaModel rutaModel;

    public AsignacionRutaService() {
        this.rutaModel = new RutaModel();
    }

    public AsignacionRutaService(RutaModel rutaModel) {
        this.rutaModel = rutaModel;
    }

    /**
     * Asigna ruta y vehiculos al envio indicado.
     * Todos los cambios se realizan dentro de la transaccion abierta en conn.
     */
    public ResultadoAsignacion asignarRuta(int idEnvio, Connection conn) throws SQLException {
        EnvioDatosAsignacion envio  = rutaModel.getEnvioDatos(idEnvio, conn);
        PaqueteDatos         paquete = rutaModel.getPaqueteDatos(idEnvio, conn);

        // 1. Construir lista de tramos
        List<TramoCalculado> tramos;
        try {
            tramos = construirTramos(envio, conn);
        } catch (ApplicationException e) {
            return fallarAsignacion(idEnvio, e.getMessage(), conn);
        }

        // 2. Asignar vehiculo a cada tramo
        long volumenCm3 = (long) paquete.getLargoCm() * paquete.getAnchoCm() * paquete.getAltoCm();
        for (TramoCalculado tramo : tramos) {
            int idPuntoBase = (tramo.getIdPuntoOrigen() != null)
                    ? tramo.getIdPuntoOrigen()
                    : tramo.getIdPuntoDestino();
            Integer idVehiculo = rutaModel.buscarVehiculoCompatible(
                    idPuntoBase, paquete.getPesoDeclaradoKg(), volumenCm3, conn);
            if (idVehiculo == null) {
                String motivo = "Sin vehiculo compatible para tramo "
                        + tramo.getOrdenSecuencia() + " (" + tramo.getTipo() + ")";
                return fallarAsignacion(idEnvio, motivo, conn);
            }
            tramo.setIdVehiculo(idVehiculo);
        }

        // 3. Calcular fechas previstas
        String codigoServicio = rutaModel.getCodigoTipoServicio(envio.getIdTipoServicio(), conn);
        calcularFechasPrevistas(tramos, codigoServicio);

        // 4. Persistir Ruta + TramoRuta + actualizar Envio
        int idRuta = rutaModel.insertarRuta(idEnvio, conn);
        for (TramoCalculado tramo : tramos) {
            rutaModel.insertarTramo(idRuta, tramo, conn);
        }

        String fechaEstimada = tramos.get(tramos.size() - 1).getFechaPrevista()
                .toLocalDate().toString();
        rutaModel.actualizarEstadoEnvio(idEnvio, "EN_RUTA", fechaEstimada, conn);
        rutaModel.insertarHistorial(idEnvio, "RUTA_ASIGNADA", "EN_RUTA",
                "Ruta asignada con " + tramos.size() + " tramos", conn);

        log.info("Ruta asignada: idEnvio={}, idRuta={}, tramos={}", idEnvio, idRuta, tramos.size());
        return new ResultadoAsignacion(true, idRuta, tramos.size(), null);
    }

    // -------------------------------------------------------------------------
    // Construccion de tramos
    // -------------------------------------------------------------------------

    private List<TramoCalculado> construirTramos(EnvioDatosAsignacion envio,
                                                 Connection conn) throws SQLException {
        List<TramoCalculado> tramos = new ArrayList<>();
        int sec = 1;

        // Tramo RECOGIDA (solo si modalidad es DOMICILIO)
        if ("DOMICILIO".equals(envio.getModalidadRecogida())) {
            TramoCalculado t = new TramoCalculado();
            t.setOrdenSecuencia(sec++);
            t.setTipo("RECOGIDA");
            t.setIdPuntoOrigen(null);
            t.setDireccionOrigen(joinDireccion(
                    envio.getRemitenteDireccion(),
                    envio.getRemitenteCiudad(),
                    envio.getRemitenteCodigoPostal()));
            t.setIdPuntoDestino(envio.getIdPuntoOrigen());
            tramos.add(t);
        }

        // Tramos INTERMEDIOS
        if (envio.getIdZonaOrigen() == envio.getIdZonaDestino()) {
            // Misma zona: un unico tramo origen → destino
            TramoCalculado t = new TramoCalculado();
            t.setOrdenSecuencia(sec++);
            t.setTipo("INTERMEDIO");
            t.setIdPuntoOrigen(envio.getIdPuntoOrigen());
            t.setIdPuntoDestino(envio.getIdPuntoDestino());
            tramos.add(t);
        } else {
            // Zonas distintas: dos tramos con almacen hub intermedio
            int idHub = rutaModel.buscarAlmacenHub(conn);
            if (idHub == -1)
                throw new ApplicationException("No hay almacen disponible como hub de transito");

            TramoCalculado t1 = new TramoCalculado();
            t1.setOrdenSecuencia(sec++);
            t1.setTipo("INTERMEDIO");
            t1.setIdPuntoOrigen(envio.getIdPuntoOrigen());
            t1.setIdPuntoDestino(idHub);
            tramos.add(t1);

            TramoCalculado t2 = new TramoCalculado();
            t2.setOrdenSecuencia(sec++);
            t2.setTipo("INTERMEDIO");
            t2.setIdPuntoOrigen(idHub);
            t2.setIdPuntoDestino(envio.getIdPuntoDestino());
            tramos.add(t2);
        }

        // Tramo ENTREGA (solo si modalidad es DOMICILIO)
        if ("DOMICILIO".equals(envio.getModalidadEntrega())) {
            int idUltimoPunto = tramos.get(tramos.size() - 1).getIdPuntoDestino();
            TramoCalculado t = new TramoCalculado();
            t.setOrdenSecuencia(sec);
            t.setTipo("ENTREGA");
            t.setIdPuntoOrigen(idUltimoPunto);
            t.setIdPuntoDestino(null);
            t.setDireccionDestino(joinDireccion(
                    envio.getDestinatarioDireccion(),
                    envio.getDestinatarioCiudad(),
                    envio.getDestinatarioCodigoPostal()));
            tramos.add(t);
        }

        return tramos;
    }

    // -------------------------------------------------------------------------
    // Calculo de fechas
    // -------------------------------------------------------------------------

    private void calcularFechasPrevistas(List<TramoCalculado> tramos, String codigoServicio) {
        long horasDelta;
        switch (codigoServicio) {
            case "URGENTE_24H": horasDelta = 8;  break;
            case "URGENTE_48H": horasDelta = 12; break;
            default:            horasDelta = 24; break;
        }
        tramos.get(0).setFechaPrevista(LocalDateTime.now().plusHours(1));
        for (int i = 1; i < tramos.size(); i++) {
            tramos.get(i).setFechaPrevista(
                    tramos.get(i - 1).getFechaPrevista().plusHours(horasDelta));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ResultadoAsignacion fallarAsignacion(int idEnvio, String motivo,
                                                 Connection conn) throws SQLException {
        rutaModel.actualizarEstadoEnvio(idEnvio, "PENDIENTE_ASIGNACION", null, conn);
        rutaModel.insertarHistorial(idEnvio, "RUTA_FALLIDA", "PENDIENTE_ASIGNACION", motivo, conn);
        log.warn("Asignacion fallida para envio {}: {}", idEnvio, motivo);
        return new ResultadoAsignacion(false, 0, 0, motivo);
    }

    private String joinDireccion(String dir, String ciudad, String cp) {
        StringBuilder sb = new StringBuilder();
        if (dir   != null && !dir.isBlank())   sb.append(dir);
        if (ciudad != null && !ciudad.isBlank()) { if (sb.length() > 0) sb.append(", "); sb.append(ciudad); }
        if (cp    != null && !cp.isBlank())    { if (sb.length() > 0) sb.append(", "); sb.append(cp); }
        return sb.toString();
    }
}
