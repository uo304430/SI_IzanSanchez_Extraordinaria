package giis.demo.tkrun.GestorEconomicoDefinePresupuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import giis.demo.tkrun.CiudadanoRegistraIncidencias.IncidenciasModel;
import giis.demo.tkrun.Entities.TipoIncidenciaEntity;
import giis.demo.tkrun.Entities.UsuarioEntity;
import giis.demo.util.ApplicationException;
import giis.demo.util.Database;

public class DefinePresupuestoModel {
    private static final int ROL_GESTOR_ECONOMICO = 4;
    private Database db = new Database();

    public List<TipoIncidenciaEntity> getTiposIncidencia() {
        List<TipoIncidenciaEntity> tipos = db.executeQueryPojo(TipoIncidenciaEntity.class,
                "SELECT id, nombre FROM Tipos ORDER BY nombre");
        return tipos == null ? new ArrayList<>() : tipos;
    }

    public List<Object[]> getPresupuestos() {
        String sql = "SELECT p.id, t.nombre, p.presupuesto, p.consumido, p.fecha_inicio, p.fecha_fin "
                + "FROM Presupuestos p JOIN Tipos t ON p.tipo=t.id "
                + "ORDER BY t.nombre, p.fecha_inicio DESC";
        List<Object[]> rows = db.executeQueryArray(sql);
        return rows == null ? new ArrayList<>() : rows;
    }

    public void definirPresupuesto(String identificacion, int tipoId, String importeTexto, boolean anual,
            String anioTexto, String fechaInicioTexto, String fechaFinTexto) {
        validarGestor(identificacion);
        if (tipoId <= 0) {
            throw new ApplicationException("Debe seleccionar un tipo de incidencia.");
        }

        BigDecimal importe = parseImporte(importeTexto);
        LocalDate fechaInicio;
        LocalDate fechaFin;

        if (anual) {
            int anio = parseAnio(anioTexto);
            fechaInicio = LocalDate.of(anio, 1, 1);
            fechaFin = LocalDate.of(anio, 12, 31);
        } else {
            fechaInicio = parseFecha(fechaInicioTexto, "La fecha de inicio");
            fechaFin = parseFecha(fechaFinTexto, "La fecha de fin");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new ApplicationException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        validarTipoExiste(tipoId);
        validarSolape(tipoId, fechaInicio, fechaFin);

        db.executeUpdate(
                "INSERT INTO Presupuestos(tipo, presupuesto, consumido, fecha_inicio, fecha_fin) VALUES (?,?,?,?,?)",
                Integer.valueOf(tipoId), importe.doubleValue(), Double.valueOf(0), fechaInicio.toString(), fechaFin.toString());
    }

    private void validarGestor(String identificacion) {
        IncidenciasModel incModel = new IncidenciasModel();
        UsuarioEntity usuario = incModel.findUsuario(identificacion);
        if (usuario == null || usuario.getRol() == null || usuario.getRol().intValue() != ROL_GESTOR_ECONOMICO) {
            throw new ApplicationException("Solo un gestor económico puede definir presupuestos.");
        }
    }

    private void validarTipoExiste(int tipoId) {
        List<Object[]> rows = db.executeQueryArray("SELECT id FROM Tipos WHERE id=?", Integer.valueOf(tipoId));
        if (rows == null || rows.isEmpty()) {
            throw new ApplicationException("El tipo de incidencia seleccionado no existe.");
        }
    }

    private BigDecimal parseImporte(String importeTexto) {
        String valor = importeTexto == null ? "" : importeTexto.trim().replace(',', '.');
        if (valor.isEmpty()) {
            throw new ApplicationException("El importe del presupuesto es obligatorio.");
        }
        try {
            BigDecimal importe = new BigDecimal(valor);
            if (importe.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApplicationException("El importe del presupuesto debe ser mayor que 0.");
            }
            return importe;
        } catch (NumberFormatException ex) {
            throw new ApplicationException("El importe del presupuesto no tiene un formato válido.");
        }
    }

    private int parseAnio(String anioTexto) {
        String valor = anioTexto == null ? "" : anioTexto.trim();
        if (valor.isEmpty()) {
            throw new ApplicationException("Debe indicar el año de vigencia.");
        }
        try {
            int anio = Integer.parseInt(valor);
            if (anio < 2000 || anio > 2100) {
                throw new ApplicationException("El año de vigencia debe estar entre 2000 y 2100.");
            }
            return anio;
        } catch (NumberFormatException ex) {
            throw new ApplicationException("El año de vigencia no es válido.");
        }
    }

    private LocalDate parseFecha(String texto, String campo) {
        String valor = texto == null ? "" : texto.trim();
        if (valor.isEmpty()) {
            throw new ApplicationException(campo + " es obligatoria.");
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ex) {
            throw new ApplicationException(campo + " debe tener formato YYYY-MM-DD.");
        }
    }

    private void validarSolape(int tipoId, LocalDate inicio, LocalDate fin) {
        String sql = "SELECT id, fecha_inicio, fecha_fin FROM Presupuestos "
                + "WHERE tipo=? AND date(fecha_inicio) <= date(?) AND date(fecha_fin) >= date(?)";
        List<Object[]> solapes = db.executeQueryArray(sql, Integer.valueOf(tipoId), fin.toString(), inicio.toString());
        if (solapes != null && !solapes.isEmpty()) {
            throw new ApplicationException("Ya existe un presupuesto vigente para ese tipo en el periodo indicado.");
        }
    }
}
