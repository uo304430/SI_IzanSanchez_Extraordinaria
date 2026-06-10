package giis.demo.tkrun.paqueteria.envios;

import java.math.BigDecimal;

public class EnvioCreacionDto {
    // Remitente
    private String remitenteNombre;
    private String remitenteDni;
    private String remitenteTelefono;
    private String remitenteDireccion;
    private String remitenteCiudad;
    private String remitenteCodigoPostal;
    private int idZonaOrigen;
    // Destinatario
    private String destinatarioNombre;
    private String destinatarioTelefono;
    private String destinatarioDireccion;
    private String destinatarioCiudad;
    private String destinatarioCodigoPostal;
    private int idZonaDestino;
    // Paquete
    private String descripcionPaquete;
    private BigDecimal pesoKg;
    private int largoCm;
    private int anchoCm;
    private int altoCm;
    private BigDecimal valorDeclarado;
    // Opciones
    private int idTipoServicio;
    private String modalidadRecogida;
    private String modalidadEntrega;
    private String formaPago;
    private int idPuntoDestino;
    // Coste calculado antes de confirmar
    private BigDecimal costeCalculado;

    public String getRemitenteNombre() { return remitenteNombre; }
    public void setRemitenteNombre(String v) { this.remitenteNombre = v; }
    public String getRemitenteDni() { return remitenteDni; }
    public void setRemitenteDni(String v) { this.remitenteDni = v; }
    public String getRemitenteTelefono() { return remitenteTelefono; }
    public void setRemitenteTelefono(String v) { this.remitenteTelefono = v; }
    public String getRemitenteDireccion() { return remitenteDireccion; }
    public void setRemitenteDireccion(String v) { this.remitenteDireccion = v; }
    public String getRemitenteCiudad() { return remitenteCiudad; }
    public void setRemitenteCiudad(String v) { this.remitenteCiudad = v; }
    public String getRemitenteCodigoPostal() { return remitenteCodigoPostal; }
    public void setRemitenteCodigoPostal(String v) { this.remitenteCodigoPostal = v; }
    public int getIdZonaOrigen() { return idZonaOrigen; }
    public void setIdZonaOrigen(int v) { this.idZonaOrigen = v; }

    public String getDestinatarioNombre() { return destinatarioNombre; }
    public void setDestinatarioNombre(String v) { this.destinatarioNombre = v; }
    public String getDestinatarioTelefono() { return destinatarioTelefono; }
    public void setDestinatarioTelefono(String v) { this.destinatarioTelefono = v; }
    public String getDestinatarioDireccion() { return destinatarioDireccion; }
    public void setDestinatarioDireccion(String v) { this.destinatarioDireccion = v; }
    public String getDestinatarioCiudad() { return destinatarioCiudad; }
    public void setDestinatarioCiudad(String v) { this.destinatarioCiudad = v; }
    public String getDestinatarioCodigoPostal() { return destinatarioCodigoPostal; }
    public void setDestinatarioCodigoPostal(String v) { this.destinatarioCodigoPostal = v; }
    public int getIdZonaDestino() { return idZonaDestino; }
    public void setIdZonaDestino(int v) { this.idZonaDestino = v; }

    public String getDescripcionPaquete() { return descripcionPaquete; }
    public void setDescripcionPaquete(String v) { this.descripcionPaquete = v; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal v) { this.pesoKg = v; }
    public int getLargoCm() { return largoCm; }
    public void setLargoCm(int v) { this.largoCm = v; }
    public int getAnchoCm() { return anchoCm; }
    public void setAnchoCm(int v) { this.anchoCm = v; }
    public int getAltoCm() { return altoCm; }
    public void setAltoCm(int v) { this.altoCm = v; }
    public BigDecimal getValorDeclarado() { return valorDeclarado; }
    public void setValorDeclarado(BigDecimal v) { this.valorDeclarado = v; }

    public int getIdTipoServicio() { return idTipoServicio; }
    public void setIdTipoServicio(int v) { this.idTipoServicio = v; }
    public String getModalidadRecogida() { return modalidadRecogida; }
    public void setModalidadRecogida(String v) { this.modalidadRecogida = v; }
    public String getModalidadEntrega() { return modalidadEntrega; }
    public void setModalidadEntrega(String v) { this.modalidadEntrega = v; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String v) { this.formaPago = v; }
    public int getIdPuntoDestino() { return idPuntoDestino; }
    public void setIdPuntoDestino(int v) { this.idPuntoDestino = v; }

    public BigDecimal getCosteCalculado() { return costeCalculado; }
    public void setCosteCalculado(BigDecimal v) { this.costeCalculado = v; }
}
