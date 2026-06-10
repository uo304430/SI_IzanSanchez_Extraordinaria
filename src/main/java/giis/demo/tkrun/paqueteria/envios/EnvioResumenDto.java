package giis.demo.tkrun.paqueteria.envios;

import java.math.BigDecimal;

public class EnvioResumenDto {
    private final String codigoEnvio;
    private final String codigoBarras;
    private final BigDecimal coste;

    public EnvioResumenDto(String codigoEnvio, String codigoBarras, BigDecimal coste) {
        this.codigoEnvio = codigoEnvio;
        this.codigoBarras = codigoBarras;
        this.coste = coste;
    }

    public String getCodigoEnvio()  { return codigoEnvio; }
    public String getCodigoBarras() { return codigoBarras; }
    public BigDecimal getCoste()    { return coste; }
}
