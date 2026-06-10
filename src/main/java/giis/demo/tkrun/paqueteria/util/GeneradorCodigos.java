package giis.demo.tkrun.paqueteria.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Generacion de codigos de envio y de barras dentro de la misma transaccion. */
public class GeneradorCodigos {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private GeneradorCodigos() {}

    /**
     * Genera el codigo de envio ENV-YYYYMMDD-NNNN consultando el MAX existente para ese dia.
     * Debe invocarse con la conexion de la transaccion activa.
     */
    public static String generarCodigoEnvio(Connection conn, LocalDate fecha) throws SQLException {
        String datePart = fecha.format(FMT);
        String prefijo = "ENV-" + datePart + "-%";
        QueryRunner runner = new QueryRunner();
        List<Object[]> res = runner.query(conn,
                "SELECT COUNT(*) FROM Envio WHERE codigo LIKE ?",
                new ArrayListHandler(), prefijo);
        int seq = ((Number) res.get(0)[0]).intValue() + 1;
        return String.format("ENV-%s-%04d", datePart, seq);
    }

    /**
     * Genera el codigo de barras BC-YYYYMMDD-NNNN-X a partir del codigo de envio,
     * sustituyendo el prefijo ENV por BC y anadiendo una letra aleatoria.
     */
    public static String generarCodigoBarras(String codigoEnvio) {
        String middle = codigoEnvio.substring(4); // YYYYMMDD-NNNN
        char letra = (char) ('A' + (int) (Math.random() * 26));
        return "BC-" + middle + "-" + letra;
    }
}
