package giis.demo.tkrun.paqueteria.rutas;

/** DTO de lectura para los datos del Paquete necesarios en la asignacion de ruta. */
public class PaqueteDatos {
    private double pesoDeclaradoKg;
    private int largoCm;
    private int anchoCm;
    private int altoCm;

    public double getPesoDeclaradoKg()       { return pesoDeclaradoKg; }
    public void setPesoDeclaradoKg(double v) { this.pesoDeclaradoKg = v; }
    public int getLargoCm()                  { return largoCm; }
    public void setLargoCm(int v)            { this.largoCm = v; }
    public int getAnchoCm()                  { return anchoCm; }
    public void setAnchoCm(int v)            { this.anchoCm = v; }
    public int getAltoCm()                   { return altoCm; }
    public void setAltoCm(int v)             { this.altoCm = v; }
}
