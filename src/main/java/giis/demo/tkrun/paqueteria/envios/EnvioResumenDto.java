package giis.demo.tkrun.paqueteria.envios;

import java.math.BigDecimal;

public class EnvioResumenDto {
    private final String codigoEnvio;
    private final String codigoBarras;
    private final BigDecimal coste;
    private boolean avisoSinRuta = false;

    public EnvioResumenDto(String codigoEnvio, String codigoBarras, BigDecimal coste) {
        this.codigoEnvio  = codigoEnvio;
        this.codigoBarras = codigoBarras;
        this.coste        = coste;
    }

    public String getCodigoEnvio()         { return codigoEnvio; }
    public String getCodigoBarras()        { return codigoBarras; }
    public BigDecimal getCoste()           { return coste; }
    public boolean isAvisoSinRuta()        { return avisoSinRuta; }
    public void setAvisoSinRuta(boolean v) { this.avisoSinRuta = v; }
}
