package giis.demo.tkrun.paqueteria.almacen;

import giis.demo.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la capa de negocio de AlmacenModel (HU-03).
 *
 * Datos de apoyo en data.sql relevantes:
 *   Envio 1 (id=1): BC-20260601-0001-X, pesoDeclarado=3.5 kg
 *     Tramo 2 (id=2): AL-NOR-01(6) -> AL-CTRO-01(5), PLANIFICADO  <- valido ENTRADA en almacen 5
 *     Tramo 3 (id=3): AL-CTRO-01(5) -> OF-MAD-01(2), PLANIFICADO
 *     Tramo 4 (id=4): OF-MAD-01(2) -> domicilio, ENTREGA, PLANIFICADO
 *   Envio 2 (id=2): BC-20260530-0002-Y, pesoUltimaVerificacion=1.8 kg
 *     Tramo 6 (id=6): AL-CTRO-01(5) -> AL-NOR-01(6), ALMACENADO   <- valido SALIDA en almacen 5
 *     Tramo 7 (id=7): AL-NOR-01(6) -> domicilio, ENTREGA           <- siguiente a tramo 6
 *   Operario: Luis Gomez (idUsuario=3), almacen AL-CTRO-01 (idPuntoLogistico=5)
 */
class AlmacenModelTest {

    // Datos fijos del data.sql usados en los tests
    private static final String CB_ENVIO1  = "BC-20260601-0001-X";
    private static final String CB_ENVIO2  = "BC-20260530-0002-Y";
    private static final int    ID_ALMACEN = 5;   // AL-CTRO-01
    private static final int    ID_OPERARIO = 3;  // Luis Gomez

    private Database db;
    private AlmacenModel model;

    @BeforeEach
    void setUp() {
        db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        model = new AlmacenModel(db);
    }

    // -------------------------------------------------------------------------
    // Caso 1: ENTRADA valida, inspeccion CORRECTO, peso sin discrepancia (<=5%)
    //   Resultado: tramo -> ALMACENADO, envio -> EN_TRANSITO, sin incidencia
    // -------------------------------------------------------------------------
    @Test
    void testCaso1_EntradaValida_Correcto_SinDiscrepancia() {
        PaqueteVerificacionDto paquete = model.buscarPaquete(CB_ENVIO1, "ENTRADA", ID_ALMACEN, ID_OPERARIO);

        OperacionAlmacenDto op = buildOp(paquete, "ENTRADA", "CORRECTO", 3.5);
        ResultadoOperacionDto resultado = model.registrarOperacion(op);

        assertTrue(resultado.isExito());
        assertFalse(resultado.isDiscrepancia());
        assertFalse(resultado.isIncidenciaGenerada());

        assertEquals("ALMACENADO", estadoTramo(paquete.getIdTramo()));
        assertEquals("EN_TRANSITO", estadoEnvio(paquete.getIdEnvio()));
        assertEquals(0, contarIncidencias(paquete.getIdEnvio()));
    }

    // -------------------------------------------------------------------------
    // Caso 2: ENTRADA con inspeccion DANO_GRAVE
    //   Resultado: se genera Incidencia tipo=DANO_GRAVE, estado=ABIERTA
    // -------------------------------------------------------------------------
    @Test
    void testCaso2_EntradaValida_DanoGrave_GeneraIncidencia() {
        PaqueteVerificacionDto paquete = model.buscarPaquete(CB_ENVIO1, "ENTRADA", ID_ALMACEN, ID_OPERARIO);

        OperacionAlmacenDto op = buildOp(paquete, "ENTRADA", "DANO_GRAVE", 3.5);
        ResultadoOperacionDto resultado = model.registrarOperacion(op);

        assertTrue(resultado.isIncidenciaGenerada());
        assertEquals("DANO_GRAVE", resultado.getTipoIncidencia());

        List<Object[]> incidencias = incidenciasDeEnvio(paquete.getIdEnvio());
        assertEquals(1, incidencias.size());
        assertEquals("DANO_GRAVE", incidencias.get(0)[0].toString());
        assertEquals("ABIERTA",    incidencias.get(0)[1].toString());
    }

    // -------------------------------------------------------------------------
    // Caso 3: Discrepancia exactamente al 5% -> NO se considera discrepancia
    //   Regla: diferencia > 0.05 (estricto), exactamente 5% NO supera el umbral
    // -------------------------------------------------------------------------
    @Test
    void testCaso3_DiscrepanciaExacta5PorCiento_NoGeneraIncidencia() {
        PaqueteVerificacionDto paquete = model.buscarPaquete(CB_ENVIO1, "ENTRADA", ID_ALMACEN, ID_OPERARIO);
        double pesoAlLimite = paquete.getPesoRefKg() * 1.05; // exactamente 5%

        OperacionAlmacenDto op = buildOp(paquete, "ENTRADA", "CORRECTO", pesoAlLimite);
        ResultadoOperacionDto resultado = model.registrarOperacion(op);

        assertFalse(resultado.isDiscrepancia(), "5% exacto no debe superar el umbral estricto >5%");
        assertFalse(resultado.isIncidenciaGenerada());
        assertEquals(0, contarIncidencias(paquete.getIdEnvio()));
    }

    // -------------------------------------------------------------------------
    // Caso 4: Discrepancia al 5.01% -> SI se genera incidencia DISCREPANCIA_PESO
    // -------------------------------------------------------------------------
    @Test
    void testCaso4_Discrepancia5punto01PorCiento_GeneraIncidencia() {
        PaqueteVerificacionDto paquete = model.buscarPaquete(CB_ENVIO1, "ENTRADA", ID_ALMACEN, ID_OPERARIO);
        double pesoSobreLimite = paquete.getPesoRefKg() * 1.0501; // 5.01%

        OperacionAlmacenDto op = buildOp(paquete, "ENTRADA", "CORRECTO", pesoSobreLimite);
        ResultadoOperacionDto resultado = model.registrarOperacion(op);

        assertTrue(resultado.isDiscrepancia());
        assertTrue(resultado.isIncidenciaGenerada());
        assertEquals("DISCREPANCIA_PESO", resultado.getTipoIncidencia());
        assertEquals(1, contarIncidencias(paquete.getIdEnvio()));
    }

    // -------------------------------------------------------------------------
    // Caso 5: DANO_GRAVE + discrepancia de peso
    //   Resultado: UNA sola incidencia tipo=DANO_GRAVE (prioridad sobre DISCREPANCIA)
    // -------------------------------------------------------------------------
    @Test
    void testCaso5_DanoGraveYDiscrepancia_UnaIncidenciaDanoGrave() {
        PaqueteVerificacionDto paquete = model.buscarPaquete(CB_ENVIO1, "ENTRADA", ID_ALMACEN, ID_OPERARIO);
        double pesoConDiscrepancia = paquete.getPesoRefKg() * 1.10; // 10%

        OperacionAlmacenDto op = buildOp(paquete, "ENTRADA", "DANO_GRAVE", pesoConDiscrepancia);
        ResultadoOperacionDto resultado = model.registrarOperacion(op);

        assertTrue(resultado.isDiscrepancia());
        assertTrue(resultado.isIncidenciaGenerada());
        assertEquals("DANO_GRAVE", resultado.getTipoIncidencia(), "DANO_GRAVE tiene prioridad");

        List<Object[]> incidencias = incidenciasDeEnvio(paquete.getIdEnvio());
        assertEquals(1, incidencias.size(), "Solo debe generarse una incidencia");
        assertEquals("DANO_GRAVE", incidencias.get(0)[0].toString());
    }

    // -------------------------------------------------------------------------
    // Caso 6: SALIDA del ultimo almacen antes del tramo de ENTREGA
    //   Envio 2 tramo 6: AL-CTRO-01 -> AL-NOR-01 (ALMACENADO)
    //   Siguiente tramo: tipo=ENTREGA -> envio debe quedar EN_REPARTO
    // -------------------------------------------------------------------------
    @Test
    void testCaso6_SalidaUltimoAlmacenAntesEntrega_EnvioEnReparto() {
        PaqueteVerificacionDto paquete = model.buscarPaquete(CB_ENVIO2, "SALIDA", ID_ALMACEN, ID_OPERARIO);

        OperacionAlmacenDto op = buildOp(paquete, "SALIDA", "CORRECTO", 1.8);
        ResultadoOperacionDto resultado = model.registrarOperacion(op);

        assertTrue(resultado.isExito());
        assertFalse(resultado.isDiscrepancia());
        assertFalse(resultado.isIncidenciaGenerada());

        assertEquals("EN_TRANSITO", estadoTramo(paquete.getIdTramo()),
                "El tramo de salida debe pasar a EN_TRANSITO");
        assertEquals("EN_REPARTO", estadoEnvio(paquete.getIdEnvio()),
                "El envio debe pasar a EN_REPARTO porque el siguiente tramo es ENTREGA");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private OperacionAlmacenDto buildOp(PaqueteVerificacionDto paquete,
                                         String tipoOp, String inspeccion, double peso) {
        OperacionAlmacenDto op = new OperacionAlmacenDto();
        op.setIdPaquete(paquete.getIdPaquete());
        op.setIdEnvio(paquete.getIdEnvio());
        op.setIdTramo(paquete.getIdTramo());
        op.setTipoOperacion(tipoOp);
        op.setInspeccionVisual(inspeccion);
        op.setPesoMedidoKg(peso);
        op.setPesoRefKg(paquete.getPesoRefKg());
        op.setIdAlmacen(ID_ALMACEN);
        op.setIdOperario(ID_OPERARIO);
        return op;
    }

    private String estadoTramo(int idTramo) {
        return db.executeQueryArray("SELECT estado FROM TramoRuta WHERE id = ?", idTramo)
                .get(0)[0].toString();
    }

    private String estadoEnvio(int idEnvio) {
        return db.executeQueryArray("SELECT estado FROM Envio WHERE id = ?", idEnvio)
                .get(0)[0].toString();
    }

    private int contarIncidencias(int idEnvio) {
        return ((Number) db.executeQueryArray(
                "SELECT COUNT(*) FROM Incidencia WHERE idEnvio = ?", idEnvio)
                .get(0)[0]).intValue();
    }

    private List<Object[]> incidenciasDeEnvio(int idEnvio) {
        return db.executeQueryArray(
                "SELECT tipo, estado FROM Incidencia WHERE idEnvio = ? ORDER BY id",
                idEnvio);
    }
}
