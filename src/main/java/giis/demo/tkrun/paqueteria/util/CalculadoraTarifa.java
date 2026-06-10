package giis.demo.tkrun.paqueteria.util;

import giis.demo.util.Database;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Consulta la tarifa vigente para un envio segun tipo de servicio, peso y zonas. */
public class CalculadoraTarifa {

    private final Database db;

    public CalculadoraTarifa(Database db) {
        this.db = db;
    }

    /**
     * Devuelve el precio de la tarifa vigente o empty si no existe ninguna que cubra
     * la combinacion indicada.
     */
    public Optional<BigDecimal> buscarTarifaVigente(int idTipoServicio, BigDecimal pesoKg,
                                                     int idZonaOrigen, int idZonaDestino) {
        String sql = "SELECT precio FROM Tarifa "
                + "WHERE idTipoServicio = ? "
                + "  AND pesoDesdeKg <= ? AND pesoHastaKg >= ? "
                + "  AND idZonaOrigen = ? AND idZonaDestino = ? "
                + "  AND activa = 1 "
                + "  AND fechaInicioVigencia <= date('now') "
                + "  AND (fechaFinVigencia IS NULL OR fechaFinVigencia >= date('now'))";
        List<Object[]> result = db.executeQueryArray(sql,
                idTipoServicio, pesoKg, pesoKg, idZonaOrigen, idZonaDestino);
        if (result.isEmpty())
            return Optional.empty();
        return Optional.of(new BigDecimal(result.get(0)[0].toString()));
    }
}
