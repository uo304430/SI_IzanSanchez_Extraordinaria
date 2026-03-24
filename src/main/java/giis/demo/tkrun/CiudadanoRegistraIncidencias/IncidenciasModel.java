package giis.demo.tkrun.CiudadanoRegistraIncidencias;

import java.time.LocalDateTime;
import java.util.List;

import giis.demo.util.Database;
import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.tkrun.DTOs.UsuarioDTO;
import giis.demo.tkrun.Entities.TipoIncidenciaEntity;
import giis.demo.tkrun.Entities.UsuarioEntity;
import giis.demo.tkrun.Entities.ZonaEntity;
import giis.demo.util.ApplicationException;

/**
 * Modelo para registrar incidencias. Proporciona un método simple register(...) que:
 *  - valida campos obligatorios
 *  - busca el usuario por email o dni
 *  - inserta la incidencia en la tabla Incidencia y devuelve el id generado (SQLite)
 */
public class IncidenciasModel {
    private Database db = new Database();

    public IncidenciasModel() {
        // Asegurar que la tabla Usuarios y datos iniciales están presentes para el funcionamiento
        try {
            List<Object[]> tablesUsuarios = db.executeQueryArray("SELECT name FROM sqlite_master WHERE type='table' AND name='Usuarios'");
            List<Object[]> tablesZonas = db.executeQueryArray("SELECT name FROM sqlite_master WHERE type='table' AND name='Zonas'");
            boolean usuariosTableExists = (tablesUsuarios != null && !tablesUsuarios.isEmpty());
            boolean zonasTableExists = (tablesZonas != null && !tablesZonas.isEmpty());
            if (!usuariosTableExists || !zonasTableExists) {
                // crear esquema y cargar datos de ejemplo incluyendo la nueva tabla Zonas
                db.createDatabase(false);
                db.loadDatabase();
            } else {
                // si las tablas existen, comprobar si contiene filas; si no, cargar datos
                List<Object[]> count = db.executeQueryArray("SELECT count(*) FROM Usuarios");
                if (count != null && !count.isEmpty() && count.get(0)[0] != null) {
                    Number n = (Number) count.get(0)[0];
                    if (n.intValue() == 0) {
                        db.loadDatabase();
                    }
                }
            }
        } catch (Exception e) {
            // No frenar la inicialización por errores, pero registrar como excepción de aplicación
            // (la vista/controlador capturará fallos al interactuar con la BD)
            throw new ApplicationException("Error inicializando la base de datos: " + e.getMessage());
        }
    }

    /**
     * Busca usuario por email o dni. Lanza ApplicationException si no existe o si el identificador está vacío.
     */
    public UsuarioEntity findUsuario(String emailOrDni) {
        String id = emailOrDni == null ? "" : emailOrDni.trim();
        System.out.println("[IncidenciasModel.findUsuario] identificador recibido='" + emailOrDni + "' normalized='" + id + "'");
        if (id.isEmpty())
            throw new ApplicationException("Identificador vacío: debe indicar email o DNI del ciudadano.");

        // 1) Intento 1: búsqueda exacta por email o dni
        List<UsuarioEntity> usuarios = db.executeQueryPojo(UsuarioEntity.class,
                "SELECT * FROM Usuarios WHERE email=? OR dni=?", id, id);
        System.out.println("[IncidenciasModel.findUsuario] intento exacto resultados=" + (usuarios==null?0:usuarios.size()));
        if (usuarios != null && !usuarios.isEmpty())
            return usuarios.get(0);

        // 2) Intento 2: búsqueda insensible a mayúsculas (por si el usuario introduce mayúsculas/minúsculas distintas)
        usuarios = db.executeQueryPojo(UsuarioEntity.class,
                "SELECT * FROM Usuarios WHERE lower(email)=? OR upper(dni)=?", id.toLowerCase(), id.toUpperCase());
        System.out.println("[IncidenciasModel.findUsuario] intento case-insensitive resultados=" + (usuarios==null?0:usuarios.size()));
        if (usuarios != null && !usuarios.isEmpty())
            return usuarios.get(0);

        // 3) Intento 3: si el identificador es numérico, buscar por id
        try {
            int numericId = Integer.parseInt(id);
            usuarios = db.executeQueryPojo(UsuarioEntity.class, "SELECT * FROM Usuarios WHERE id=?", Integer.valueOf(numericId));
            System.out.println("[IncidenciasModel.findUsuario] intento por id resultados=" + (usuarios==null?0:usuarios.size()));
            if (usuarios != null && !usuarios.isEmpty())
                return usuarios.get(0);
        } catch (NumberFormatException ex) {
            // no es numérico, ignorar
            System.out.println("[IncidenciasModel.findUsuario] identificador no numérico para búsqueda por id");
        }

        throw new ApplicationException("Usuario no identificado: " + id + ". Asegúrese de usar el email o DNI registrados (por ejemplo: ana.lopez@example.com o 12345678A)");
    }

    /**
     * Valida que el tipo de incidencia exista.
     */
    public TipoIncidenciaEntity getTipo(int idTipo) {
        List<TipoIncidenciaEntity> tipos = db.executeQueryPojo(TipoIncidenciaEntity.class,
                "SELECT id,nombre FROM Tipos WHERE id=?", idTipo);
        if (tipos == null || tipos.isEmpty())
            throw new ApplicationException("Tipo de incidencia no encontrado: " + idTipo);
        return tipos.get(0);
    }

    /** Valida que la zona exista y la devuelve. */
    public ZonaEntity getZona(int idZona) {
        List<ZonaEntity> zonas = db.executeQueryPojo(ZonaEntity.class, "SELECT id,descripcion FROM Zonas WHERE id=?", idZona);
        if (zonas == null || zonas.isEmpty())
            throw new ApplicationException("Zona no encontrada: " + idZona);
        return zonas.get(0);
    }

    /**
     * Registra una incidencia y devuelve un DTO con los datos básicos.
    * Requisitos: usuario identificado; descripcion obligatoria; zona valida; tipo valido.
     * Asigna id nuevo calculando max(id)+1 y fecha actual (ISO).
     */
    public IncidenciaDTO registrarIncidencia(String emailOrDni, int idTipo, String descripcion, int idZona) {
    	
        UsuarioEntity usuario = findUsuario(emailOrDni);
    
        if (descripcion == null || descripcion.trim().isEmpty())
            throw new ApplicationException("La descripción es obligatoria");
        // validar zona y usar su id para persistir, pero conservar descripcion para el DTO
        ZonaEntity zona = getZona(idZona);

        getTipo(idTipo); // valida existencia

        // calcular siguiente id utilizando executeQueryArray (devuelve lista de Object[])
        List<Object[]> rows = db.executeQueryArray("SELECT max(id) FROM Incidencia");
        int nextId = 1;
        if (rows != null && !rows.isEmpty() && rows.get(0)[0] != null) {
            Number n = (Number) rows.get(0)[0];
            nextId = n.intValue() + 1;
        }

        // usar fecha/hora actual como LocalDateTime
        LocalDateTime fechaHora = LocalDateTime.now();

        // Insercion: asignamos tecnico null, coste 0, descr_reparación vacío, estado = 1 (Nueva), validación = false
        // Ajustar tipos para la tabla (SQLite acepta null y valores string/num según columnas)
        db.executeUpdate(
            "INSERT INTO Incidencia(id,tipo,descripcion,localizacion,usuario,tecnico,Coste,descr_reparación,fecha,estado,validación) VALUES (?,?,?,?,?,?,?,?,?,1,0)",
            Integer.valueOf(nextId), Integer.valueOf(idTipo), descripcion, Integer.valueOf(idZona), Integer.valueOf(usuario.getId()), null, "0", "", fechaHora.toString());

        // Construir UsuarioDTO a partir de UsuarioEntity para incluir nombre en el DTO
        UsuarioDTO ciudadano = new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getDni(), usuario.getRol());

        // Crear IncidenciaDTO usando constructor existente (id,tipo,descripcion,localizacion,ciudadano,fechaHora,estado)
        // El DTO mantiene la descripcion de la zona para mostrar en UI
        IncidenciaDTO dto = new IncidenciaDTO(Integer.valueOf(nextId), Integer.valueOf(idTipo), descripcion, zona.getId(), ciudadano, fechaHora, Integer.valueOf(1));

        return dto;
    }

    /**
     * Devuelve la lista de tipos de incidencia definidos en la base de datos.
     */
    public java.util.List<TipoIncidenciaEntity> getAllTipos() {
        return db.executeQueryPojo(TipoIncidenciaEntity.class, "SELECT id,nombre FROM Tipos");
    }

    /** Devuelve la lista de zonas disponibles. */
    public java.util.List<ZonaEntity> getAllZonas() {
        return db.executeQueryPojo(ZonaEntity.class, "SELECT id,descripcion FROM Zonas ORDER BY id");
    }
}
