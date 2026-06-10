package giis.demo.tkrun.paqueteria.util;

/** Item generico para poblar JComboBox con un id numerico y un texto de display. */
public class ComboItem {
    private final int id;
    private final String texto;

    public ComboItem(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() { return id; }
    public String getTexto() { return texto; }

    @Override
    public String toString() { return texto; }
}
