package giis.demo.tkrun.paqueteria.entregas;

import giis.demo.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la capa de negocio de EntregasModel (HU-06).
 *
 * Datos relevantes en data.sql:
 *   Envio 1 (id=1): ENV-20260601-0001, EN_RUTA
 *     Tramo 4 (id=4): tipo=ENTREGA, idVehiculo=5 (7890-EEE), PLANIFICADO, sin intentos previos
 *   Envio 3 (id=3): ENV-20260525-0003, PENDIENTE_REENTREGA
 *     Tramo 11 (id=11): tipo=ENTREGA, idVehiculo=5 (7890-EEE), FALLIDO, 2 intentos previos
 *   Transportista Javier Martin: idUsuario=7, idVehiculoHabitual=5
 */
class EntregasModelTest {

    private static final int ID_ENVIO_SIN_INTENTOS  = 1;   // ENV-20260601-0001, tramo 4
    private static final int ID_TRAMO_ENVIO1         = 4;
    private static final int ID_ENVIO_DOS_FALLOS     = 3;   // ENV-20260525-0003, tramo 11
    private static final int ID_TRAMO_ENVIO3         = 11;
    private static final int ID_TRANSPORTISTA        = 7;   // Javier Martin

    private Database db;
    private EntregasModel model;

    @BeforeEach
    void setUp() {
        db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        model = new EntregasModel(db);
    }

    // -------------------------------------------------------------------------
    // Caso 1: Primer intento exitoso
    // -------------------------------------------------------------------------
    @Test
    void testCaso1_PrimerIntentoExitoso() {
        ResultadoEntregaDto res = model.registrarEntregaCompletada(
                ID_ENVIO_SIN_INTENTOS, ID_TRAMO_ENVIO1, ID_TRANSPORTISTA);

        assertTrue(res.isExito());
        assertFalse(res.isEsCuartoFallo());
        assertEquals(1, res.getNumeroIntento());

        assertEquals("ENTREGADO",  estadoEnvio(ID_ENVIO_SIN_INTENTOS));
        assertEquals("COMPLETADO", estadoTramo(ID_TRAMO_ENVIO1));
        assertEquals("COMPLETADA", estadoRuta(ID_ENVIO_SIN_INTENTOS));

        List<Object[]> intentos = intentosDeEnvio(ID_ENVIO_SIN_INTENTOS);
        assertEquals(1, intentos.size());
        assertEquals("COMPLETADO", intentos.get(0)[0].toString());
        assertEquals(1, ((Number) intentos.get(0)[1]).intValue());

        assertEquals(1, contarAvisos(ID_ENVIO_SIN_INTENTOS, "ENTREGADO"));
    }

    // -------------------------------------------------------------------------
    // Caso 2: Primer intento fallido (AUSENTE)
    // -------------------------------------------------------------------------
    @Test
    void testCaso2_PrimerIntentoFallidoAusente() {
        ResultadoEntregaDto res = model.registrarEntregaFallida(
                ID_ENVIO_SIN_INTENTOS, ID_TRAMO_ENVIO1, ID_TRANSPORTISTA, "AUSENTE", null);

        assertFalse(res.isExito());
        assertFalse(res.isEsCuartoFallo());
        assertEquals(1, res.getNumeroIntento());

        assertEquals("PENDIENTE_REENTREGA", estadoEnvio(ID_ENVIO_SIN_INTENTOS));
        assertEquals("FALLIDO", estadoTramo(ID_TRAMO_ENVIO1));

        List<Object[]> intentos = intentosDeEnvio(ID_ENVIO_SIN_INTENTOS);
        assertEquals(1, intentos.size());
        assertEquals("FALLIDO", intentos.get(0)[0].toString());
        assertEquals(1, ((Number) intentos.get(0)[1]).intValue());

        assertEquals(1, contarAvisos(ID_ENVIO_SIN_INTENTOS, "INTENTO_FALLIDO"));
    }

    // -------------------------------------------------------------------------
    // Caso 3: Tercer intento fallido (envio ya tenia 2 fallos en data.sql)
    // -------------------------------------------------------------------------
    @Test
    void testCaso3_TercerIntentoFallido() {
        assertEquals(2, model.contarIntentosPrevios(ID_ENVIO_DOS_FALLOS));

        ResultadoEntregaDto res = model.registrarEntregaFallida(
                ID_ENVIO_DOS_FALLOS, ID_TRAMO_ENVIO3, ID_TRANSPORTISTA, "AUSENTE", null);

        assertFalse(res.isExito());
        assertFalse(res.isEsCuartoFallo(), "El 3er fallo NO es cuarto fallo");
        assertEquals(3, res.getNumeroIntento());

        assertEquals("PENDIENTE_REENTREGA", estadoEnvio(ID_ENVIO_DOS_FALLOS));
        assertEquals(3, intentosDeEnvio(ID_ENVIO_DOS_FALLOS).size());
    }

    // -------------------------------------------------------------------------
    // Caso 4: Cuarto intento fallido -> DEPOSITADO_EN_PUNTO
    // -------------------------------------------------------------------------
    @Test
    void testCaso4_CuartoIntentoFallido_DepositadoEnPunto() {
        insertarIntentoFallidoPrevio(ID_ENVIO_DOS_FALLOS, ID_TRAMO_ENVIO3, 3);

        ResultadoEntregaDto res = model.registrarEntregaFallida(
                ID_ENVIO_DOS_FALLOS, ID_TRAMO_ENVIO3, ID_TRANSPORTISTA, "AUSENTE", null);

        assertFalse(res.isExito());
        assertTrue(res.isEsCuartoFallo(), "El 4o fallo debe marcar esCuartoFallo=true");
        assertEquals(4, res.getNumeroIntento());

        assertEquals("DEPOSITADO_EN_PUNTO", estadoEnvio(ID_ENVIO_DOS_FALLOS));
        assertEquals("FALLIDO", estadoTramo(ID_TRAMO_ENVIO3));
        assertNotNull(fechaRealTramo(ID_TRAMO_ENVIO3), "fechaReal debe estar rellena en el 4o fallo");
        assertEquals(4, intentosDeEnvio(ID_ENVIO_DOS_FALLOS).size());
        assertEquals(1, contarAvisos(ID_ENVIO_DOS_FALLOS, "DEPOSITADO"));
    }

    // -------------------------------------------------------------------------
    // Caso 5: Cuarto intento, pero exitoso -> ENTREGADO (no DEPOSITADO)
    // -------------------------------------------------------------------------
    @Test
    void testCaso5_CuartoIntentoExitoso() {
        insertarIntentoFallidoPrevio(ID_ENVIO_DOS_FALLOS, ID_TRAMO_ENVIO3, 3);

        ResultadoEntregaDto res = model.registrarEntregaCompletada(
                ID_ENVIO_DOS_FALLOS, ID_TRAMO_ENVIO3, ID_TRANSPORTISTA);

        assertTrue(res.isExito());
        assertFalse(res.isEsCuartoFallo());
        assertEquals(4, res.getNumeroIntento());

        assertEquals("ENTREGADO",  estadoEnvio(ID_ENVIO_DOS_FALLOS));
        assertEquals("COMPLETADO", estadoTramo(ID_TRAMO_ENVIO3));
        assertEquals("COMPLETADA", estadoRuta(ID_ENVIO_DOS_FALLOS));
    }

    // -------------------------------------------------------------------------
    // Caso 6: Motivo nulo -> ApplicationException
    // -------------------------------------------------------------------------
    @Test
    void testCaso6_MotivoNulo_LanzaExcepcion() {
        assertThrows(giis.demo.util.ApplicationException.class, () ->
                model.registrarEntregaFallida(
                        ID_ENVIO_SIN_INTENTOS, ID_TRAMO_ENVIO1, ID_TRANSPORTISTA, null, null));
    }

    // -------------------------------------------------------------------------
    // Caso 7: numeroIntento se calcula como COUNT(intentos previos) + 1
    // -------------------------------------------------------------------------
    @Test
    void testCaso7_NumeroIntentoCorrecto() {
        assertEquals(0, model.contarIntentosPrevios(ID_ENVIO_SIN_INTENTOS));
        assertEquals(2, model.contarIntentosPrevios(ID_ENVIO_DOS_FALLOS));

        model.registrarEntregaFallida(
                ID_ENVIO_SIN_INTENTOS, ID_TRAMO_ENVIO1, ID_TRANSPORTISTA, "AUSENTE", null);
        assertEquals(1, model.contarIntentosPrevios(ID_ENVIO_SIN_INTENTOS));

        model.registrarEntregaFallida(
                ID_ENVIO_DOS_FALLOS, ID_TRAMO_ENVIO3, ID_TRANSPORTISTA, "AUSENTE", null);
        assertEquals(3, model.contarIntentosPrevios(ID_ENVIO_DOS_FALLOS));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void insertarIntentoFallidoPrevio(int idEnvio, int idTramo, int numero) {
        db.executeUpdate(
                "INSERT INTO IntentoEntrega (idEnvio, idTramoEntrega, numeroIntento, idTransportista, "
                + "fechaIntento, resultado, motivoFallo) VALUES (?,?,?,?,'2026-06-10 10:00:00','FALLIDO','AUSENTE')",
                idEnvio, idTramo, numero, ID_TRANSPORTISTA);
    }

    private String estadoEnvio(int idEnvio) {
        return db.executeQueryArray("SELECT estado FROM Envio WHERE id = ?", idEnvio)
                .get(0)[0].toString();
    }

    private String estadoTramo(int idTramo) {
        return db.executeQueryArray("SELECT estado FROM TramoRuta WHERE id = ?", idTramo)
                .get(0)[0].toString();
    }

    private String estadoRuta(int idEnvio) {
        return db.executeQueryArray("SELECT r.estado FROM Ruta r WHERE r.idEnvio = ?", idEnvio)
                .get(0)[0].toString();
    }

    private String fechaRealTramo(int idTramo) {
        Object val = db.executeQueryArray("SELECT fechaReal FROM TramoRuta WHERE id = ?", idTramo)
                .get(0)[0];
        return val == null ? null : val.toString();
    }

    private List<Object[]> intentosDeEnvio(int idEnvio) {
        return db.executeQueryArray(
                "SELECT resultado, numeroIntento FROM IntentoEntrega WHERE idEnvio = ? ORDER BY numeroIntento",
                idEnvio);
    }

    private int contarAvisos(int idEnvio, String tipoEvento) {
        return ((Number) db.executeQueryArray(
                "SELECT COUNT(*) FROM AvisoCliente WHERE idEnvio = ? AND tipoEvento = ?",
                idEnvio, tipoEvento).get(0)[0]).intValue();
    }
}
