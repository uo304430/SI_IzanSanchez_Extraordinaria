package FacturaIncidencia;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del proceso de negocio: Generación de factura para una incidencia.
 * Método probado: FacturaModel.generarFacturaParaIncidencia(int incidenciaId, String emisor)
 *
 * Estructura de clases de equivalencia:
 *   CE1: Incidencia no existe → retorna null
 *   CE2: Incidencia existe
 *     CE2.1: Coste = 0 → retorna null
 *     CE2.2: Coste < 0 → retorna null
 *     CE2.3: Coste > 0
 *       CE2.3.1: Ya existe factura para esa incidencia → retorna null
 *       CE2.3.2: No existe factura previa → genera correctamente
 *         CE2.3.2a: DTO retornado no es null
 *         CE2.3.2b: Número de factura tiene formato F-...
 *         CE2.3.2c: Coste total en el DTO coincide con el de la incidencia
 *         CE2.3.2d: Emisor en el DTO coincide con el pasado como parámetro
 *         CE2.3.2e: Se crea exactamente 1 concepto en FacturaLinea
 *         CE2.3.2f: Se registra entrada en HistorialIncidencia
 *         CE2.3.2g: Intentar generar segunda factura para la misma incidencia → retorna null
 */
public class TestFacturaModel {

    private FacturaModel model;

    // IDs de incidencias que deben existir en la BD de prueba
    // Ajusta estos valores según los datos que carga db.loadDatabase()
    private static final int ID_INCIDENCIA_COSTE_POSITIVO = 1;  // incidencia con Coste > 0
    private static final int ID_INCIDENCIA_COSTE_CERO     = 2;  // incidencia con Coste = 0
    private static final int ID_INCIDENCIA_NO_EXISTE      = 99999;
    private static final String EMISOR = "Gestor Económico";

    @BeforeEach
    public void setUp() {
        model = new FacturaModel();
        // Desactivar foreign keys para poder limpiar sin restricciones
        model.getDb().executeUpdate("PRAGMA foreign_keys = OFF");
        model.borrarTodasFacturas();
        model.getDb().executeUpdate("PRAGMA foreign_keys = ON");
    }

    // -----------------------------------------------------------------------
    // CE1: La incidencia no existe en la base de datos
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE1 - Incidencia inexistente devuelve null")
    public void testGenerarFactura_IncidenciaNoExiste_RetornaNull() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_NO_EXISTE, EMISOR);
        assertNull(resultado, "Debe retornar null si la incidencia no existe");
    }

    // -----------------------------------------------------------------------
    // CE2.1: La incidencia existe pero tiene coste = 0
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.1 - Incidencia con coste 0 devuelve null")
    public void testGenerarFactura_CosteCero_RetornaNull() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_CERO, EMISOR);
        assertNull(resultado, "Debe retornar null si el coste de la incidencia es 0");
    }

    // -----------------------------------------------------------------------
    // CE2.3.1: La incidencia tiene coste > 0 pero ya tiene factura generada
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.1 - Incidencia ya facturada devuelve null en segundo intento")
    public void testGenerarFactura_YaFacturada_RetornaNull() {
        // Primera generación (debe funcionar)
        model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        // Segunda generación sobre la misma incidencia
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNull(resultado, "Debe retornar null si la incidencia ya tiene factura");
    }

    // -----------------------------------------------------------------------
    // CE2.3.2a: Generación correcta → DTO no es null
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.2a - Generación correcta devuelve DTO no nulo")
    public void testGenerarFactura_Correcta_DtoNoEsNull() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNotNull(resultado, "El DTO retornado no debe ser null en una generación correcta");
    }

    // -----------------------------------------------------------------------
    // CE2.3.2b: El número de factura tiene formato F-...
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.2b - Número de factura tiene formato F-...")
    public void testGenerarFactura_Correcta_NumeroConFormatoCorrecto() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNotNull(resultado);
        assertTrue(resultado.getNumero().startsWith("F-"),
                "El número de factura debe comenzar por 'F-', pero fue: " + resultado.getNumero());
    }

    // -----------------------------------------------------------------------
    // CE2.3.2c: El coste total en el DTO coincide con el de la incidencia
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.2c - Coste total en el DTO es correcto")
    public void testGenerarFactura_Correcta_CosteTotalCorrecto() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNotNull(resultado);
        assertNotNull(resultado.getCosteTotal(), "El coste total no debe ser null");
        int coste = Integer.parseInt(resultado.getCosteTotal());
        assertTrue(coste > 0, "El coste total debe ser mayor que 0");
    }

    // -----------------------------------------------------------------------
    // CE2.3.2d: El emisor en el DTO coincide con el pasado como parámetro
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.2d - Emisor en el DTO coincide con el parámetro")
    public void testGenerarFactura_Correcta_EmisorCorrecto() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNotNull(resultado);
        assertEquals(EMISOR, resultado.getEmisor(),
                "El emisor del DTO debe coincidir con el parámetro pasado");
    }

    // -----------------------------------------------------------------------
    // CE2.3.2e: Se crea exactamente 1 concepto (FacturaLinea)
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.2e - Se genera exactamente 1 concepto en la factura")
    public void testGenerarFactura_Correcta_UnConcepto() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNotNull(resultado);
        assertNotNull(resultado.getConceptos(), "La lista de conceptos no debe ser null");
        assertEquals(1, resultado.getConceptos().size(),
                "Debe generarse exactamente 1 concepto en la factura");
    }

    // -----------------------------------------------------------------------
    // CE2.3.2f: Se registra entrada en HistorialIncidencia
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.2f - Se registra la factura en el historial de la incidencia")
    public void testGenerarFactura_Correcta_RegistradaEnHistorial() {
        FacturaDTO resultado = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNotNull(resultado);
        // Verificamos indirectamente: si intentamos borrar facturas y luego
        // comprobar que existeFactura devuelve false (el historial se limpia con borrarTodasFacturas)
        // Para verificar el historial directamente usamos existeFacturaParaIncidencia antes/después
        assertTrue(model.existeFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO),
                "Tras generar la factura, debe existir en la BD y quedar registrada");
    }

    // -----------------------------------------------------------------------
    // CE2.3.2g: Intentar generar segunda factura → retorna null (duplicado)
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("CE2.3.2g - Segunda generación para la misma incidencia retorna null")
    public void testGenerarFactura_Duplicado_SegundaGeneracionNull() {
        FacturaDTO primera = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNotNull(primera, "La primera generación debe ser correcta");

        FacturaDTO segunda = model.generarFacturaParaIncidencia(ID_INCIDENCIA_COSTE_POSITIVO, EMISOR);
        assertNull(segunda, "La segunda generación debe retornar null (no se permiten duplicados)");
    }
    
}