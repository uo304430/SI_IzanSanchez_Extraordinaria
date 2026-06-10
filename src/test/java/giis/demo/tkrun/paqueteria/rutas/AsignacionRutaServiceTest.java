package giis.demo.tkrun.paqueteria.rutas;

import giis.demo.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la capa de negocio de AsignacionRutaService (HU-02).
 *
 * Datos de apoyo en data.sql relevantes:
 *   PuntoLogistico:
 *     id=1  OF-GIJ-01  OFICINA  zona=1 (NORTE)
 *     id=2  OF-MAD-01  OFICINA  zona=2 (CENTRO)
 *     id=5  AL-CTRO-01 ALMACEN  zona=2  <- hub (primer ALMACEN por id)
 *     id=6  AL-NOR-01  ALMACEN  zona=1
 *   Vehiculos activos:
 *     id=1  1234-AAA  FURGONETA   1500kg  8m3  base=1
 *     id=2  5678-BBB  CAMION_RIG  8000kg 30m3  base=5
 *     id=5  7890-EEE  FURGONETA   1500kg  8m3  base=2
 *     id=3  9012-CCC  CAMION_RIG  8000kg 30m3  base=6
 */
class AsignacionRutaServiceTest {

    private Database db;
    private AsignacionRutaService service;

    @BeforeEach
    void setUp() {
        db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        service = new AsignacionRutaService();
    }

    // -------------------------------------------------------------------------
    // Caso 1: misma zona (NORTE→NORTE), recogida OFICINA, entrega DOMICILIO
    //         Tramos esperados: INTERMEDIO(1→6) + ENTREGA(6→null) = 2 tramos
    // -------------------------------------------------------------------------
    @Test
    void testCaso1_MismaZona_OficinaRecogida_DomicilioEntrega() throws Exception {
        int idEnvio = insertarEnvio(1, 6, 1, 1, "OFICINA", "DOMICILIO", 1.0, 10, 10, 10);

        Connection conn = db.getConnection();
        conn.setAutoCommit(false);
        ResultadoAsignacion resultado = service.asignarRuta(idEnvio, conn);
        conn.commit();
        conn.close();

        assertTrue(resultado.isExito(), "La asignacion debe tener exito");
        assertEquals(2, resultado.getNumTramos(), "Deben crearse 2 tramos (INTERMEDIO + ENTREGA)");

        List<Object[]> tramos = tramosDeEnvio(idEnvio);
        assertEquals(2, tramos.size());
        assertEquals("EN_RUTA", estadoEnvio(idEnvio));
    }

    // -------------------------------------------------------------------------
    // Caso 2: zonas distintas (NORTE→CENTRO), recogida DOMICILIO, entrega OFICINA
    //         Tramos esperados: RECOGIDA + INTERMEDIO(1→hub) + INTERMEDIO(hub→2) = 3 tramos
    // -------------------------------------------------------------------------
    @Test
    void testCaso2_ZonasDistintas_DomicilioRecogida_OficinaEntrega() throws Exception {
        int idEnvio = insertarEnvio(1, 2, 1, 2, "DOMICILIO", "OFICINA", 1.0, 10, 10, 10);

        Connection conn = db.getConnection();
        conn.setAutoCommit(false);
        ResultadoAsignacion resultado = service.asignarRuta(idEnvio, conn);
        conn.commit();
        conn.close();

        assertTrue(resultado.isExito());
        assertEquals(3, resultado.getNumTramos(), "Deben crearse 3 tramos (RECOGIDA + 2 INTERMEDIOS)");

        List<Object[]> tramos = tramosDeEnvio(idEnvio);
        assertEquals(3, tramos.size());
        assertEquals("RECOGIDA", tramos.get(0)[0].toString(), "El primer tramo debe ser de tipo RECOGIDA");
        assertEquals("EN_RUTA", estadoEnvio(idEnvio));
    }

    // -------------------------------------------------------------------------
    // Caso 3: zonas distintas (NORTE→CENTRO), recogida OFICINA, entrega DOMICILIO
    //         Tramos esperados: INTERMEDIO(1→hub) + INTERMEDIO(hub→2) + ENTREGA(2→null) = 3 tramos
    // -------------------------------------------------------------------------
    @Test
    void testCaso3_ZonasDistintas_OficinaRecogida_DomicilioEntrega() throws Exception {
        int idEnvio = insertarEnvio(1, 2, 1, 2, "OFICINA", "DOMICILIO", 1.0, 10, 10, 10);

        Connection conn = db.getConnection();
        conn.setAutoCommit(false);
        ResultadoAsignacion resultado = service.asignarRuta(idEnvio, conn);
        conn.commit();
        conn.close();

        assertTrue(resultado.isExito());
        assertEquals(3, resultado.getNumTramos(), "Deben crearse 3 tramos (2 INTERMEDIOS + ENTREGA)");

        List<Object[]> tramos = tramosDeEnvio(idEnvio);
        assertEquals(3, tramos.size());
        assertEquals("ENTREGA", tramos.get(2)[0].toString(), "El ultimo tramo debe ser de tipo ENTREGA");
        assertEquals("EN_RUTA", estadoEnvio(idEnvio));
    }

    // -------------------------------------------------------------------------
    // Caso 4: paquete que excede la capacidad de todos los vehiculos
    //         -> exito=false, envio queda en PENDIENTE_ASIGNACION
    // -------------------------------------------------------------------------
    @Test
    void testCaso4_SinVehiculoCompatible_QuedaPendienteAsignacion() throws Exception {
        int idEnvio = insertarEnvio(1, 6, 1, 1, "OFICINA", "DOMICILIO", 99999.0, 10, 10, 10);

        Connection conn = db.getConnection();
        conn.setAutoCommit(false);
        ResultadoAsignacion resultado = service.asignarRuta(idEnvio, conn);
        conn.commit();
        conn.close();

        assertFalse(resultado.isExito(), "La asignacion debe fallar por falta de vehiculo");
        assertNotNull(resultado.getMotivoFallo(), "Debe indicarse el motivo del fallo");
        assertEquals("PENDIENTE_ASIGNACION", estadoEnvio(idEnvio),
                "El envio debe quedar en estado PENDIENTE_ASIGNACION");

        List<Object[]> tramos = tramosDeEnvio(idEnvio);
        assertEquals(0, tramos.size(), "No deben haberse creado tramos");
    }

    // -------------------------------------------------------------------------
    // Caso 5: los ordenSecuencia de los tramos son consecutivos (1, 2, 3)
    // -------------------------------------------------------------------------
    @Test
    void testCaso5_OrdenSecuenciaConsecutivo() throws Exception {
        int idEnvio = insertarEnvio(1, 2, 1, 2, "OFICINA", "DOMICILIO", 1.0, 10, 10, 10);

        Connection conn = db.getConnection();
        conn.setAutoCommit(false);
        service.asignarRuta(idEnvio, conn);
        conn.commit();
        conn.close();

        List<Object[]> secuencias = db.executeQueryArray(
                "SELECT ordenSecuencia FROM TramoRuta "
                + "WHERE idRuta = (SELECT id FROM Ruta WHERE idEnvio = ?) "
                + "ORDER BY ordenSecuencia", idEnvio);

        assertEquals(3, secuencias.size());
        assertEquals(1, ((Number) secuencias.get(0)[0]).intValue());
        assertEquals(2, ((Number) secuencias.get(1)[0]).intValue());
        assertEquals(3, ((Number) secuencias.get(2)[0]).intValue());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Inserta un Envio + Paquete de prueba y retorna el id del Envio generado.
     * Estado inicial siempre REGISTRADO. codigo unico por ejecucion via timestamp.
     */
    private int insertarEnvio(int idPuntoOrigen, int idPuntoDestino,
                              int idZonaOrigen,  int idZonaDestino,
                              String modalRecogida, String modalEntrega,
                              double pesoKg, int largo, int ancho, int alto) {
        String codigo = "TEST-" + System.nanoTime();
        db.executeUpdate(
                "INSERT INTO Envio (codigo, canalOrigen, idTipoServicio, "
                + "remitenteNombre, remitenteDni, remitenteTelefono, "
                + "remitenteDireccion, remitenteCiudad, remitenteCodigoPostal, idZonaOrigen, "
                + "destinatarioNombre, destinatarioTelefono, "
                + "destinatarioDireccion, destinatarioCiudad, destinatarioCodigoPostal, idZonaDestino, "
                + "modalidadRecogida, modalidadEntrega, idPuntoOrigen, idPuntoDestino, "
                + "estado, costeCalculado, valorDeclarado, formaPago, pagado) "
                + "VALUES (?, 'OFICINA', 1, "
                + "'Rem Test', '12345678A', '600000000', "
                + "'Calle Test 1', 'Ciudad Test', '00000', ?, "
                + "'Dest Test', '600000001', "
                + "'Calle Dest 1', 'Ciudad Dest', '11111', ?, "
                + "?, ?, ?, ?, "
                + "'REGISTRADO', 10.00, 0, 'EFECTIVO', 1)",
                codigo, idZonaOrigen, idZonaDestino,
                modalRecogida, modalEntrega, idPuntoOrigen, idPuntoDestino);

        int idEnvio = ((Number) db.executeQueryArray("SELECT MAX(id) FROM Envio").get(0)[0]).intValue();

        db.executeUpdate(
                "INSERT INTO Paquete (idEnvio, codigoBarras, descripcion, "
                + "pesoDeclaradoKg, largoCm, anchoCm, altoCm, estadoFisico) "
                + "VALUES (?, ?, 'Test HU-02', ?, ?, ?, ?, 'CORRECTO')",
                idEnvio, "BC-TEST-" + idEnvio, pesoKg, largo, ancho, alto);

        return idEnvio;
    }

    private String estadoEnvio(int idEnvio) {
        return db.executeQueryArray("SELECT estado FROM Envio WHERE id = ?", idEnvio)
                .get(0)[0].toString();
    }

    private List<Object[]> tramosDeEnvio(int idEnvio) {
        return db.executeQueryArray(
                "SELECT tipo, ordenSecuencia FROM TramoRuta "
                + "WHERE idRuta = (SELECT id FROM Ruta WHERE idEnvio = ?) "
                + "ORDER BY ordenSecuencia", idEnvio);
    }
}
