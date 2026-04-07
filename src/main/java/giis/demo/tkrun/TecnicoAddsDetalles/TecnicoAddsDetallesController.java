package giis.demo.tkrun.TecnicoAddsDetalles;
import java.util.List;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.HistorialDTO;
import java.util.List;

public class TecnicoAddsDetallesController {
    private TecnicoAddsDetallesModel model;

    public TecnicoAddsDetallesController() {
        model = new TecnicoAddsDetallesModel();
    }

    // Constructor compatible con el resto de controladores de la aplicación
    public TecnicoAddsDetallesController(TecnicoAddsDetallesModel model, TecnicoAddsDetallesView view, String identificacion) {
        this.model = model == null ? new TecnicoAddsDetallesModel() : model;
        // El view puede construirse y mostrarse por quien llame; no es necesario almacenarlo aquí
    }

    public List<IncidenciaDTO> obtenerIncidenciasEnCurso() {
        return model.obtenerIncidenciasEnCurso();
    }

    public void añadirComentario(int incidenciaId, String comentario) {
        model.añadirComentario(incidenciaId, comentario);
    }

    public void añadirComentario(int incidenciaId, String comentario, String fechaComentario) {
        model.añadirComentario(incidenciaId, comentario, fechaComentario);
    }

    public List<HistorialDTO> obtenerHistorial(int incidenciaId) {
        return model.obtenerHistorial(incidenciaId);
    }
}